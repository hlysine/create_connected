package com.hlysine.create_connected.content.linkedtransmitter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface LinkedTransmitterBlock {
    Block getBlock();

    Block getBase();

    void replaceBase(BlockState baseState, Level world, BlockPos pos);
}
