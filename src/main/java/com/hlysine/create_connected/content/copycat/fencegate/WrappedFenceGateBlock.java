package com.hlysine.create_connected.content.copycat.fencegate;

import net.minecraft.world.level.block.FenceGateBlock;

public class WrappedFenceGateBlock extends FenceGateBlock {
    public WrappedFenceGateBlock(Properties pProperties) {
        super(pProperties);
    }

    /**
     * For compatibility with Additional Placements
     */
    public boolean generateAdditionalStates() {
        return false;
    }
}
