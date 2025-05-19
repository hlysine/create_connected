package com.hlysine.create_connected.content.copycat.wall;

import com.hlysine.create_connected.content.copycat.IWrappedBlock;
import net.minecraft.world.level.block.WallBlock;

public class WrappedWallBlock extends WallBlock implements IWrappedBlock {
    public WrappedWallBlock(Properties properties) {
        super(properties);
    }
}
