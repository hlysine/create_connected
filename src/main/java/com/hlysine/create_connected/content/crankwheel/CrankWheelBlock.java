package com.hlysine.create_connected.content.crankwheel;

import com.hlysine.create_connected.CCBlockEntityTypes;
import com.hlysine.create_connected.CCShapes;
import com.simibubi.create.content.kinetics.crank.HandCrankBlock;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock.isValidCogwheelPosition;
import static net.minecraft.core.Direction.Axis;

public class CrankWheelBlock extends HandCrankBlock implements ICogWheel {
    // Required by rotation propagator
    public static EnumProperty<Axis> AXIS = CogWheelBlock.AXIS;
    public final boolean largeCog;

    public CrankWheelBlock(Properties properties, boolean largeCog) {
        super(properties);
        this.largeCog = largeCog;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return largeCog ? CCShapes.LARGE_CRANK_WHEEL.get(state.getValue(FACING)) : CCShapes.CRANK_WHEEL.get(state.getValue(FACING));
    }

    @Override
    public BlockEntityType<? extends CrankWheelBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.CRANK_WHEEL.get();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferred = getPreferredFacing(context);
        BlockState defaultBlockState = withWater(defaultBlockState(), context);
        if (preferred == null || (context.getPlayer() != null && context.getPlayer()
                .isShiftKeyDown()))
            return defaultBlockState.setValue(FACING, context.getClickedFace()).setValue(AXIS, context.getClickedFace().getAxis());
        Direction facing = Direction.fromAxisAndDirection(preferred.getAxis(), context.getNearestLookingDirection().getOpposite().getAxisDirection());
        return defaultBlockState
                .setValue(FACING, facing)
                .setValue(AXIS, facing.getAxis());
    }

    @Override
    public Direction getPreferredFacing(BlockPlaceContext context) {
        Direction preferredSide = null;
        for (Direction side : Iterate.directions) {
            BlockState blockState = context.getLevel()
                    .getBlockState(context.getClickedPos()
                            .relative(side));
            if (blockState.getBlock() instanceof ICogWheel cog) {
                Axis rotationAxis = cog.getRotationAxis(blockState);
                if (rotationAxis != side.getAxis())
                    if (preferredSide != null && preferredSide.getAxis() != rotationAxis) {
                        preferredSide = null;
                        break;
                    } else {
                        preferredSide = Direction.fromAxisAndDirection(rotationAxis, side.getAxisDirection());
                    }
            }
        }
        return preferredSide;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return isValidCogwheelPosition(ICogWheel.isLargeCog(state), worldIn, pos, state.getValue(AXIS));
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        BlockState newState = super.rotate(state, level, pos, direction);
        return newState.setValue(AXIS, newState.getValue(AXIS));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        BlockState newState = super.mirror(state, mirrorIn);
        return newState.setValue(AXIS, newState.getValue(AXIS));
    }

    @Override
    public boolean isLargeCog() {
        return largeCog;
    }

    @Override
    public boolean isSmallCog() {
        return !largeCog;
    }

    public static class Small extends CrankWheelBlock {
        public Small(Properties properties) {
            super(properties, false);
        }
    }

    public static class Large extends CrankWheelBlock {
        public Large(Properties properties) {
            super(properties, true);
        }
    }
}
