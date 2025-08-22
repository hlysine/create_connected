package com.hlysine.create_connected.content.inventoryaccessport;

import com.hlysine.create_connected.CCBlockEntityTypes;
import com.hlysine.create_connected.CreateConnected;
import com.simibubi.create.content.redstone.DirectedDirectionalBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

import static com.hlysine.create_connected.content.inventoryaccessport.InventoryAccessPortBlock.ATTACHED;

@EventBusSubscriber(modid = CreateConnected.MODID, bus = EventBusSubscriber.Bus.MOD)
public class InventoryAccessPortBlockEntity extends SmartBlockEntity {
    protected IItemHandler itemCapability;
    private InvManipulationBehaviour observedInventory;
    private boolean powered;

    public InventoryAccessPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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
                CCBlockEntityTypes.INVENTORY_ACCESS_PORT.get(),
                (be, context) -> {
                    if (be.itemCapability == null)
                        be.refreshCapability();
                    return be.itemCapability;
                }
        );
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        CapManipulationBehaviourBase.InterfaceProvider towardBlockFacing =
                (w, p, s) -> new BlockFace(p, DirectedDirectionalBlock.getTargetDirection(s));
        behaviours.add(observedInventory = new InvManipulationBehaviour(this, towardBlockFacing));
    }

    public boolean isAttached() {
        return !powered && observedInventory.hasInventory() && !(observedInventory.getInventory() instanceof WrappedItemHandler);
    }

    public void updateConnectedInventory() {
        observedInventory.findNewCapability();
        boolean previouslyPowered = powered;
        assert level != null;
        powered = level.hasNeighborSignal(worldPosition);
        if (powered != previouslyPowered) {
            notifyUpdate();
        }
        if (isAttached() != getBlockState().getValue(ATTACHED)) {
            BlockState state = getBlockState().cycle(ATTACHED);
            level.setBlockAndUpdate(worldPosition, state);
        }
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

    private IItemHandler getConnectedItemHandler() {
        if (powered) return null;
        IItemHandler handler = observedInventory.getInventory();
        if (handler instanceof WrappedItemHandler) return null;
        return handler;
    }

    private void refreshCapability() {
        itemCapability = new InventoryAccessHandler();
        invalidateCapabilities();
    }

    private class InventoryAccessHandler implements WrappedItemHandler {

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
                IItemHandler handler = getConnectedItemHandler();
                return handler == null ? 0 : handler.getSlots();
            }, 0);
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int i) {
            return preventRecursion(() -> {
                IItemHandler handler = getConnectedItemHandler();
                return handler == null ? ItemStack.EMPTY : handler.getStackInSlot(i);
            }, ItemStack.EMPTY);
        }

        @Override
        public @NotNull ItemStack insertItem(int i, @NotNull ItemStack itemStack, boolean b) {
            return preventRecursion(() -> {
                IItemHandler handler = getConnectedItemHandler();
                return handler == null ? itemStack : handler.insertItem(i, itemStack, b);
            }, itemStack);
        }

        @Override
        public @NotNull ItemStack extractItem(int i, int i1, boolean b) {
            return preventRecursion(() -> {
                IItemHandler handler = getConnectedItemHandler();
                return handler == null ? ItemStack.EMPTY : handler.extractItem(i, i1, b);
            }, ItemStack.EMPTY);
        }

        @Override
        public int getSlotLimit(int i) {
            return preventRecursion(() -> {
                IItemHandler handler = getConnectedItemHandler();
                return handler == null ? 0 : handler.getSlotLimit(i);
            }, 0);
        }

        @Override
        public boolean isItemValid(int i, @NotNull ItemStack itemStack) {
            return preventRecursion(() -> {
                IItemHandler handler = getConnectedItemHandler();
                return handler != null && handler.isItemValid(i, itemStack);
            }, false);
        }
    }
}
