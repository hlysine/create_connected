package com.hlysine.create_connected.content.linkedtransmitter;

import com.hlysine.create_connected.CCShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public interface LinkedTransmitterBlock {
    Block getBlock();

    Block getBase();

    void replaceBase(BlockState baseState, Level world, BlockPos pos);

    default VoxelShape getTransmitterShape(BlockState state) {
        Direction facing = state.getValue(FaceAttachedHorizontalDirectionalBlock.FACING);
        return switch (state.getValue(FaceAttachedHorizontalDirectionalBlock.FACE)) {
            case FLOOR -> CCShapes.FLOOR_LINKED_TRANSMITTER.get(facing);
            case WALL -> CCShapes.WALL_LINKED_TRANSMITTER.get(facing);
            case CEILING -> CCShapes.CEILING_LINKED_TRANSMITTER.get(facing);
        };
    }

    default boolean isHittingBase(BlockState state, BlockGetter level, BlockPos pos, HitResult hit) {
        return !getTransmitterShape(state).bounds().inflate(0.01 / 16).move(pos).contains(hit.getLocation());
    }

    default @NotNull InteractionResult useTransmitter(@NotNull BlockState state,
                                                      @NotNull Level level,
                                                      @NotNull BlockPos pos,
                                                      @NotNull Player player) {
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                level.setBlock(pos, state.cycle(BlockStateProperties.LOCKED), Block.UPDATE_CLIENTS);
            }
            return InteractionResult.SUCCESS;
        }
        if (state.getValue(BlockStateProperties.LOCKED))
            return InteractionResult.CONSUME;
        return InteractionResult.PASS;
    }
}
