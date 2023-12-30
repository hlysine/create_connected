package com.hlysine.create_connected.content.linkedmodule;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface LinkedModuleBlock {
    Block getBlock();

    Block getBase();

    void replaceBase(BlockState baseState, Level world, BlockPos pos);
}
