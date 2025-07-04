package com.hlysine.create_connected.content.copycat.stairs;

import com.hlysine.create_connected.content.copycat.ICopycatWithWrappedBlock;
import com.hlysine.create_connected.content.copycat.WaterloggedCopycatWrappedBlock;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.core.Direction.*;
import static net.minecraft.world.level.block.StairBlock.HALF;

public class CopycatStairsBlock extends WaterloggedCopycatWrappedBlock {

    public static StairBlock stairs;

    public CopycatStairsBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(StairBlock.FACING, NORTH)
                .setValue(HALF, Half.BOTTOM)
                .setValue(StairBlock.SHAPE, StairsShape.STRAIGHT)
        );
    }

    @Override
    public Block getWrappedBlock() {
        return stairs;
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
        return ICopycatWithWrappedBlock.copyState(state, super.getStateForPlacement(pContext), false);
    }

    @Override
    public boolean useShapeForLightOcclusion(@NotNull BlockState pState) {
        return true;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return ICopycatWithWrappedBlock.wrappedState(stairs, pState).getShape(pLevel, pPos, pContext);
    }

    @Override
    public void attack(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer) {
        ICopycatWithWrappedBlock.wrappedState(stairs, pState).attack(pLevel, pPos, pPlayer);
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        return super.getExplosionResistance(state, level, pos, explosion);
    }

    @Override
    public void onPlace(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pOldState, boolean pIsMoving) {
        ICopycatWithWrappedBlock.wrappedState(stairs, pState).onPlace(pLevel, pPos, pOldState, pIsMoving);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        ICopycatWithWrappedBlock.wrappedState(stairs, pState).onRemove(pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState pState) {
        return ICopycatWithWrappedBlock.wrappedState(stairs, pState).isRandomlyTicking();
    }

    @Override
    public void randomTick(@NotNull BlockState pState, @NotNull ServerLevel pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
        ICopycatWithWrappedBlock.wrappedState(stairs, pState).randomTick(pLevel, pPos, pRandom);
    }

    @Override
    public void tick(@NotNull BlockState pState, @NotNull ServerLevel pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
        ICopycatWithWrappedBlock.wrappedState(stairs, pState).tick(pLevel, pPos, pRandom);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState pState, @NotNull Direction pDirection, @NotNull BlockState pNeighborState, @NotNull LevelAccessor pLevel, @NotNull BlockPos pCurrentPos, @NotNull BlockPos pNeighborPos) {
        return migrateOnUpdate(pLevel.isClientSide(), ICopycatWithWrappedBlock.unwrapForOperation(stairs, pState, state -> state.updateShape(pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos)));
    }

    @Override
    public @NotNull BlockState rotate(@NotNull BlockState pState, @NotNull Rotation pRotation) {
        return ICopycatWithWrappedBlock.unwrapForOperation(stairs, pState, state -> state.rotate(pRotation));
    }

    @Override
    public @NotNull BlockState mirror(@NotNull BlockState pState, @NotNull Mirror pMirror) {
        return ICopycatWithWrappedBlock.unwrapForOperation(stairs, pState, state -> state.mirror(pMirror));
    }

    @Override
    protected boolean isPathfindable(@NotNull BlockState pState, @NotNull PathComputationType pPathComputationType) {
        return ICopycatWithWrappedBlock.wrappedState(stairs, pState).isPathfindable(pPathComputationType);
    }

    @Override
    public boolean isIgnoredConnectivitySide(BlockAndTintGetter reader, BlockState state, Direction face,
                                             @Nullable BlockPos fromPos, @Nullable BlockPos toPos) {
        if (fromPos == null || toPos == null)
            return true;

        boolean flipped = state.getValue(HALF) == Half.TOP;
        Direction facing = state.getValue(StairBlock.FACING);
        BlockState toState = reader.getBlockState(toPos);
        BlockPos diff = toPos.subtract(fromPos);
        if (diff.equals(Vec3i.ZERO)) {
            return true;
        }
        Direction side = Direction.fromDelta(diff.getX(), diff.getY(), diff.getZ());

        if (toState.is(this)) {
            return false;
        } else {
            if (diff.getY() == 0) {
                // if target is level with this block,
                // only allows it to connect if it's adjacent to a full face of this block
                int fullCount = 0;
                if (diff.getX() != 0 && getFaceShape(state, Direction.fromAxisAndDirection(Axis.X, directionOf(diff.getX()))).isFull())
                    fullCount++;
                if (diff.getZ() != 0 && getFaceShape(state, Direction.fromAxisAndDirection(Axis.Z, directionOf(diff.getZ()))).isFull())
                    fullCount++;
                return fullCount == 0;
            } else {
                // if target is not level with this block,
                // only allow connections below the base of this block
                return (diff.getY() > 0) != flipped;
            }
        }
    }

    @Override
    public boolean canConnectTexturesToward(BlockAndTintGetter reader, BlockPos fromPos, BlockPos toPos, BlockState state) {
        BlockState toState = reader.getBlockState(toPos);
        BlockPos diff = toPos.subtract(fromPos);
        if (diff.equals(Vec3i.ZERO)) {
            return true;
        }
        Direction side = Direction.fromDelta(diff.getX(), diff.getY(), diff.getZ());

        if (side != null) {
            FaceShape sideShape = getFaceShape(state, side);
            if (!sideShape.canConnect()) return false;
            if (toState.is(this)) {
                if (!sideShape.equals(getFaceShape(toState, side.getOpposite()))) return false;
            } else {
                if (!sideShape.isFull()) return false;
            }
        }

        return true;
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
        if (neighborState.getBlock() instanceof StairBlock || neighborState.getBlock() instanceof CopycatStairsBlock) {
            if (getMaterial(level, pos).skipRendering(getMaterial(level, pos.relative(dir)), dir.getOpposite()))
                return getFaceShape(state, dir).equals(getFaceShape(neighborState, dir.getOpposite()));
        }

        return getFaceShape(state, dir).isFull()
                && getMaterial(level, pos).skipRendering(neighborState, dir.getOpposite());
    }

    public static BlockState getMaterial(BlockGetter reader, BlockPos targetPos) {
        BlockState state = CopycatBlock.getMaterial(reader, targetPos);
        if (state.is(Blocks.AIR)) return reader.getBlockState(targetPos);
        return state;
    }

    private static AxisDirection directionOf(int value) {
        return value >= 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE;
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

