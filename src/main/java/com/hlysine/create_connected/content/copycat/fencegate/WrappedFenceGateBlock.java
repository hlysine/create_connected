package com.hlysine.create_connected.content.copycat.fencegate;

import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.properties.WoodType;

public class WrappedFenceGateBlock extends FenceGateBlock {
    public WrappedFenceGateBlock(WoodType pType, Properties pProperties) {
        super(pType, pProperties);
    }

    /**
     * For compatibility with Additional Placements
     */
    public boolean generateAdditionalStates() {
        return false;
    }
}
