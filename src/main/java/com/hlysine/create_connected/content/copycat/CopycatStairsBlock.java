package com.hlysine.create_connected.content.copycat;

import com.simibubi.create.content.decoration.copycat.WaterloggedCopycatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.core.Direction.*;
import static net.minecraft.world.level.block.StairBlock.HALF;

@SuppressWarnings("deprecation")
public class CopycatStairsBlock extends WaterloggedCopycatBlock {

    private final StairBlock stairs;

    public CopycatStairsBlock(Properties properties) {
        super(properties);
        stairs = (StairBlock) Blocks.STONE_STAIRS;
        registerDefaultState(defaultBlockState()
                .setValue(StairBlock.FACING, NORTH)
                .setValue(HALF, Half.BOTTOM)
                .setValue(StairBlock.SHAPE, StairsShape.STRAIGHT)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(StairBlock.FACING, HALF, StairBlock.SHAPE));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext pContext) {
        BlockState state = stairs.getStateForPlacement(pContext);
        if (state == null) return super.getStateForPlacement(pContext);
        return super.getStateForPlacement(pContext)
                .setValue(StairBlock.FACING, state.getValue(StairBlock.FACING))
                .setValue(HALF, state.getValue(HALF))
                .setValue(StairBlock.SHAPE, state.getValue(StairBlock.SHAPE));
    }

    @Override
    public boolean useShapeForLightOcclusion(@NotNull BlockState pState) {
        return true;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return stairs.getShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public void animateTick(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
        stairs.animateTick(pState, pLevel, pPos, pRandom);
    }

    @Override
    public void attack(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer) {
        stairs.attack(pState, pLevel, pPos, pPlayer);
    }

    @Override
    public void destroy(@NotNull LevelAccessor pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState) {
        stairs.destroy(pLevel, pPos, pState);
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        return super.getExplosionResistance(state, level, pos, explosion);
    }

    @Override
    public void onPlace(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pOldState, boolean pIsMoving) {
        stairs.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        stairs.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState pState) {
        return stairs.isRandomlyTicking(pState);
    }

    @Override
    public void randomTick(@NotNull BlockState pState, @NotNull ServerLevel pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
        stairs.randomTick(pState, pLevel, pPos, pRandom);
    }

    @Override
    public void tick(@NotNull BlockState pState, @NotNull ServerLevel pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
        stairs.tick(pState, pLevel, pPos, pRandom);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        InteractionResult result = super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
        if (result == InteractionResult.PASS) {
            return stairs.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
        }
        return result;
    }

    @Override
    public void wasExploded(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Explosion pExplosion) {
        stairs.wasExploded(pLevel, pPos, pExplosion);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState pState, @NotNull Direction pDirection, @NotNull BlockState pNeighborState, @NotNull LevelAccessor pLevel, @NotNull BlockPos pCurrentPos, @NotNull BlockPos pNeighborPos) {
        return stairs.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
    }

    @Override
    public @NotNull BlockState rotate(@NotNull BlockState pState, @NotNull Rotation pRotation) {
        return stairs.rotate(pState, pRotation);
    }

    @Override
    public @NotNull BlockState mirror(@NotNull BlockState pState, @NotNull Mirror pMirror) {
        return stairs.mirror(pState, pMirror);
    }

    @Override
    public boolean isPathfindable(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull PathComputationType pType) {
        return stairs.isPathfindable(pState, pLevel, pPos, pType);
    }

    @Override
    public boolean isIgnoredConnectivitySide(BlockAndTintGetter reader, BlockState state, Direction face,
                                             BlockPos fromPos, BlockPos toPos) {
        BlockState toState = reader.getBlockState(toPos);

        if (!toState.is(this)) {
            return !canConnectTexturesToward(reader, fromPos, toPos, state);
        }
        return false;
    }

    @Override
    public boolean canConnectTexturesToward(BlockAndTintGetter reader, BlockPos fromPos, BlockPos toPos, BlockState state) {
        BlockState toState = reader.getBlockState(toPos);

        if (!state.is(this) && toState.is(this)) {
            return canConnectTexturesToward(reader, toPos, fromPos, toState);
        }

        BlockPos diff = toPos.subtract(fromPos);
        if (diff.equals(Vec3i.ZERO)) {
            return true;
        }

        int connectScore = 0;
        if (diff.getX() != 0) {
            FaceShape shape = getFaceShape(state, Direction.fromAxisAndDirection(Axis.X, diff.getX() > 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE));
            if (!shape.canConnect())
                return false;
            if (shape.isFull())
                connectScore += 2;
            else connectScore += 1;
            if (toState.is(this) && !getFaceShape(toState, Direction.fromAxisAndDirection(Axis.X, diff.getX() < 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE)).canConnect())
                return false;
        }
        if (diff.getY() != 0) {
            FaceShape shape = getFaceShape(state, Direction.fromAxisAndDirection(Axis.Y, diff.getY() > 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE));
            if (!shape.canConnect())
                return false;
            if (shape.isFull())
                connectScore += 2;
            else connectScore += 1;
            if (toState.is(this) && !getFaceShape(toState, Direction.fromAxisAndDirection(Axis.Y, diff.getY() < 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE)).canConnect())
                return false;
        }
        if (diff.getZ() != 0) {
            FaceShape shape = getFaceShape(state, Direction.fromAxisAndDirection(Axis.Z, diff.getZ() > 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE));
            if (!shape.canConnect())
                return false;
            if (shape.isFull())
                connectScore += 2;
            else connectScore += 1;
            if (toState.is(this) && !getFaceShape(toState, Direction.fromAxisAndDirection(Axis.Z, diff.getZ() < 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE)).canConnect())
                return false;
        }
        return !toState.is(this) && connectScore >= 2 || toState.is(this);
    }

    @Override
    public boolean canFaceBeOccluded(BlockState state, Direction face) {
        int count = getFaceShape(state, face).countBlocks();
        return count == 4 || count == 3 && state.getValue(StairBlock.SHAPE) == StairsShape.STRAIGHT;
    }

    @Override
    public boolean shouldFaceAlwaysRender(BlockState state, Direction face) {
        return !canFaceBeOccluded(state, face);
    }

    @Override
    public boolean supportsExternalFaceHiding(BlockState state) {
        return true;
    }

    @Override
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState,
                                     Direction dir) {
        if (state.is(this) == neighborState.is(this)) {
            if (getMaterial(level, pos).skipRendering(getMaterial(level, pos.relative(dir)), dir.getOpposite()))
                return getFaceShape(state, dir) == getFaceShape(neighborState, dir.getOpposite());
        }

        return getFaceShape(state, dir).isFull()
                && getMaterial(level, pos).skipRendering(neighborState, dir.getOpposite());
    }

