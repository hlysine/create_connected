package com.hlysine.create_connected.content;

import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ConditionalFanProcessing {
    boolean canApplyFanType(FanProcessingType type, Level level, BlockPos pos, BlockState state);
}
