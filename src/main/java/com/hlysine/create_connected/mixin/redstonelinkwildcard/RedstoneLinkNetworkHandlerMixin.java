package com.hlysine.create_connected.mixin.redstonelinkwildcard;

import com.hlysine.create_connected.content.redstonelinkwildcard.LinkWildcardNetworkHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(RedstoneLinkNetworkHandler.class)
public class RedstoneLinkNetworkHandlerMixin {
    @WrapOperation(
            method = "updateNetworkOf",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/redstone/link/RedstoneLinkNetworkHandler;getNetworkOf(Lnet/minecraft/world/level/LevelAccessor;Lcom/simibubi/create/content/redstone/link/IRedstoneLinkable;)Ljava/util/Set;")
    )
    private Set<IRedstoneLinkable> updateNetworkOf(RedstoneLinkNetworkHandler instance, LevelAccessor world, IRedstoneLinkable actor, Operation<Set<IRedstoneLinkable>> original) {
        Set<IRedstoneLinkable> result = LinkWildcardNetworkHandler.getNetworkOf(instance, world, actor);
        if (result != null) {
            return result;
        } else {
            return original.call(instance, world, actor);
        }
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
