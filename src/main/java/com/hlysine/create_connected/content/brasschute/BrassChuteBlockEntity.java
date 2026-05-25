package com.hlysine.create_connected.content.brasschute;

import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BrassChuteBlockEntity extends ChuteBlockEntity {
    public BrassChuteBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected int getExtractionAmount() {
        return 64;
    }
}
