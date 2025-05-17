package com.hlysine.create_connected.content.copycat.stairs;

import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class WrappedStairsBlock extends StairBlock {
    public WrappedStairsBlock(BlockState state, Properties properties) {
        super(state, properties);
    }

    /**
     * For compatibility with Additional Placements
     */
    public boolean generateAdditionalStates() {
        return false;
    }
}
