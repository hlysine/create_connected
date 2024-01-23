package com.hlysine.create_connected.content.copycat;

import com.hlysine.create_connected.CCShapes;
import com.simibubi.create.content.decoration.copycat.WaterloggedCopycatBlock;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CopycatBoardBlock extends WaterloggedCopycatBlock {
    public static BooleanProperty UP = BlockStateProperties.UP;
    public static BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static BooleanProperty EAST = BlockStateProperties.EAST;
    public static BooleanProperty WEST = BlockStateProperties.WEST;
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;

    public CopycatBoardBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST));
    }

    @Override
    public boolean isIgnoredConnectivitySide(BlockAndTintGetter reader, BlockState state, Direction face, BlockPos fromPos, BlockPos toPos) {
        return !reader.getBlockState(toPos).is(this);
    }

    @Override
    public boolean canConnectTexturesToward(BlockAndTintGetter reader, BlockPos fromPos, BlockPos toPos, BlockState state) {
        return reader.getBlockState(toPos).is(this);
    }

    @Override
    public boolean canFaceBeOccluded(BlockState state, Direction face) {
        return !state.getValue(byDirection(face.getOpposite()));
    }

    @Override
    public boolean shouldFaceAlwaysRender(BlockState state, Direction face) {
        return !canFaceBeOccluded(state, face);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        VoxelShape shape = Shapes.empty();
        for (Direction direction : Iterate.directions) {
            if (pState.getValue(byDirection(direction))) {
                shape = Shapes.or(shape, CCShapes.CASING_1PX.get(direction));
            }
        }
        return shape;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState stateForPlacement = super.getStateForPlacement(context);
        assert stateForPlacement != null;
        BlockPos blockPos = context.getClickedPos();
        BlockState state = context.getLevel().getBlockState(blockPos);
        if (state.is(this)) {
            return state.setValue(byDirection(context.getClickedFace().getOpposite()), true);
        } else {
            return stateForPlacement.setValue(byDirection(context.getClickedFace().getOpposite()), true);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {
        ItemStack itemstack = pUseContext.getItemInHand();
        return !pState.getValue(byDirection(pUseContext.getClickedFace().getOpposite())) && itemstack.is(this.asItem());
    }

    @Override
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState,
                                     Direction dir) {
        if (state.is(this) && !state.getValue(byDirection(dir))) return false;
        if (neighborState.is(this) && !neighborState.getValue(byDirection(dir.getOpposite()))) return false;
        if (state.is(this) == neighborState.is(this)) {
            return (getMaterial(level, pos).skipRendering(getMaterial(level, pos.relative(dir)), dir.getOpposite()));
        }

        return getMaterial(level, pos).skipRendering(neighborState, dir.getOpposite());
    }

    public static BooleanProperty byDirection(Direction direction) {
        return PROPERTY_BY_DIRECTION.get(direction);
    }
}

