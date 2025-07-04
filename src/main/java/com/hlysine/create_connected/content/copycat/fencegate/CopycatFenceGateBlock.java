package com.hlysine.create_connected.content.copycat.fencegate;

import com.hlysine.create_connected.content.copycat.ICopycatWithWrappedBlock;
import com.hlysine.create_connected.content.copycat.WaterloggedCopycatWrappedBlock;
import com.hlysine.create_connected.mixin.copycat.fencegate.FenceGateBlockAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.FenceGateBlock.*;

public class CopycatFenceGateBlock extends WaterloggedCopycatWrappedBlock {

    public static FenceGateBlock fenceGate;

    public CopycatFenceGateBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(OPEN, false)
                .setValue(POWERED, false)
                .setValue(IN_WALL, false)
                .setValue(FACING, Direction.SOUTH)
        );
    }

    @Override
    public Block getWrappedBlock() {
        return fenceGate;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(OPEN, POWERED, IN_WALL, FACING));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext pContext) {
        BlockState state = fenceGate.getStateForPlacement(pContext);
        if (state == null) return super.getStateForPlacement(pContext);
        return ICopycatWithWrappedBlock.copyState(state, super.getStateForPlacement(pContext), false);
    }

    @Override
    public boolean collisionExtendsVertically(BlockState state, BlockGetter level, BlockPos pos, Entity collidingEntity) {
        return true;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        return ((FenceGateBlockAccessor) fenceGate).callUseWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return ICopycatWithWrappedBlock.wrappedState(fenceGate, pState).getShape(pLevel, pPos, pContext);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState pState, @NotNull Direction pDirection, @NotNull BlockState pNeighborState, @NotNull LevelAccessor pLevel, @NotNull BlockPos pCurrentPos, @NotNull BlockPos pNeighborPos) {
        return migrateOnUpdate(pLevel.isClientSide(), ICopycatWithWrappedBlock.unwrapForOperation(fenceGate, pState, state -> state.updateShape(pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos)));
    }

    @Override
    public @NotNull VoxelShape getBlockSupportShape(@NotNull BlockState pState, @NotNull BlockGetter pReader, @NotNull BlockPos pPos) {
        return ICopycatWithWrappedBlock.wrappedState(fenceGate, pState).getBlockSupportShape(pReader, pPos);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return ICopycatWithWrappedBlock.wrappedState(fenceGate, pState).getCollisionShape(pLevel, pPos, pContext);
    }

    @Override
    public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos) {
        return ICopycatWithWrappedBlock.wrappedState(fenceGate, pState).getOcclusionShape(pLevel, pPos);
    }

    @Override
    protected boolean isPathfindable(@NotNull BlockState pState, @NotNull PathComputationType pPathComputationType) {
        return ICopycatWithWrappedBlock.wrappedState(fenceGate, pState).isPathfindable(pPathComputationType);
    }

    @Override
    public void neighborChanged(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Block pBlock, @NotNull BlockPos pFromPos, boolean pIsMoving) {
        ICopycatWithWrappedBlock.wrappedState(fenceGate, pState).handleNeighborChanged(pLevel, pPos, pBlock, pFromPos, pIsMoving);
    }

    @Override
    public @NotNull BlockState rotate(@NotNull BlockState pState, @NotNull Rotation pRotation) {
        return ICopycatWithWrappedBlock.unwrapForOperation(fenceGate, pState, state -> state.rotate(pRotation));
    }

    @Override
    public @NotNull BlockState mirror(@NotNull BlockState pState, @NotNull Mirror pMirror) {
        return ICopycatWithWrappedBlock.unwrapForOperation(fenceGate, pState, state -> state.mirror(pMirror));
    }

    @Override
    public boolean isIgnoredConnectivitySide(BlockAndTintGetter reader, BlockState state, Direction face,
                                             @Nullable BlockPos fromPos, @Nullable BlockPos toPos) {
        return true;
    }

    @Override
    public boolean canConnectTexturesToward(BlockAndTintGetter reader, BlockPos fromPos, BlockPos toPos, BlockState state) {
        return false;
    }

    @Override
    public boolean canFaceBeOccluded(BlockState state, Direction face) {
        return false;
    }

    @Override
    public boolean shouldFaceAlwaysRender(BlockState state, Direction face) {
        return true;
    }

    @Override
    public boolean supportsExternalFaceHiding(BlockState state) {
        return true;
    }
}

