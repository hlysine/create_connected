package com.hlysine.create_connected.content.inventoryaccessport;

import com.simibubi.create.content.redstone.DirectedDirectionalBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.utility.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.hlysine.create_connected.content.inventoryaccessport.InventoryAccessPortBlock.*;

public class InventoryAccessPortBlockEntity extends SmartBlockEntity {
    protected LazyOptional<IItemHandler> itemCapability;
    private InvManipulationBehaviour observedInventory;

    public InventoryAccessPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        itemCapability = LazyOptional.empty();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        CapManipulationBehaviourBase.InterfaceProvider towardBlockFacing =
                (w, p, s) -> new BlockFace(p, DirectedDirectionalBlock.getTargetDirection(s));
        behaviours.add(observedInventory = new InvManipulationBehaviour(this, towardBlockFacing));
    }

    public boolean isAttached() {
        return observedInventory.hasInventory() && !(observedInventory.getInventory() instanceof InventoryAccessHandler);
    }

    public void updateConnectedInventory() {
        observedInventory.findNewCapability();
        if (isAttached() != getBlockState().getValue(ATTACHED)) {
            BlockState state = getBlockState().cycle(ATTACHED);
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

    private IItemHandler getConnectedItemHandler() {
        return observedInventory.getInventory();
    }

    private void initCapability() {
        itemCapability = LazyOptional.of(InventoryAccessHandler::new);
    }

    private class InventoryAccessHandler implements IItemHandler {
        @Override
        public int getSlots() {
            IItemHandler handler = getConnectedItemHandler();
            return handler == null ? 0 : handler.getSlots();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int i) {
            IItemHandler handler = getConnectedItemHandler();
            return handler == null ? ItemStack.EMPTY : handler.getStackInSlot(i);
        }

        @Override
        public @NotNull ItemStack insertItem(int i, @NotNull ItemStack itemStack, boolean b) {
            IItemHandler handler = getConnectedItemHandler();
            return handler == null ? itemStack : handler.insertItem(i, itemStack, b);
        }

        @Override
        public @NotNull ItemStack extractItem(int i, int i1, boolean b) {
            IItemHandler handler = getConnectedItemHandler();
            return handler == null ? ItemStack.EMPTY : handler.extractItem(i, i1, b);
        }

        @Override
        public int getSlotLimit(int i) {
            IItemHandler handler = getConnectedItemHandler();
            return handler == null ? 0 : handler.getSlotLimit(i);
        }

        @Override
        public boolean isItemValid(int i, @NotNull ItemStack itemStack) {
            IItemHandler handler = getConnectedItemHandler();
            return handler != null && handler.isItemValid(i, itemStack);
        }
    }
}
