package com.hlysine.create_connected.mixin;

import com.hlysine.create_connected.content.ISplitShaftBlockEntity;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.DirectionalShaftHalvesBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlockEntity;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RotationPropagator.class)
public class RotationPropagatorMixin {
    @Inject(
            method = "getAxisModifier",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void getSplitShaftModifier(KineticBlockEntity be, Direction direction, CallbackInfoReturnable<Float> cir) {
        if (!(be.hasSource() || be.isSource()))
            return;

        if (be instanceof ISplitShaftBlockEntity splitShaftBE) {
            cir.setReturnValue(splitShaftBE.getRotationSpeedModifier(direction));
        }
    }
}
