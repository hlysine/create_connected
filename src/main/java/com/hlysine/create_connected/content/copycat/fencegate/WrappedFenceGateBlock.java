package com.hlysine.create_connected.content.copycat.fencegate;

import com.hlysine.create_connected.content.copycat.IWrappedBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.properties.WoodType;

public class WrappedFenceGateBlock extends FenceGateBlock implements IWrappedBlock {
    public WrappedFenceGateBlock(WoodType pType, Properties pProperties) {
        super(pType, pProperties);
    }
}
