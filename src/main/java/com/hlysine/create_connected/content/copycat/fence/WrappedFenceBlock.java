package com.hlysine.create_connected.content.copycat.fence;

import net.minecraft.world.level.block.FenceBlock;

public class WrappedFenceBlock extends FenceBlock {
    public WrappedFenceBlock(Properties properties) {
        super(properties);
    }

    /**
     * For compatibility with Additional Placements
     */
    public boolean generateAdditionalStates() {
        return false;
    }
}
