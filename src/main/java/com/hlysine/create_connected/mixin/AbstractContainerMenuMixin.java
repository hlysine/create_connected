package com.hlysine.create_connected.mixin;

import com.hlysine.create_connected.content.contraption.menu.TrackingContainerLevelAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
    @Inject(
            at = @At("HEAD"),
            method = "stillValid(Lnet/minecraft/world/inventory/ContainerLevelAccess;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/Block;)Z",
            cancellable = true
    )
    private static void stillValid(ContainerLevelAccess pAccess, Player pPlayer, Block pTargetBlock, CallbackInfoReturnable<Boolean> cir) {
        if (pAccess instanceof TrackingContainerLevelAccess) {
            cir.setReturnValue(pAccess.evaluate((level, pos) -> pPlayer.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true));
        }
    }
}