    /**
     * Return the area of the face that is at the edge of the block.
     */
    public static FaceShape getFaceShape(BlockState state, Direction face) {
        boolean top = state.getValue(StairBlock.HALF) == Half.TOP;
        Direction facing = state.getValue(StairBlock.FACING);
        StairsShape shape = state.getValue(StairBlock.SHAPE);
        if (!top && face == DOWN) return new FaceShape().fillAll();
        if (top && face == UP) return new FaceShape().fillAll();

        FaceShape faceShape = new FaceShape();

        switch (shape) {
            case STRAIGHT -> {
                if (!top && face == UP || top && face == DOWN)
                    return faceShape.fillTop().rotate(facing.toYRot());
                faceShape.fillRow(top);
                if (face == facing) return faceShape.fillRow(!top);
                if (face == facing.getOpposite()) return faceShape;
                return faceShape.fillRow(!top, facing.getAxisDirection());
            }
            case INNER_LEFT -> {
                if (!top && face == UP || top && face == DOWN)
                    return faceShape.fillTop().fillBottom(AxisDirection.POSITIVE).rotate(facing.toYRot());
                faceShape.fillRow(top);
                if (face == facing) return faceShape.fillRow(!top);
                if (face == facing.getOpposite())
                    return faceShape.fillRow(!top, facing.getCounterClockWise().getAxisDirection());
                if (face == facing.getCounterClockWise()) return faceShape.fillRow(!top);
                if (face == facing.getClockWise())
                    return faceShape.fillRow(!top, facing.getAxisDirection());
            }
            case INNER_RIGHT -> {
                if (!top && face == UP || top && face == DOWN)
                    return faceShape.fillTop().fillBottom(AxisDirection.NEGATIVE).rotate(facing.toYRot());
                faceShape.fillRow(top);
                if (face == facing) return faceShape.fillRow(!top);
                if (face == facing.getOpposite())
                    return faceShape.fillRow(!top, facing.getClockWise().getAxisDirection());
                if (face == facing.getClockWise()) return faceShape.fillRow(!top);
                if (face == facing.getCounterClockWise())
                    return faceShape.fillRow(!top, facing.getAxisDirection());
            }
            case OUTER_LEFT -> {
                if (!top && face == UP || top && face == DOWN)
                    return faceShape.fillTop(AxisDirection.POSITIVE).rotate(facing.toYRot());
                faceShape.fillRow(top);
                if (face == facing) return faceShape.fillRow(!top, facing.getCounterClockWise().getAxisDirection());
                if (face == facing.getOpposite())
                    return faceShape;
                if (face == facing.getCounterClockWise()) return faceShape.fillRow(!top, facing.getAxisDirection());
                if (face == facing.getClockWise())
                    return faceShape;
            }
            case OUTER_RIGHT -> {
                if (!top && face == UP || top && face == DOWN)
                    return faceShape.fillTop(AxisDirection.NEGATIVE).rotate(facing.toYRot());
                faceShape.fillRow(top);
                if (face == facing) return faceShape.fillRow(!top, facing.getClockWise().getAxisDirection());
                if (face == facing.getOpposite())
                    return faceShape;
                if (face == facing.getClockWise()) return faceShape.fillRow(!top, facing.getAxisDirection());
                if (face == facing.getCounterClockWise())
                    return faceShape;
            }
        }
        return faceShape;
    }

