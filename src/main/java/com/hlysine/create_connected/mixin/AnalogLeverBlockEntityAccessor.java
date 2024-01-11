package com.hlysine.create_connected.mixin;

import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AnalogLeverBlockEntity.class)
public interface AnalogLeverBlockEntityAccessor {
    @Accessor
    int getLastChange();
}
