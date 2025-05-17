package com.hlysine.create_connected.content.copycat.verticalstep;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.CCShapes;
import com.hlysine.create_connected.content.copycat.MigratingWaterloggedCopycatBlock;
import com.simibubi.create.foundation.placement.PoleHelper;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
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
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static net.minecraft.core.Direction.Axis;

public class CopycatVerticalStepBlock extends MigratingWaterloggedCopycatBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final int placementHelperId = PlacementHelpers.register(new PlacementHelper());

    public CopycatVerticalStepBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH));
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
                                             BlockPos fromPos, BlockPos toPos) {
        Direction direction = state.getValue(FACING);
        BlockState toState = reader.getBlockState(toPos);

        if (toState.is(this)) {
            // connecting to another copycat beam
            return toState.getValue(FACING) != direction;
        } else {
            // doesn't connect to any other blocks
            return true;
        }
    }

    @Override
    public boolean canConnectTexturesToward(BlockAndTintGetter reader, BlockPos fromPos, BlockPos toPos,
                                            BlockState state) {
        Direction facing = state.getValue(FACING);
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
            return toState.getValue(FACING) == facing && face.getAxis() == Axis.Y;
        } else {
            return false;
        }
    }

    @Override
    protected boolean isPathfindable(@NotNull BlockState state, @NotNull PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public boolean canFaceBeOccluded(BlockState state, Direction face) {
        Direction facing = state.getValue(FACING);
        return face.getAxis() == Axis.Y || face == facing || face == facing.getCounterClockWise();
    }

    @Override
    public boolean shouldFaceAlwaysRender(BlockState state, Direction face) {
        return !canFaceBeOccluded(state, face);
    }

    private static final Map<Pair<Integer, Integer>, Direction> VERTICAL_POSITION_MAP = new HashMap<>();
    private static final Map<Pair<Direction, Integer>, Direction> HORIZONTAL_POSITION_MAP = new HashMap<>();

    static {
        for (Direction main : Iterate.horizontalDirections) {
            Direction cross = main.getCounterClockWise();

            int mainOffset = main.getAxisDirection().getStep();
            int crossOffset = cross.getAxisDirection().getStep();

            if (main.getAxis() == Axis.X)
                VERTICAL_POSITION_MAP.put(Pair.of(mainOffset, crossOffset), main);
            else
                VERTICAL_POSITION_MAP.put(Pair.of(crossOffset, mainOffset), main);

            HORIZONTAL_POSITION_MAP.put(Pair.of(main.getOpposite(), crossOffset), main);
            HORIZONTAL_POSITION_MAP.put(Pair.of(cross.getOpposite(), mainOffset), main);
        }
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        BlockState stateForPlacement = super.getStateForPlacement(context);
        assert stateForPlacement != null;

        int xOffset = context.getClickLocation().x - context.getClickedPos().getX() > 0.5 ? 1 : -1;
        int zOffset = context.getClickLocation().z - context.getClickedPos().getZ() > 0.5 ? 1 : -1;

        if (context.getClickedFace().getAxis() == Axis.Y) {
            return stateForPlacement.setValue(FACING, VERTICAL_POSITION_MAP.get(Pair.of(xOffset, zOffset)));
        } else {
            return stateForPlacement.setValue(FACING, HORIZONTAL_POSITION_MAP.get(
                    Pair.of(context.getClickedFace(), context.getClickedFace().getAxis() == Axis.X ? zOffset : xOffset)
            ));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return CCShapes.CASING_8PX_VERTICAL.get(pState.getValue(FACING));
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
                return dir.getAxis().isVertical() && neighborState.getValue(FACING) == state.getValue(FACING);
            }
        }

        return false;
    }

    @Override
    public @NotNull BlockState rotate(@NotNull BlockState pState, Rotation pRot) {
        return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(@NotNull BlockState pState, @NotNull Mirror pMirror) {
        Axis mirrorAxis = null;
        for (Axis axis : Iterate.axes) {
            if (pMirror.rotation().inverts(axis)) {
                mirrorAxis = axis;
                break;
            }
        }
        if (mirrorAxis == null || mirrorAxis.isVertical()) {
            return super.mirror(pState, pMirror);
        }
        Direction facing = pState.getValue(FACING);
        if (facing.getAxis() != mirrorAxis) {
            return pState.setValue(FACING, facing.getClockWise());
        } else {
            return pState.setValue(FACING, facing.getCounterClockWise());
        }
    }

    @MethodsReturnNonnullByDefault
    private static class PlacementHelper extends PoleHelper<Direction> {

        private PlacementHelper() {
            super(CCBlocks.COPYCAT_VERTICAL_STEP::has, $ -> Axis.Y, FACING);
        }

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return i -> i.getItem() instanceof BlockItem
                    && (((BlockItem) i.getItem()).getBlock() instanceof CopycatVerticalStepBlock);
        }

    }

}