    private static class FaceShape {
        public boolean topNegative;
        public boolean topPositive;
        public boolean bottomNegative;
        public boolean bottomPositive;

        public FaceShape fillTop() {
            topNegative = topPositive = true;
            return this;
        }

        public FaceShape fillTop(AxisDirection direction) {
            switch (direction) {
                case POSITIVE -> topPositive = true;
                case NEGATIVE -> topNegative = true;
            }
            return this;
        }

        public FaceShape fillBottom() {
            bottomNegative = bottomPositive = true;
            return this;
        }

        public FaceShape fillBottom(AxisDirection direction) {
            switch (direction) {
                case POSITIVE -> bottomPositive = true;
                case NEGATIVE -> bottomNegative = true;
            }
            return this;
        }

        public FaceShape fillRow(boolean top) {
            if (top) return fillTop();
            return fillBottom();
        }

        public FaceShape fillRow(boolean top, AxisDirection direction) {
            if (top) return fillTop(direction);
            return fillBottom(direction);
        }

        public FaceShape fillAll() {
            return fillTop().fillBottom();
        }

        public FaceShape rotate(float angle) {
            return rotate((int) angle);
        }

        public FaceShape rotate(int angle) {
            angle = angle % 360;
            if (angle < 0) angle += 360;
            return switch (angle) {
                case 90 -> set(topNegative, bottomNegative, topPositive, bottomPositive);
                case 180 -> set(topPositive, topNegative, bottomPositive, bottomNegative);
                case 270 -> set(bottomPositive, topPositive, bottomNegative, topNegative);
                default -> this;
            };
        }

        public FaceShape set(boolean bottomNegative, boolean bottomPositive, boolean topNegative, boolean topPositive) {
            this.bottomNegative = bottomNegative;
            this.bottomPositive = bottomPositive;
            this.topNegative = topNegative;
            this.topPositive = topPositive;
            return this;
        }

        public int countBlocks() {
            int count = 0;
            if (bottomNegative) count++;
            if (bottomPositive) count++;
            if (topNegative) count++;
            if (topPositive) count++;
            return count;
        }

        public boolean canConnect() {
            return countBlocks() >= 3;
        }

        public boolean isFull() {
            return countBlocks() == 4;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof FaceShape shape)) return false;
            return shape.bottomNegative == this.bottomNegative && shape.bottomPositive == this.bottomPositive &&
                    shape.topNegative == this.topNegative && shape.topPositive == this.topPositive;
        }
    }
}

