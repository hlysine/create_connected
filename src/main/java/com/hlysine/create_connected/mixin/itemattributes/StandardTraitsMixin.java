package com.hlysine.create_connected.mixin.itemattributes;

import com.hlysine.create_connected.CCItemAttributes;
import com.simibubi.create.content.logistics.item.filter.attribute.legacydeserializers.StandardTraitsLegacyDeserializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = StandardTraitsLegacyDeserializer.class, remap = false)
public class StandardTraitsMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void registerAdditional(CallbackInfo ci) {
        CCItemAttributes.register();
    }
}
