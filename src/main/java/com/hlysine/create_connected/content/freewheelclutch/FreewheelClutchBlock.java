package com.hlysine.create_connected.content.freewheelclutch;

import com.hlysine.create_connected.CCBlockEntityTypes;
import com.hlysine.create_connected.content.CCBlockStateProperties;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class FreewheelClutchBlock extends DirectionalKineticBlock implements IBE<FreewheelClutchBlockEntity> {

    public static final BooleanProperty UNCOUPLED = CCBlockStateProperties.UNCOUPLED;

    public FreewheelClutchBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(UNCOUPLED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UNCOUPLED);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public Class<FreewheelClutchBlockEntity> getBlockEntityClass() {
        return FreewheelClutchBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FreewheelClutchBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.FREEWHEEL_CLUTCH.get();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(FACING).getAxis();
    }
}
