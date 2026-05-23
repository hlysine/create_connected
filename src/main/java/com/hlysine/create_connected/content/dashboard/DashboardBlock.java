package com.hlysine.create_connected.content.dashboard;

import com.hlysine.create_connected.CCBlockEntityTypes;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignApplicator;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DashboardBlock extends HorizontalDirectionalBlock implements IWrenchable, ProperWaterloggedBlock, IBE<DashboardBlockEntity> {

    public static final BooleanProperty OPEN = BooleanProperty.create("open");

    public static final MapCodec<DashboardBlock> CODEC = simpleCodec(DashboardBlock::new);

    public DashboardBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(OPEN, false)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING, OPEN, WATERLOGGED));
    }


    @Override
    protected ItemInteractionResult useItemOn(final ItemStack itemStack, final BlockState blockState, final Level level, final BlockPos blockPos, final Player player, final InteractionHand interactionHand, final BlockHitResult blockHitResult) {
        final ItemStack heldItem = player.getItemInHand(interactionHand);

        if (heldItem.getItem() instanceof final SignApplicator signApplicator) {
            final MutableBoolean success = new MutableBoolean(false);
            this.withBlockEntityDo(level, blockPos, be -> {
                final SignBlockEntity dummySign = new SignBlockEntity(blockPos, Blocks.OAK_SIGN.defaultBlockState());
                dummySign.setLevel(be.getLevel());
                dummySign.setText(be.text, true);
                dummySign.setWaxed(true);

                if (signApplicator.canApplyToSign(be.text, player) && signApplicator.tryApplyToSign(be.getLevel(), dummySign, true, player)) {
                    be.setText(dummySign.getText(true));
                    success.setTrue();
                }
            });
            return success.booleanValue() ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
                                  LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        updateWater(pLevel, pState, pCurrentPos);
        if (pDirection == pState.getValue(FACING)) {
            return pState.setValue(OPEN, pNeighborState.getBlock() instanceof SeatBlock);
        }
        return pState;
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return fluidState(pState);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state = withWater(super.getStateForPlacement(pContext), pContext);
        Direction horizontalDirection = pContext.getHorizontalDirection();
        Player player = pContext.getPlayer();

        state = state.setValue(FACING, horizontalDirection.getOpposite());
        if (player != null && player.isShiftKeyDown())
            state = state.setValue(FACING, horizontalDirection);

        BlockPos target = pContext.getClickedPos().relative(state.getValue(FACING));
        if (pContext.getLevel().getBlockState(target).getBlock() instanceof SeatBlock) {
            state = state.setValue(OPEN, true);
        }

        return state;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AllShapes.CONTROLS.get(pState.getValue(FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos,
                                        CollisionContext pContext) {
        return AllShapes.CONTROLS_COLLISION.get(pState.getValue(FACING));
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public Class<DashboardBlockEntity> getBlockEntityClass() {
        return DashboardBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends DashboardBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.DASHBOARD.get();
    }
}
