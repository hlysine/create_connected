package com.hlysine.create_connected.mixin;

import com.hlysine.create_connected.PreciseItemUseOverrides;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.foundation.block.ItemUseOverrides;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemUseOverrides.class)
public class ItemUseOverridesMixin {
    @WrapOperation(
            method = "onBlockActivated",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/BlockHelper;invokeUse(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;")
    )
    private static InteractionResult preciseHitLocation(BlockState instance,
                                                        Level level,
                                                        Player player,
                                                        InteractionHand interactionHand,
                                                        BlockHitResult blockHitResult,
                                                        Operation<InteractionResult> original,
                                                        @Local ResourceLocation id) {
        if (PreciseItemUseOverrides.OVERRIDES.contains(id)) {
            HitResult preciseHitResult = player.pick(player.blockInteractionRange(), 1, false);
            if (preciseHitResult instanceof BlockHitResult preciseBlockHitResult) {
                // Ensures that preciseBlockHitResult has the same block pos as blockHitResult
                // to prevent an unexpected block from being selected because of de-synced rotation (#56)
                if (preciseBlockHitResult.getBlockPos().equals(blockHitResult.getBlockPos())) {
                    return original.call(instance, level, player, interactionHand, preciseBlockHitResult);
                }
            }
        }
        return original.call(instance, level, player, interactionHand, blockHitResult);
    }
}
