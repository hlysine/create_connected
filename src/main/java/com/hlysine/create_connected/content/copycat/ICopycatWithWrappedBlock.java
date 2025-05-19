package com.hlysine.create_connected.content.copycat;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public interface ICopycatWithWrappedBlock {
    /**
     * Returns any non-copycat block that is representative of this copycat.
     */
    Block getWrappedBlock();

    static Block unwrap(Block block) {
        if (block instanceof ICopycatWithWrappedBlock wrapper) {
            return wrapper.getWrappedBlock();
        }
        return block;
    }

    static BlockState copyState(BlockState source, BlockState destination, boolean includeWaterlogged) {
        BlockState newState = destination;
        for (Property<?> property : source.getProperties()) {
            if (property == BlockStateProperties.WATERLOGGED && !includeWaterlogged)
                continue;
            newState = tryCopyProperty(source, newState, property);
        }
        return newState;
    }

    static <T extends Comparable<T>> BlockState tryCopyProperty(BlockState state, BlockState newState, Property<T> property) {
        if (state.hasProperty(property) && newState.hasProperty(property)) {
            newState = newState.setValue(property, state.getValue(property));
        }
        return newState;
    }
}
