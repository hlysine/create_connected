package com.hlysine.create_connected.content.kineticbridge;

import com.hlysine.create_connected.CCBlockEntityTypes;
import com.hlysine.create_connected.CCBlocks;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class KineticBridgeBlock extends DirectionalKineticBlock implements IBE<KineticBridgeBlockEntity> {

    public KineticBridgeBlock(Properties properties) {
        super(properties);
    }

    private BlockState getBaseStateForPlacement(BlockPlaceContext context) {
        Direction preferred = getPreferredFacing(context);
        if (preferred == null || (context.getPlayer() != null && context.getPlayer()
                .isShiftKeyDown())) {
            Direction nearestLookingDirection = context.getNearestLookingDirection();
            return defaultBlockState().setValue(FACING, context.getPlayer() != null && context.getPlayer()
                    .isShiftKeyDown() ? nearestLookingDirection.getOpposite() : nearestLookingDirection);
        }
        return defaultBlockState().setValue(FACING, preferred.getOpposite());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState stateForPlacement = getBaseStateForPlacement(context);
        BlockPos pos = context.getClickedPos();
        Direction facing = stateForPlacement.getValue(FACING);

        BlockPos destinationPos = pos.relative(facing);
        BlockState occupiedState = context.getLevel().getBlockState(destinationPos);
        if (!occupiedState.canBeReplaced())
            return null;

        return stateForPlacement;
    }

    public Direction getDirectionForPlacement(BlockPlaceContext context) {
        return getBaseStateForPlacement(context).getValue(FACING);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.PASS;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.getBlockTicks().hasScheduledTick(pos, this))
            level.scheduleTick(pos, this, 1);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pNewState.is(this)) {
            Direction facing = pState.getValue(FACING);
            BlockPos destinationPos = pPos.relative(facing);

            BlockState occupiedState = pLevel.getBlockState(destinationPos);
            BlockState requiredStructure = CCBlocks.KINETIC_BRIDGE_DESTINATION.getDefaultState()
                    .setValue(KineticBridgeDestinationBlock.FACING, facing);
            if (occupiedState.equals(requiredStructure)) {
                pLevel.destroyBlock(destinationPos, false);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        Direction facing = pState.getValue(FACING);
        BlockPos destinationPos = pPos.relative(facing);

        BlockState occupiedState = pLevel.getBlockState(destinationPos);
        BlockState requiredStructure = CCBlocks.KINETIC_BRIDGE_DESTINATION.getDefaultState()
                .setValue(KineticBridgeDestinationBlock.FACING, facing);
        if (!occupiedState.equals(requiredStructure)) {
            if (!occupiedState.canBeReplaced()) {
                pLevel.destroyBlock(pPos, false);
                return;
            }
            pLevel.setBlockAndUpdate(destinationPos, requiredStructure);
        }
    }

    @Override
    public BlockEntityType<? extends KineticBridgeBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.KINETIC_BRIDGE.get();
    }

    @Override
    public Class<KineticBridgeBlockEntity> getBlockEntityClass() {
        return KineticBridgeBlockEntity.class;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING).getOpposite();
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }
}

