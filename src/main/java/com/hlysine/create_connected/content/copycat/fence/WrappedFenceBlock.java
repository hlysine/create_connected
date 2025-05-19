package com.hlysine.create_connected.content.copycat.fence;

import com.hlysine.create_connected.content.copycat.IWrappedBlock;
import net.minecraft.world.level.block.FenceBlock;

public class WrappedFenceBlock extends FenceBlock implements IWrappedBlock {
    public WrappedFenceBlock(Properties properties) {
        super(properties);
    }
}
