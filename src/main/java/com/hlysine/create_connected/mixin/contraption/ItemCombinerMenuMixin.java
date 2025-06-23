package com.hlysine.create_connected.mixin.contraption;

import com.hlysine.create_connected.compat.ModMixin;
import com.hlysine.create_connected.content.contraption.menu.TrackingContainerLevelAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@ModMixin(mods = {"railways"}, applyIfPresent = false)
@Mixin(ItemCombinerMenu.class)
public class ItemCombinerMenuMixin {
    @Shadow
    @Final
    protected ContainerLevelAccess access;

    @Inject(
            at = @At("HEAD"),
            method = "stillValid",
            cancellable = true
    )
    private void stillValid(Player pPlayer, CallbackInfoReturnable<Boolean> cir) {
        if (access instanceof TrackingContainerLevelAccess) {
            cir.setReturnValue(access.evaluate((level, pos) -> pPlayer.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true));
        }
    }
}
