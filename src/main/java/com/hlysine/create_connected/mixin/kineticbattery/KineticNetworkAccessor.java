package com.hlysine.create_connected.mixin.kineticbattery;

import com.simibubi.create.content.kinetics.KineticNetwork;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KineticNetwork.class)
public interface KineticNetworkAccessor {
    @Accessor
    float getUnloadedStress();
}
