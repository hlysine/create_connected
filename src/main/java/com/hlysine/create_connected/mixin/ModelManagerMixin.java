package com.hlysine.create_connected.mixin;

import com.hlysine.create_connected.datagen.CCDynamicModels;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ModelManager.class)
public class ModelManagerMixin {
    @Inject(
            at = @At("RETURN"),
            method = "loadBlockStates(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;",
            cancellable = true
    )
    private static void addCustomBlockStates(ResourceManager pResourceManager, Executor p_249943_, CallbackInfoReturnable<CompletableFuture<Map<ResourceLocation, List<ModelBakery.LoadedJson>>>> cir) {
        cir.setReturnValue(cir.getReturnValue().thenApply(map -> {
            Map<ResourceLocation, List<ModelBakery.LoadedJson>> mutMap = new HashMap<>(map);
            CCDynamicModels.onLoadBlockStates(mutMap);
            return Collections.unmodifiableMap(mutMap);
        }));
    }
}
