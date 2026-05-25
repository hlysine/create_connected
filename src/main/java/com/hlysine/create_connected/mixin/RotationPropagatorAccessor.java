package com.hlysine.create_connected.mixin;

import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = RotationPropagator.class, remap = false)
public interface RotationPropagatorAccessor {
    @Invoker
    static KineticBlockEntity callFindConnectedNeighbour(KineticBlockEntity currentTE, BlockPos neighbourPos) {
        throw new NotImplementedException();
    }
}
