/*
package com.hlysine.create_connected.mixin.linkedtransmitter;

import dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrottleLeverBlock.class)
public abstract class ThrottleLeverBlockMixin {
    @Inject(
            cancellable = true,
            at = @At("HEAD"),
            method = "onRemove"
    )
    private void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving, CallbackInfo ci) {
        if (state.getBlock() instanceof ThrottleLeverBlock && newState.getBlock() instanceof ThrottleLeverBlock)
            ci.cancel();
    }
}
 */
