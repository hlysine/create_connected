package com.hlysine.create_connected.mixin;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.CCItems;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnalogLeverBlock.class)
public class AnalogLeverBlockMixin {
    @Inject(
            cancellable = true,
            at = @At("HEAD"),
            method = "use(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"
    )
    private void use(BlockState state,
                     Level worldIn,
                     BlockPos pos,
                     Player player,
                     InteractionHand handIn,
                     BlockHitResult hit,
                     CallbackInfoReturnable<InteractionResult> cir) {
        if (player.isHolding(CCItems.LINKED_TRANSMITTER.get()) && !state.is(CCBlocks.LINKED_ANALOG_LEVER.get())) {
            cir.setReturnValue(InteractionResult.PASS);
            cir.cancel();
        }
    }
}
