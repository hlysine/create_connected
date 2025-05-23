package com.hlysine.create_connected.mixin;

import com.hlysine.create_connected.content.IConnectionForwardingBlock;
import com.hlysine.create_connected.content.ISplitShaftBlockEntity;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.DirectionalShaftHalvesBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlockEntity;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RotationPropagator.class)
public abstract class RotationPropagatorMixin {

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

    @Inject(
            method = "getPotentialNeighbourLocations",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void forwardConnection(KineticBlockEntity be, CallbackInfoReturnable<List<BlockPos>> cir) {
        List<BlockPos> positions = cir.getReturnValue();
        for (int i = 0; i < positions.size(); i++) {
            BlockPos sourcePos = be.getBlockPos();
            BlockPos neighborPos = positions.get(i);
            while (sourcePos != neighborPos && be.getLevel().getBlockState(neighborPos).getBlock() instanceof IConnectionForwardingBlock forwardingBlock) {
                BlockPos tempSource = sourcePos;
                sourcePos = neighborPos;
                neighborPos = forwardingBlock.forwardConnection(be.getLevel(), tempSource, be.getLevel().getBlockState(tempSource), neighborPos);
            }

            positions.set(i, neighborPos);
        }
        cir.setReturnValue(positions);
    }
}
