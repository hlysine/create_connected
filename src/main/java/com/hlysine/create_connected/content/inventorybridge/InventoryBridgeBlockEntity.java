package com.hlysine.create_connected.content.inventorybridge;
import com.hlysine.create_connected.content.inventoryaccessport.WrappedItemHandler;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.SidedFilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

import static com.hlysine.create_connected.content.inventorybridge.InventoryBridgeBlock.ATTACHED_NEGATIVE;
import static com.hlysine.create_connected.content.inventorybridge.InventoryBridgeBlock.ATTACHED_POSITIVE;

public class InventoryBridgeBlockEntity extends SmartBlockEntity {
    protected LazyOptional<IItemHandler> itemCapability;
    private InvManipulationBehaviour negativeInventory;
    private InvManipulationBehaviour positiveInventory;

    SidedFilteringBehaviour filters;
    public FilteringBehaviour negativeFilter;
    public FilteringBehaviour positiveFilter;

    private boolean powered;

    public InventoryBridgeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        itemCapability = LazyOptional.empty();
        powered = false;
    }

    @Override
    public void initialize() {
        super.initialize();
        updateConnectedInventory();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        CapManipulationBehaviourBase.InterfaceProvider towardBlockFacing1 =
                (w, p, s) -> new BlockFace(p, InventoryBridgeBlock.getNegativeTarget(s));
        CapManipulationBehaviourBase.InterfaceProvider towardBlockFacing2 =
                (w, p, s) -> new BlockFace(p, InventoryBridgeBlock.getPositiveTarget(s));
        behaviours.add(negativeInventory = new InvManipulationBehaviour(this, towardBlockFacing1));
        behaviours.add(positiveInventory = new InvManipulationBehaviour(this, towardBlockFacing2));
        behaviours.add(filters = new SidedFilteringBehaviour(
                this,
                new InventoryBridgeFilterSlot(),
                (facing, filter) -> {
                    if (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
                        negativeFilter = filter;
                    } else {
                        positiveFilter = filter;
                    }
                    return filter;
                },
                facing -> facing.getAxis() == getBlockState().getValue(InventoryBridgeBlock.AXIS)
        ));
    }

    public boolean isAttachedNegative() {
        return !powered && negativeInventory.hasInventory() && !(negativeInventory.getInventory() instanceof WrappedItemHandler);
    }

    public boolean isAttachedPositive() {
        return !powered && positiveInventory.hasInventory() && !(positiveInventory.getInventory() instanceof WrappedItemHandler);
    }

    public void updateConnectedInventory() {
        negativeInventory.findNewCapability();
        positiveInventory.findNewCapability();
        boolean previouslyPowered = powered;
        powered = level.hasNeighborSignal(worldPosition);
        if (powered != previouslyPowered) {
            notifyUpdate();
        }
        boolean attachedNegative = isAttachedNegative();
        boolean attachedPositive = isAttachedPositive();
        if (attachedNegative != getBlockState().getValue(ATTACHED_NEGATIVE) || attachedPositive != getBlockState().getValue(ATTACHED_POSITIVE)) {
            BlockState state = getBlockState()
                    .setValue(ATTACHED_NEGATIVE, attachedNegative)
                    .setValue(ATTACHED_POSITIVE, attachedPositive);
            level.setBlockAndUpdate(worldPosition, state);
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (isItemHandlerCap(cap)) {
            initCapability();
            return itemCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        powered = compound.getBoolean("Powered");
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putBoolean("Powered", powered);
    }

    private IItemHandler getNegativeHandler() {
        if (powered) return null;
        IItemHandler handler = negativeInventory.getInventory();
        if (handler instanceof WrappedItemHandler) return null;
        return handler;
    }

    private IItemHandler getPositiveHandler() {
        if (powered) return null;
        IItemHandler handler = positiveInventory.getInventory();
        if (handler instanceof WrappedItemHandler) return null;
        return handler;
    }

    private void initCapability() {
        itemCapability = LazyOptional.of(InventoryBridgeHandler::new);
    }

    private class InventoryBridgeHandler implements WrappedItemHandler {

        private final ThreadLocal<Boolean> recursionGuard = ThreadLocal.withInitial(() -> false);

        private <T> T preventRecursion(Supplier<T> value, T defaultValue) {
            if (recursionGuard.get()) return defaultValue;
            recursionGuard.set(true);
            T result = value.get();
            recursionGuard.set(false);
            return result;
        }

        @Override
        public int getSlots() {
            return preventRecursion(() -> {
                IItemHandler handler1 = getNegativeHandler();
                IItemHandler handler2 = getPositiveHandler();
                if (handler1 == null && handler2 == null) {
                    return 0;
                } else if (handler1 == null) {
                    return handler2.getSlots();
                } else if (handler2 == null) {
                    return handler1.getSlots();
                } else {
                    return handler1.getSlots() + handler2.getSlots();
                }
            }, 0);
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return preventRecursion(() -> {
                IItemHandler handler1 = getNegativeHandler();
                IItemHandler handler2 = getPositiveHandler();
                if (handler1 == null && handler2 == null) {
                    return ItemStack.EMPTY;
                } else if (handler1 == null) {
                    return handler2.getStackInSlot(slot);
                } else if (handler2 == null) {
                    return handler1.getStackInSlot(slot);
                } else {
                    int size1 = handler1.getSlots();
                    return slot < size1 ? handler1.getStackInSlot(slot) : handler2.getStackInSlot(slot - size1);
                }
            }, ItemStack.EMPTY);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return preventRecursion(() -> {
                IItemHandler handler1 = getNegativeHandler();
                IItemHandler handler2 = getPositiveHandler();
                if (handler1 == null && handler2 == null) {
                    return stack;
                } else if (handler1 == null) {
                    boolean negative = negativeFilter.test(stack);
                    boolean positive = positiveFilter.test(stack);
                    if (!positive) return stack;
                    if (negative && !negativeFilter.getFilter().isEmpty() && positiveFilter.getFilter().isEmpty())
                        return stack;
                    return handler2.insertItem(slot, stack, simulate);
                } else if (handler2 == null) {
                    boolean negative = negativeFilter.test(stack);
                    boolean positive = positiveFilter.test(stack);
                    if (!negative) return stack;
                    if (positive && !positiveFilter.getFilter().isEmpty() && negativeFilter.getFilter().isEmpty())
                        return stack;
                    return handler1.insertItem(slot, stack, simulate);
                } else {
                    boolean negative = negativeFilter.test(stack);
                    boolean positive = positiveFilter.test(stack);
                    int size1 = handler1.getSlots();
                    if (!negative && !positive) return stack;
                    if (negative && !positive && slot >= size1) return stack;
                    if (positive && !negative && slot < size1) return stack;
                    boolean negativeFilterEmpty = negativeFilter.getFilter().isEmpty();
                    boolean positiveFilterEmpty = positiveFilter.getFilter().isEmpty();
                    if (!negativeFilterEmpty || !positiveFilterEmpty) {
                        if (slot >= size1 && negative && positiveFilterEmpty) return stack;
                        if (slot < size1 && positive && negativeFilterEmpty) return stack;
                    }
                    return slot < size1
                            ? handler1.insertItem(slot, stack, simulate)
                            : handler2.insertItem(slot - size1, stack, simulate);
                }
            }, stack);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return preventRecursion(() -> {
                IItemHandler handler1 = getNegativeHandler();
                IItemHandler handler2 = getPositiveHandler();
                if (handler1 == null && handler2 == null) {
                    return ItemStack.EMPTY;
                } else if (handler1 == null) {
                    return handler2.extractItem(slot, amount, simulate);
                } else if (handler2 == null) {
                    return handler1.extractItem(slot, amount, simulate);
                } else {
                    int size1 = handler1.getSlots();
                    return slot < size1 ? handler1.extractItem(slot, amount, simulate) : handler2.extractItem(slot - size1, amount, simulate);
                }
            }, ItemStack.EMPTY);
        }

        @Override
        public int getSlotLimit(int slot) {
            return preventRecursion(() -> {
                IItemHandler handler1 = getNegativeHandler();
                IItemHandler handler2 = getPositiveHandler();
                if (handler1 == null && handler2 == null) {
                    return 0;
                } else if (handler1 == null) {
                    return handler2.getSlotLimit(slot);
                } else if (handler2 == null) {
                    return handler1.getSlotLimit(slot);
                } else {
                    int size1 = handler1.getSlots();
                    return slot < size1 ? handler1.getSlotLimit(slot) : handler2.getSlotLimit(slot - size1);
                }
            }, 0);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return preventRecursion(() -> {
                IItemHandler handler1 = getNegativeHandler();
                IItemHandler handler2 = getPositiveHandler();
                if (handler1 == null && handler2 == null) {
                    return false;
                } else if (handler1 == null) {
                    boolean negative = negativeFilter.test(stack);
                    boolean positive = positiveFilter.test(stack);
                    if (!positive) return false;
                    if (negative && !negativeFilter.getFilter().isEmpty() && positiveFilter.getFilter().isEmpty())
                        return false;
                    return handler2.isItemValid(slot, stack);
                } else if (handler2 == null) {
                    boolean negative = negativeFilter.test(stack);
                    boolean positive = positiveFilter.test(stack);
                    if (!negative) return false;
                    if (positive && !positiveFilter.getFilter().isEmpty() && negativeFilter.getFilter().isEmpty())
                        return false;
                    return handler1.isItemValid(slot, stack);
                } else {
                    boolean negative = negativeFilter.test(stack);
                    boolean positive = positiveFilter.test(stack);
                    int size1 = handler1.getSlots();
                    if (!negative && !positive) return false;
                    if (negative && !positive && slot >= size1) return false;
                    if (positive && !negative && slot < size1) return false;
                    boolean negativeFilterEmpty = negativeFilter.getFilter().isEmpty();
                    boolean positiveFilterEmpty = positiveFilter.getFilter().isEmpty();
                    if (!negativeFilterEmpty || !positiveFilterEmpty) {
                        if (slot >= size1 && negative && positiveFilterEmpty) return false;
                        if (slot < size1 && positive && negativeFilterEmpty) return false;
                    }
                    return slot < size1
                            ? handler1.isItemValid(slot, stack)
                            : handler2.isItemValid(slot - size1, stack);
                }
            }, false);
        }
    }
}
