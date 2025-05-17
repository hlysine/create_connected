package com.hlysine.create_connected.content;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractBEShaftBlock<T extends KineticBlockEntity> extends RotatedPillarKineticBlock
        implements IBE<T>, ProperWaterloggedBlock {

    public AbstractBEShaftBlock(Properties properties) {
        super(properties);
        registerDefaultState(super.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(AXIS);
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }

    @Override
    public @NotNull FluidState getFluidState(@NotNull BlockState state) {
        return fluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(BlockStateProperties.WATERLOGGED));
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state,
                                           @NotNull Direction direction,
                                           @NotNull BlockState neighbourState,
                                           @NotNull LevelAccessor world,
                                           @NotNull BlockPos pos,
                                           @NotNull BlockPos neighbourPos) {
        updateWater(world, state, pos);
        return state;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return withWater(super.getStateForPlacement(context), context);
    }

}
