package com.hlysine.create_connected.mixin.copycat;

import com.hlysine.create_connected.content.copycat.CopycatFenceGateBlock;
import com.hlysine.create_connected.content.copycat.WrappedFenceGateBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WallBlock.class)
public class WallBlockMixin {
    @Inject(
            at = @At("HEAD"),
            method = "connectsTo(Lnet/minecraft/world/level/block/state/BlockState;ZLnet/minecraft/core/Direction;)Z",
            cancellable = true
    )
    private void connectsToCopycat(BlockState pState, boolean pSideSolid, Direction pDirection, CallbackInfoReturnable<Boolean> cir) {
        if (pState.getBlock() instanceof CopycatFenceGateBlock && FenceGateBlock.connectsToDirection(pState, pDirection))
            cir.setReturnValue(true);
        else if (pState.getBlock() instanceof WrappedFenceGateBlock && FenceGateBlock.connectsToDirection(pState, pDirection))
            cir.setReturnValue(true);
    }
}
