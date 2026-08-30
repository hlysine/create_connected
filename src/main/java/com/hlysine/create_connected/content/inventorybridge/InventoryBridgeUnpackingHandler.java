package com.hlysine.create_connected.content.inventorybridge;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.packager.unpacking.UnpackingHandler;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.impl.unpacking.CrafterUnpackingHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public enum InventoryBridgeUnpackingHandler implements UnpackingHandler {
    INSTANCE;

    @Override
    public boolean unpack(Level level, BlockPos pos, BlockState state, Direction side, List<ItemStack> items, @Nullable PackageOrderWithCrafts orderContext, boolean simulate) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof InventoryBridgeBlockEntity bridgeBE))
            return DEFAULT.unpack(level, pos, state, side, items, orderContext, simulate);

        Direction negativeTarget = InventoryBridgeBlock.getNegativeTarget(state);
        Direction positiveTarget = InventoryBridgeBlock.getPositiveTarget(state);
        BlockPos negativePos = pos.relative(negativeTarget);
        BlockPos positivePos = pos.relative(positiveTarget);
        BlockState negativeState = level.getBlockState(negativePos);
        BlockState positiveState = level.getBlockState(positivePos);

        if (negativeState.is(AllBlocks.MECHANICAL_CRAFTER)) {
            boolean filterPass = true;
            for (ItemStack item : items) {
                if (!bridgeBE.negativeFilter.test(item)) {
                    filterPass = false;
                    break;
                }
            }
            if (filterPass && CrafterUnpackingHandler.INSTANCE.unpack(level, negativePos, negativeState, negativeTarget, copyItems(items), orderContext, true))
                return CrafterUnpackingHandler.INSTANCE.unpack(level, negativePos, negativeState, negativeTarget, items, orderContext, simulate);
        }
        if (positiveState.is(AllBlocks.MECHANICAL_CRAFTER)) {
            boolean filterPass = true;
            for (ItemStack item : items) {
                if (!bridgeBE.positiveFilter.test(item)) {
                    filterPass = false;
                    break;
                }
            }
            if (filterPass && CrafterUnpackingHandler.INSTANCE.unpack(level, positivePos, positiveState, positiveTarget, copyItems(items), orderContext, true))
                return CrafterUnpackingHandler.INSTANCE.unpack(level, positivePos, positiveState, positiveTarget, items, orderContext, simulate);
        }
        return DEFAULT.unpack(level, pos, state, side, items, orderContext, simulate);
    }

    private List<ItemStack> copyItems(List<ItemStack> items) {
        List<ItemStack> copy = new ArrayList<>(items.size());
        for (ItemStack item : items) {
            copy.add(item.copy());
        }
        return copy;
    }
}
