package com.hlysine.create_connected.content.copycat.stairs;

import com.hlysine.create_connected.content.copycat.IWrappedBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class WrappedStairsBlock extends StairBlock implements IWrappedBlock {
    public WrappedStairsBlock(Supplier<BlockState> state, Properties properties) {
        super(state, properties);
    }
}
