package com.hlysine.create_connected.mixin.redstonelinkwildcard;

import com.hlysine.create_connected.content.redstonelinkwildcard.LinkWildcardNetworkHandler;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(value = RedstoneLinkNetworkHandler.class, remap = false)
public class RedstoneLinkNetworkHandlerMixin {
    @Inject(
            method = "updateNetworkOf",
            at = @At("RETURN")
    )
    private void updateNetworkOf(LevelAccessor world, IRedstoneLinkable actor, CallbackInfo ci, @Local(ordinal = 0) int power) {
        LinkWildcardNetworkHandler.updateWildcardNetworkOf((RedstoneLinkNetworkHandler) (Object) this, world, actor, power);
    }

    @Inject(
            method = "addToNetwork",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/redstone/link/RedstoneLinkNetworkHandler;updateNetworkOf(Lnet/minecraft/world/level/LevelAccessor;Lcom/simibubi/create/content/redstone/link/IRedstoneLinkable;)V")
    )
    private void addToNetwork(LevelAccessor world, IRedstoneLinkable actor, CallbackInfo ci) {
        LinkWildcardNetworkHandler.addToNetwork((RedstoneLinkNetworkHandler) (Object) this, world, actor);
    }

    @Inject(
            method = "removeFromNetwork",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;isEmpty()Z", shift = At.Shift.AFTER)
    )
    private void removeFromNetwork(LevelAccessor world, IRedstoneLinkable actor, CallbackInfo ci) {
        LinkWildcardNetworkHandler.removeFromNetwork((RedstoneLinkNetworkHandler) (Object) this, world, actor);
    }
}
