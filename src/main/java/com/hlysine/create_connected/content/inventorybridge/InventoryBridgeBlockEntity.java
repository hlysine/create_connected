package com.hlysine.create_connected.content.inventorybridge;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.content.inventoryaccessport.WrappedItemHandler;
import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.simibubi.create.api.packager.InventoryIdentifier;
import com.simibubi.create.content.logistics.packager.IdentifiedInventory;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.SidedFilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryWrapper;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

import static com.hlysine.create_connected.content.inventorybridge.InventoryBridgeBlock.ATTACHED_NEGATIVE;
import static com.hlysine.create_connected.content.inventorybridge.InventoryBridgeBlock.ATTACHED_POSITIVE;

@EventBusSubscriber(modid = CreateConnected.MODID)
public class InventoryBridgeBlockEntity extends SmartBlockEntity {
    protected IItemHandler itemCapability;
    private InvManipulationBehaviour negativeInventory;
    private InvManipulationBehaviour positiveInventory;

    SidedFilteringBehaviour filters;
    public FilteringBehaviour negativeFilter;
    public FilteringBehaviour positiveFilter;

    private boolean powered;

    private boolean handlerDirty = true;

    public InventoryBridgeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        itemCapability = null;
        powered = false;
    }

    @Override
    public void initialize() {
        super.initialize();
        updateConnectedInventory();
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CCBlockEntityTypes.INVENTORY_BRIDGE.get(),
                (be, context) -> be.refreshCapability()
        );
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

    public @Nullable BlockState getNegativeAttachedBlock() {
        if (!isAttachedNegative()) return null;
        return level.getBlockState(negativeInventory.getTarget().getConnectedPos());
    }

    public @Nullable BlockState getPositiveAttachedBlock() {
        if (!isAttachedPositive()) return null;
        return level.getBlockState(positiveInventory.getTarget().getConnectedPos());
    }

    public void updateConnectedInventory() {
        handlerDirty = true;
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

    @Nullable
    public InventoryIdentifier getInventoryId() {
        // best we can do is identify as one of the two connected inventory
        // not currently possible to completely dedupe inventory contents in stock networks
        IdentifiedInventory inv = negativeInventory.getIdentifiedInventory();
        return inv == null ? null : inv.identifier();
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        powered = tag.getBoolean("Powered");
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putBoolean("Powered", powered);
    }

    private IItemHandler getNegativeHandler() {
        if (powered) return null;
        return negativeInventory.getInventory();
    }

    private IItemHandler getPositiveHandler() {
        if (powered) return null;
        return positiveInventory.getInventory();
    }

    private IItemHandler refreshCapability() {
        if (handlerDirty || itemCapability == null) {
            if (getLevel().isClientSide()) {
                itemCapability = new InventoryBridgeHandler(null, null);
            } else {
                IItemHandler negativeHandler = getNegativeHandler();
                IItemHandler positiveHandler = getPositiveHandler();
                if (negativeHandler instanceof VersionedInventoryWrapper negativeVersioned && positiveHandler instanceof VersionedInventoryWrapper positiveVersioned) {
                    itemCapability = new VersionedInventoryBridgeHandler(negativeVersioned, positiveVersioned);
                } else if (negativeHandler instanceof IItemHandlerModifiable negativeModifiable && positiveHandler instanceof IItemHandlerModifiable positiveModifiable) {
                    itemCapability = new ModifiableInventoryBridgeHandler(negativeModifiable, positiveModifiable);
                } else {
                    itemCapability = new InventoryBridgeHandler(negativeHandler, positiveHandler);
                }
            }
            invalidateCapabilities();
            handlerDirty = false;
        }
        return itemCapability;
    }

    private static boolean inRecursion = false;

    private static <T> T preventRecursion(Supplier<T> value, T defaultValue) {
        if (inRecursion) return defaultValue;
        inRecursion = true;
        try {
            return value.get();
        } finally {
            inRecursion = false;
        }
    }

    private int getSlots(IItemHandler negativeHandler, IItemHandler positiveHandler) {
        return preventRecursion(() -> {
            if (negativeHandler == null && positiveHandler == null) {
                return 0;
            } else if (negativeHandler == null) {
                return positiveHandler.getSlots();
            } else if (positiveHandler == null) {
                return negativeHandler.getSlots();
            } else {
                return negativeHandler.getSlots() + positiveHandler.getSlots();
            }
        }, 0);
    }

    private @NotNull ItemStack getStackInSlot(IItemHandler negativeHandler, IItemHandler positiveHandler, int slot) {
        return preventRecursion(() -> {
            if (negativeHandler == null && positiveHandler == null) {
                return ItemStack.EMPTY;
            } else if (negativeHandler == null) {
                ItemStack stack = positiveHandler.getStackInSlot(slot);
                boolean negative = negativeFilter.test(stack);
                boolean positive = positiveFilter.test(stack);
                if (!positive) return ItemStack.EMPTY;
                if (negative && !negativeFilter.getFilter().isEmpty() && positiveFilter.getFilter().isEmpty())
                    return ItemStack.EMPTY;
                return stack;
            } else if (positiveHandler == null) {
                ItemStack stack = negativeHandler.getStackInSlot(slot);
                boolean negative = negativeFilter.test(stack);
                boolean positive = positiveFilter.test(stack);
                if (!negative) return ItemStack.EMPTY;
                if (positive && !positiveFilter.getFilter().isEmpty() && negativeFilter.getFilter().isEmpty())
                    return ItemStack.EMPTY;
                return stack;
            } else {
                int size1 = negativeHandler.getSlots();
                ItemStack stack = slot < size1 ? negativeHandler.getStackInSlot(slot) : positiveHandler.getStackInSlot(slot - size1);
                boolean negative = negativeFilter.test(stack);
                boolean positive = positiveFilter.test(stack);
                if (!negative && !positive) return ItemStack.EMPTY;
                if (negative && !positive && slot >= size1) return ItemStack.EMPTY;
                if (positive && !negative && slot < size1) return ItemStack.EMPTY;
                boolean negativeFilterEmpty = negativeFilter.getFilter().isEmpty();
                boolean positiveFilterEmpty = positiveFilter.getFilter().isEmpty();
                if (!negativeFilterEmpty || !positiveFilterEmpty) {
                    if (slot >= size1 && negative && positiveFilterEmpty) return ItemStack.EMPTY;
                    if (slot < size1 && positive && negativeFilterEmpty) return ItemStack.EMPTY;
                }
                return stack;
            }
        }, ItemStack.EMPTY);
    }

    private @NotNull ItemStack insertItem(IItemHandler negativeHandler, IItemHandler positiveHandler, int slot, @NotNull ItemStack stack, boolean simulate) {
        return preventRecursion(() -> {
            if (negativeHandler == null && positiveHandler == null) {
                return stack;
            } else if (negativeHandler == null) {
                boolean negative = negativeFilter.test(stack);
                boolean positive = positiveFilter.test(stack);
                if (!positive) return stack;
                if (negative && !negativeFilter.getFilter().isEmpty() && positiveFilter.getFilter().isEmpty())
                    return stack;
                return positiveHandler.insertItem(slot, stack, simulate);
            } else if (positiveHandler == null) {
                boolean negative = negativeFilter.test(stack);
                boolean positive = positiveFilter.test(stack);
                if (!negative) return stack;
                if (positive && !positiveFilter.getFilter().isEmpty() && negativeFilter.getFilter().isEmpty())
                    return stack;
                return negativeHandler.insertItem(slot, stack, simulate);
            } else {
                boolean negative = negativeFilter.test(stack);
                boolean positive = positiveFilter.test(stack);
                int size1 = negativeHandler.getSlots();
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
                        ? negativeHandler.insertItem(slot, stack, simulate)
                        : positiveHandler.insertItem(slot - size1, stack, simulate);
            }
        }, stack);
    }

    private @NotNull ItemStack extractItem(IItemHandler negativeHandler, IItemHandler positiveHandler, int slot, int amount, boolean simulate) {
        return preventRecursion(() -> {
            if (negativeHandler == null && positiveHandler == null) {
                return ItemStack.EMPTY;
            } else if (negativeHandler == null) {
                ItemStack stack = positiveHandler.extractItem(slot, amount, true);
                boolean negative = negativeFilter.test(stack);
                boolean positive = positiveFilter.test(stack);
                if (!positive) return ItemStack.EMPTY;
                if (negative && !negativeFilter.getFilter().isEmpty() && positiveFilter.getFilter().isEmpty())
                    return ItemStack.EMPTY;
                return positiveHandler.extractItem(slot, amount, simulate);
            } else if (positiveHandler == null) {
                ItemStack stack = negativeHandler.extractItem(slot, amount, true);
                boolean negative = negativeFilter.test(stack);
                boolean positive = positiveFilter.test(stack);
                if (!negative) return ItemStack.EMPTY;
                if (positive && !positiveFilter.getFilter().isEmpty() && negativeFilter.getFilter().isEmpty())
                    return ItemStack.EMPTY;
                return negativeHandler.extractItem(slot, amount, simulate);
            } else {
                int size1 = negativeHandler.getSlots();
                ItemStack stack = slot < size1 ? negativeHandler.extractItem(slot, amount, true) : positiveHandler.extractItem(slot - size1, amount, true);
                boolean negative = negativeFilter.test(stack);
                boolean positive = positiveFilter.test(stack);
                if (!negative && !positive) return ItemStack.EMPTY;
                if (negative && !positive && slot >= size1) return ItemStack.EMPTY;
                if (positive && !negative && slot < size1) return ItemStack.EMPTY;
                boolean negativeFilterEmpty = negativeFilter.getFilter().isEmpty();
                boolean positiveFilterEmpty = positiveFilter.getFilter().isEmpty();
                if (!negativeFilterEmpty || !positiveFilterEmpty) {
                    if (slot >= size1 && negative && positiveFilterEmpty) return ItemStack.EMPTY;
                    if (slot < size1 && positive && negativeFilterEmpty) return ItemStack.EMPTY;
                }
                return slot < size1 ? negativeHandler.extractItem(slot, amount, simulate) : positiveHandler.extractItem(slot - size1, amount, simulate);
            }
        }, ItemStack.EMPTY);
    }

    private int getSlotLimit(IItemHandler negativeHandler, IItemHandler positiveHandler, int slot) {
        return preventRecursion(() -> {
            if (negativeHandler == null && positiveHandler == null) {
                return 0;
            } else if (negativeHandler == null) {
                return positiveHandler.getSlotLimit(slot);
            } else if (positiveHandler == null) {
                return negativeHandler.getSlotLimit(slot);
            } else {
                int size1 = negativeHandler.getSlots();
                return slot < size1 ? negativeHandler.getSlotLimit(slot) : positiveHandler.getSlotLimit(slot - size1);
            }
        }, 0);
    }

    private boolean isItemValid(IItemHandler negativeHandler, IItemHandler positiveHandler, int slot, @NotNull ItemStack stack) {
        return preventRecursion(() -> {
            if (negativeHandler == null && positiveHandler == null) {
                return false;
            } else if (negativeHandler == null) {
                boolean negative = negativeFilter.test(stack);
                boolean positive = positiveFilter.test(stack);
                if (!positive) return false;
                if (negative && !negativeFilter.getFilter().isEmpty() && positiveFilter.getFilter().isEmpty())
                    return false;
                return positiveHandler.isItemValid(slot, stack);
            } else if (positiveHandler == null) {
                boolean negative = negativeFilter.test(stack);
                boolean positive = positiveFilter.test(stack);
                if (!negative) return false;
                if (positive && !positiveFilter.getFilter().isEmpty() && negativeFilter.getFilter().isEmpty())
                    return false;
                return negativeHandler.isItemValid(slot, stack);
            } else {
                boolean negative = negativeFilter.test(stack);
                boolean positive = positiveFilter.test(stack);
                int size1 = negativeHandler.getSlots();
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
                        ? negativeHandler.isItemValid(slot, stack)
                        : positiveHandler.isItemValid(slot - size1, stack);
            }
        }, false);
    }

    private void setStackInSlot(IItemHandlerModifiable negativeHandler, IItemHandlerModifiable positiveHandler, int slot, @NotNull ItemStack stack) {
        preventRecursion(() -> {
            if (negativeHandler == null && positiveHandler == null) {
                // do nothing
            } else if (negativeHandler == null) {
                positiveHandler.setStackInSlot(slot, stack);
            } else if (positiveHandler == null) {
                negativeHandler.setStackInSlot(slot, stack);
            } else {
                int size1 = negativeHandler.getSlots();
                if (slot < size1) {
                    negativeHandler.setStackInSlot(slot, stack);
                } else {
                    positiveHandler.setStackInSlot(slot - size1, stack);
                }
            }
            return 0;
        }, 0);
    }

    private class VersionedInventoryBridgeHandler extends VersionedInventoryWrapper implements WrappedItemHandler {

        private final VersionedInventoryWrapper negativeHandler;
        private final VersionedInventoryWrapper positiveHandler;

        public VersionedInventoryBridgeHandler(VersionedInventoryWrapper negativeHandler, VersionedInventoryWrapper positiveHandler) {
            super(negativeHandler);
            this.negativeHandler = negativeHandler;
            this.positiveHandler = positiveHandler;
        }

        @Override
        public void incrementVersion() {
            if (positiveHandler != null) positiveHandler.incrementVersion();
            if (negativeHandler != null) negativeHandler.incrementVersion();
        }

        @Override
        public int getVersion() {
            return positiveHandler.getVersion() + negativeHandler.getVersion();
        }

        @Override
        public int getId() {
            return -positiveHandler.getId() - negativeHandler.getId();
        }

        @Override
        public int getSlots() {
            return InventoryBridgeBlockEntity.this.getSlots(negativeHandler, positiveHandler);
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return InventoryBridgeBlockEntity.this.getStackInSlot(negativeHandler, positiveHandler, slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return InventoryBridgeBlockEntity.this.insertItem(negativeHandler, positiveHandler, slot, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return InventoryBridgeBlockEntity.this.extractItem(negativeHandler, positiveHandler, slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return InventoryBridgeBlockEntity.this.getSlotLimit(negativeHandler, positiveHandler, slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return InventoryBridgeBlockEntity.this.isItemValid(negativeHandler, positiveHandler, slot, stack);
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            InventoryBridgeBlockEntity.this.setStackInSlot(negativeHandler, positiveHandler, slot, stack);
        }
    }

    private class ModifiableInventoryBridgeHandler implements IItemHandlerModifiable, WrappedItemHandler {

        private final IItemHandlerModifiable negativeHandler;
        private final IItemHandlerModifiable positiveHandler;

        public ModifiableInventoryBridgeHandler(IItemHandlerModifiable negativeHandler, IItemHandlerModifiable positiveHandler) {
            this.negativeHandler = negativeHandler;
            this.positiveHandler = positiveHandler;
        }

        @Override
        public int getSlots() {
            return InventoryBridgeBlockEntity.this.getSlots(negativeHandler, positiveHandler);
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return InventoryBridgeBlockEntity.this.getStackInSlot(negativeHandler, positiveHandler, slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return InventoryBridgeBlockEntity.this.insertItem(negativeHandler, positiveHandler, slot, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return InventoryBridgeBlockEntity.this.extractItem(negativeHandler, positiveHandler, slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return InventoryBridgeBlockEntity.this.getSlotLimit(negativeHandler, positiveHandler, slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return InventoryBridgeBlockEntity.this.isItemValid(negativeHandler, positiveHandler, slot, stack);
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            InventoryBridgeBlockEntity.this.setStackInSlot(negativeHandler, positiveHandler, slot, stack);
        }
    }

    private class InventoryBridgeHandler implements WrappedItemHandler {

        private final IItemHandler negativeHandler;
        private final IItemHandler positiveHandler;

        public InventoryBridgeHandler(IItemHandler negativeHandler, IItemHandler positiveHandler) {
            this.negativeHandler = negativeHandler;
            this.positiveHandler = positiveHandler;
        }

        @Override
        public int getSlots() {
            return InventoryBridgeBlockEntity.this.getSlots(negativeHandler, positiveHandler);
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return InventoryBridgeBlockEntity.this.getStackInSlot(negativeHandler, positiveHandler, slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return InventoryBridgeBlockEntity.this.insertItem(negativeHandler, positiveHandler, slot, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return InventoryBridgeBlockEntity.this.extractItem(negativeHandler, positiveHandler, slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return InventoryBridgeBlockEntity.this.getSlotLimit(negativeHandler, positiveHandler, slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return InventoryBridgeBlockEntity.this.isItemValid(negativeHandler, positiveHandler, slot, stack);
        }
    }
}
