package com.hlysine.create_connected.content.copycat.beam;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.CCShapes;
import com.hlysine.create_connected.content.copycat.MigratingWaterloggedCopycatBlock;
import com.simibubi.create.foundation.placement.PoleHelper;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

import static net.minecraft.core.Direction.Axis;

public class CopycatBeamBlock extends MigratingWaterloggedCopycatBlock {

    public static final EnumProperty<Axis> AXIS = BlockStateProperties.AXIS;

    private static final int placementHelperId = PlacementHelpers.register(new PlacementHelper());

    public CopycatBeamBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState()
                .setValue(AXIS, Axis.Y));
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!player.isShiftKeyDown() && player.mayBuild()) {
            ItemStack heldItem = player.getItemInHand(hand);
            IPlacementHelper placementHelper = PlacementHelpers.get(placementHelperId);
            if (placementHelper.matchesItem(heldItem)) {
                placementHelper.getOffset(player, level, state, pos, hitResult)
                        .placeInWorld(level, (BlockItem) heldItem.getItem(), player, hand, hitResult);
                return ItemInteractionResult.SUCCESS;
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public boolean isIgnoredConnectivitySide(BlockAndTintGetter reader, BlockState state, Direction face,
                                             @Nullable BlockPos fromPos, @Nullable BlockPos toPos) {
        if (fromPos == null || toPos == null)
            return true;

        Axis axis = state.getValue(AXIS);
        BlockState toState = reader.getBlockState(toPos);

        if (toState.is(this)) {
            // connecting to another copycat beam
            return toState.getValue(AXIS) != axis;
        } else {
            // doesn't connect to any other blocks
            return true;
        }
    }

    @Override
    public boolean canConnectTexturesToward(BlockAndTintGetter reader, BlockPos fromPos, BlockPos toPos,
                                            BlockState state) {
        Axis axis = state.getValue(AXIS);
        BlockState toState = reader.getBlockState(toPos);

        BlockPos diff = toPos.subtract(fromPos);
        if (diff.equals(Vec3i.ZERO)) {
            return true;
        }
        Direction face = Direction.fromDelta(diff.getX(), diff.getY(), diff.getZ());
        if (face == null) {
            return false;
        }

        if (toState.is(this)) {
            return toState.getValue(AXIS) == axis && face.getAxis() == axis;
        } else {
            return false;
        }
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public boolean canFaceBeOccluded(BlockState state, Direction face) {
        return face.getAxis() == state.getValue(AXIS);
    }

    @Override
    public boolean shouldFaceAlwaysRender(BlockState state, Direction face) {
        return face.getAxis() != state.getValue(AXIS);
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        BlockState stateForPlacement = super.getStateForPlacement(context);
        assert stateForPlacement != null;
        Axis axis = context.getNearestLookingDirection().getAxis();
        return stateForPlacement.setValue(AXIS, axis);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(AXIS));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return CCShapes.CASING_8PX_CENTERED.get(pState.getValue(AXIS));
    }

    @Override
    public boolean supportsExternalFaceHiding(BlockState state) {
        return true;
    }

    @Override
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState,
                                     Direction dir) {
        if (state.is(this) == neighborState.is(this)) {
            if (getMaterial(level, pos).skipRendering(getMaterial(level, pos.relative(dir)), dir.getOpposite())) {
                return state.getValue(AXIS) == dir.getAxis() && neighborState.getValue(AXIS) == dir.getAxis();
            }
        }

        return false;
    }

    @Override
    public @NotNull BlockState rotate(@NotNull BlockState state, Rotation rot) {
        switch (rot) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> {
                return switch (state.getValue(AXIS)) {
                    case X -> state.setValue(AXIS, Axis.Z);
                    case Z -> state.setValue(AXIS, Axis.X);
                    default -> state;
                };
            }
            default -> {
                return state;
            }
        }
    }

    @MethodsReturnNonnullByDefault
    private static class PlacementHelper extends PoleHelper<Axis> {

        private PlacementHelper() {
            super(CCBlocks.COPYCAT_BEAM::has, state -> state.getValue(AXIS), AXIS);
        }

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return i -> i.getItem() instanceof BlockItem
                    && (((BlockItem) i.getItem()).getBlock() instanceof CopycatBeamBlock);
        }

    }

}

