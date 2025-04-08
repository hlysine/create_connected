package com.hlysine.create_connected.content.linkedtransmitter;

import com.hlysine.create_connected.CCShapes;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface LinkedTransmitterBlock {
    Block getBlock();

    Block getBase();

    void replaceBase(BlockState baseState, Level world, BlockPos pos);

    default VoxelShape getTransmitterShape(BlockState state) {
        Direction facing = state.getValue(AnalogLeverBlock.FACING);
        return switch (state.getValue(AnalogLeverBlock.FACE)) {
            case FLOOR -> CCShapes.FLOOR_LINKED_TRANSMITTER.get(facing);
            case WALL -> CCShapes.WALL_LINKED_TRANSMITTER.get(facing);
            case CEILING -> CCShapes.CEILING_LINKED_TRANSMITTER.get(facing);
        };
    }

    default boolean isHittingBase(BlockState state, BlockGetter level, BlockPos pos, HitResult hit) {
        return !getTransmitterShape(state).bounds().inflate(0.01 / 16).move(pos).contains(hit.getLocation());
    }
}
