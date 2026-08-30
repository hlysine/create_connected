package com.hlysine.create_connected.content.inventoryaccessport;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.packager.unpacking.UnpackingHandler;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.impl.unpacking.CrafterUnpackingHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public enum InventoryAccessPortUnpackingHandler implements UnpackingHandler {
    INSTANCE;

    @Override
    public boolean unpack(Level level, BlockPos pos, BlockState state, Direction side, List<ItemStack> items, @Nullable PackageOrderWithCrafts orderContext, boolean simulate) {
        if (!(state.getBlock() instanceof InventoryAccessPortBlock))
            return DEFAULT.unpack(level, pos, state, side, items, orderContext, simulate);

        Direction targetDirection = InventoryAccessPortBlock.getTargetDirection(state);
        BlockPos targetPos = pos.relative(targetDirection);
        BlockState targetState = level.getBlockState(targetPos);

        if (targetState.is(AllBlocks.MECHANICAL_CRAFTER)) {
            return CrafterUnpackingHandler.INSTANCE.unpack(level, targetPos, targetState, targetDirection, items, orderContext, simulate);
        } else {
            return DEFAULT.unpack(level, pos, state, side, items, orderContext, simulate);
        }
    }
}
