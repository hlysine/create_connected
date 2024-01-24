package com.hlysine.create_connected.mixin.copycat;

import com.hlysine.create_connected.content.copycat.CopycatFenceGateBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {
        DirtPathBlock.class,
        FarmBlock.class
})
public class BlockCanSurviveMixin {
    @Inject(
            at = @At("HEAD"),
            method = "canSurvive(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z",
            cancellable = true
    )
    private void canSurviveCopycat(BlockState pState, LevelReader pLevel, BlockPos pPos, CallbackInfoReturnable<Boolean> cir) {
        BlockState blockstate = pLevel.getBlockState(pPos.above());
        if (blockstate.getBlock() instanceof CopycatFenceGateBlock) cir.setReturnValue(true);
    }
}
