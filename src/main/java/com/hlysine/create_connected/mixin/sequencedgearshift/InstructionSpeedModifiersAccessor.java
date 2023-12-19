package com.hlysine.create_connected.mixin.sequencedgearshift;

import com.simibubi.create.content.kinetics.transmission.sequencer.InstructionSpeedModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = InstructionSpeedModifiers.class, remap = false)
public interface InstructionSpeedModifiersAccessor {
    @Accessor
    int getValue();
}
