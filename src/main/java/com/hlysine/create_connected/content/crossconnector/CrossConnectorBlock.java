package com.hlysine.create_connected.content.crossconnector;

import com.hlysine.create_connected.content.IConnectionForwardingBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CrossConnectorBlock extends Block implements IWrenchable, IConnectionForwardingBlock, IRotate {

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

    public CrossConnectorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(AXIS, Direction.Axis.Y));
    }

    @Override
    public @NotNull BlockState rotate(@NotNull BlockState state, Rotation rot) {
        return switch (rot) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch (state.getValue(AXIS)) {
                case X -> state.setValue(AXIS, Direction.Axis.Z);
                case Z -> state.setValue(AXIS, Direction.Axis.X);
                default -> state;
            };
            default -> state;
        };
    }

    public static Direction.Axis getPreferredAxis(BlockPlaceContext context) {
        Direction.Axis prefferedAxis = null;
        for (Direction side : Iterate.directions) {
            BlockState blockState = context.getLevel()
                    .getBlockState(context.getClickedPos()
                            .relative(side));
            if (blockState.getBlock() instanceof IRotate) {
                if (((IRotate) blockState.getBlock()).hasShaftTowards(context.getLevel(), context.getClickedPos()
                        .relative(side), blockState, side.getOpposite()))
                    if (prefferedAxis != null && prefferedAxis != side.getAxis()) {
                        prefferedAxis = null;
                        break;
                    } else {
                        prefferedAxis = side.getAxis();
                    }
            }
        }
        return prefferedAxis;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        Direction.Axis preferredAxis = getPreferredAxis(context);
        if (preferredAxis != null && (context.getPlayer() == null || !context.getPlayer().isShiftKeyDown())) {
            List<Direction.Axis> axes = new ArrayList<>();
            axes.add(Direction.Axis.X);
            axes.add(Direction.Axis.Y);
            axes.add(Direction.Axis.Z);
            axes.remove(preferredAxis);
            axes.remove(context.getNearestLookingDirection().getAxis());
            return this.defaultBlockState()
                    .setValue(AXIS, axes.getFirst());
        }
        return this.defaultBlockState()
                .setValue(AXIS, preferredAxis != null && context.getPlayer().isShiftKeyDown()
                        ? context.getClickedFace().getAxis()
                        : context.getNearestLookingDirection().getAxis());
    }

    public void updateConnections(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide()) {
            Direction.Axis axis = state.getValue(AXIS);
            for (Direction direction : Iterate.directions) {
                if (direction.getAxis() == axis)
                    continue;
                BlockPos sourcePos = pos;
                BlockPos neighborPos = pos.relative(direction);
                while (sourcePos != neighborPos && level.getBlockState(neighborPos).getBlock() instanceof IConnectionForwardingBlock forwardingBlock) {
                    BlockPos tempSource = sourcePos;
                    sourcePos = neighborPos;
                    neighborPos = forwardingBlock.forwardConnection(level, tempSource, tempSource.equals(pos) ? state : level.getBlockState(tempSource), neighborPos);
                }
                BlockEntity neighbourTE = level.getBlockEntity(neighborPos);
                if (neighbourTE instanceof KineticBlockEntity kineticTE) {
                    if (kineticTE.hasNetwork())
                        kineticTE.getOrCreateNetwork().remove(kineticTE);
                    kineticTE.detachKinetics();
                    kineticTE.removeSource();
                    BlockState neighborState = level.getBlockState(neighborPos);
                    level.markAndNotifyBlock(neighborPos, level.getChunkAt(neighborPos), neighborState, neighborState, 3, 512);
                    if (neighbourTE instanceof GeneratingKineticBlockEntity generatingBlockEntity) {
                        generatingBlockEntity.reActivateSource = true;
                    }
                }
            }
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        updateConnections(level, pos, state);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        updateConnections(level, pos, state);
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public BlockPos forwardConnection(Level level, BlockPos sourcePos, BlockState sourceState, BlockPos neighbourPos) {
        BlockState state = level.getBlockState(neighbourPos);
        if (state.getBlock() != this)
            return neighbourPos;

        Vec3i offset = neighbourPos.subtract(sourcePos);
        if (!(sourceState.getBlock() instanceof IRotate rotatingBlock))
            return neighbourPos;
        Direction offsetDirection = Direction.fromDelta(offset.getX(), offset.getY(), offset.getZ());
        if (offsetDirection == null)
            return neighbourPos;
        if (!rotatingBlock.hasShaftTowards(level, sourcePos, sourceState, offsetDirection))
            return neighbourPos;
        if (offsetDirection.getAxis() == state.getValue(AXIS))
            return neighbourPos;
        return neighbourPos.offset(offset);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() != state.getValue(AXIS);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }
}
