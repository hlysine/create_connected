package com.hlysine.create_connected.content.copycat.wall;

import net.minecraft.world.level.block.WallBlock;

public class WrappedWallBlock extends WallBlock {
    public WrappedWallBlock(Properties properties) {
        super(properties);
    }

    /**
     * For compatibility with Additional Placements
     */
    public boolean generateAdditionalStates() {
        return false;
    }
}
