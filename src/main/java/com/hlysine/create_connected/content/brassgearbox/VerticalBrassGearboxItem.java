package com.hlysine.create_connected.content.brassgearbox;

import com.hlysine.create_connected.CCBlocks;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class VerticalBrassGearboxItem extends BlockItem {

    public VerticalBrassGearboxItem(Properties builder) {
        super(CCBlocks.BRASS_GEARBOX.get(), builder);
    }

    @Override
    public String getDescriptionId() {
        return "item.create_connected.vertical_brass_gearbox";
    }

    @Override
    public void fillItemCategory(@NotNull CreativeModeTab pTab, @NotNull NonNullList<ItemStack> pStack) {
    }

    @Override
    public void registerBlocks(Map<Block, Item> p_195946_1_, Item p_195946_2_) {
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level world, Player player, ItemStack stack, BlockState state) {
        Direction.Axis prefferedAxis = null;
        for (Direction side : Iterate.horizontalDirections) {
            BlockState blockState = world.getBlockState(pos.relative(side));
            if (blockState.getBlock() instanceof IRotate) {
                if (((IRotate) blockState.getBlock()).hasShaftTowards(world, pos.relative(side), blockState,
                        side.getOpposite()))
                    if (prefferedAxis != null && prefferedAxis != side.getAxis()) {
                        prefferedAxis = null;
                        break;
                    } else {
                        prefferedAxis = side.getAxis();
                    }
            }
        }

        Direction.Axis axis = prefferedAxis == null ? player.getDirection()
                .getClockWise()
                .getAxis() : prefferedAxis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.AXIS, axis));
        return super.updateCustomBlockEntityTag(pos, world, player, stack, state);
    }

}
