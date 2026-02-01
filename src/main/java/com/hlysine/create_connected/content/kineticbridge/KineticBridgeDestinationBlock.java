package com.hlysine.create_connected.content.kineticbridge;

import com.hlysine.create_connected.CCBlockEntityTypes;
import com.hlysine.create_connected.CCBlocks;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class KineticBridgeDestinationBlock extends DirectionalKineticBlock implements IBE<KineticBridgeDestinationBlockEntity> {

    public KineticBridgeDestinationBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction getPreferredFacing(BlockPlaceContext context) {
        for (Direction side : Iterate.directions) {
            BlockState blockState = context.getLevel().getBlockState(context.getClickedPos().relative(side));
            if (!(blockState.getBlock() instanceof KineticBridgeBlock))
                continue;
            if (blockState.getValue(FACING) == side.getOpposite())
                return side.getOpposite();
        }
        return null;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState stateForPlacement = super.getStateForPlacement(context);
        if (stateForPlacement == null)
            return null;
        Direction preferredFacing = getPreferredFacing(context);
        if (preferredFacing == null)
            return null;
        return stateForPlacement.setValue(FACING, preferredFacing);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.PASS;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return CCBlocks.KINETIC_BRIDGE.asStack();
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        Level level = context.getLevel();

        if (stillValid(level, clickedPos, state)) {
            BlockPos sourcePos = getSource(clickedPos, state);
            if (!context.getLevel().getBlockState(sourcePos).is(CCBlocks.KINETIC_BRIDGE.get()))
                return super.onSneakWrenched(state, context);
            context = new UseOnContext(level, context.getPlayer(), context.getHand(), context.getItemInHand(),
                    new BlockHitResult(context.getClickLocation(), context.getClickedFace(), sourcePos,
                            context.isInside()));
            state = level.getBlockState(sourcePos);
        }

        return super.onSneakWrenched(state, context);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (stillValid(pLevel, pPos, pState)) {
            BlockPos sourcePos = getSource(pPos, pState);
            if (pLevel.getBlockState(sourcePos).is(CCBlocks.KINETIC_BRIDGE.get())) {
                pLevel.destroyBlock(sourcePos, true);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (stillValid(pLevel, pPos, pState)) {
            BlockPos sourcePos = getSource(pPos, pState);
            if (!pLevel.getBlockState(sourcePos).is(CCBlocks.KINETIC_BRIDGE.get())) {
                super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
                return;
            }
            pLevel.destroyBlockProgress(sourcePos.hashCode(), sourcePos, -1);
            if (!pLevel.isClientSide() && pPlayer.isCreative())
                pLevel.destroyBlock(sourcePos, false);
        }
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel,
                                  BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (stillValid(pLevel, pCurrentPos, pState)) {
            BlockPos sourcePos = getSource(pCurrentPos, pState);
            if (pLevel.getBlockState(sourcePos).is(CCBlocks.KINETIC_BRIDGE.get()))
                if (!pLevel.getBlockTicks().hasScheduledTick(sourcePos, CCBlocks.KINETIC_BRIDGE.get()))
                    pLevel.scheduleTick(sourcePos, CCBlocks.KINETIC_BRIDGE.get(), 1);
            return pState;
        }
        if (!(pLevel instanceof Level level) || level.isClientSide())
            return pState;
        if (!level.getBlockTicks().hasScheduledTick(pCurrentPos, this))
            level.scheduleTick(pCurrentPos, this, 1);
        return pState;
    }

    public static BlockPos getSource(BlockPos pos, BlockState state) {
        Direction direction = state.getOptionalValue(FACING).orElse(Direction.NORTH);
        return pos.relative(direction.getOpposite());
    }

    public boolean stillValid(BlockGetter level, BlockPos pos, BlockState state) {
        if (!state.is(this))
            return false;

        Direction direction = state.getValue(FACING);
        BlockPos sourcePos = pos.relative(direction.getOpposite());
        BlockState sourceState = level.getBlockState(sourcePos);
        return sourceState.getBlock() instanceof KineticBridgeBlock
                && sourceState.getValue(KineticBridgeBlock.FACING) == direction;
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!stillValid(pLevel, pPos, pState))
            pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
    }

    @Override
    public BlockEntityType<? extends KineticBridgeDestinationBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.KINETIC_BRIDGE_DESTINATION.get();
    }

    @Override
    public Class<KineticBridgeDestinationBlockEntity> getBlockEntityClass() {
        return KineticBridgeDestinationBlockEntity.class;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }
}

