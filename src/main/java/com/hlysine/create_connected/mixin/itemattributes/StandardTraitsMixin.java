package com.hlysine.create_connected.mixin.itemattributes;

import com.hlysine.create_connected.CCItemAttributes;
import com.simibubi.create.content.logistics.filter.ItemAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemAttribute.StandardTraits.class, remap = false)
public class StandardTraitsMixin {
    @Inject(method = "<clinit>()V", at = @At("TAIL"))
    private static void registerAdditional(CallbackInfo ci) {
        CCItemAttributes.register();
    }
}
