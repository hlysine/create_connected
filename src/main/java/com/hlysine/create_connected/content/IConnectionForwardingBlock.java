package com.hlysine.create_connected.content;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IConnectionForwardingBlock {
    /**
     * Modify the position of the connection to the neighbour block.
     * The connection is assumed to work both ways.
     *
     * @param level       The level the block is in
     * @param sourcePos   The position of the block that is connecting to the neighbour
     * @param sourceState The state of the block that is connecting to the neighbour
     * @param neighborPos The position of the neighbour block
     * @return The modified position of the neighbour block to connect to
     */
    BlockPos forwardConnection(Level level, BlockPos sourcePos, BlockState sourceState, BlockPos neighborPos);
}
