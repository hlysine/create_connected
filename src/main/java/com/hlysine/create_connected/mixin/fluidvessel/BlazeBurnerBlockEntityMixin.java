package com.hlysine.create_connected.mixin.fluidvessel;

import com.hlysine.create_connected.content.fluidvessel.FluidVesselBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlazeBurnerBlockEntity.class)
public class BlazeBurnerBlockEntityMixin {
    @SuppressWarnings("UnreachableCode")
    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"),
            method = "isValidBlockAbove()Z",
            cancellable = true
    )
    private void isValidBlockAbove(CallbackInfoReturnable<Boolean> cir) {
        BlazeBurnerBlockEntity be = ((BlazeBurnerBlockEntity) (Object) this);
        BlockState blockState = be.getLevel().getBlockState(be.getBlockPos().above());
        if (blockState.getBlock() instanceof FluidVesselBlock) {
            cir.setReturnValue(true);
        }
    }
}
