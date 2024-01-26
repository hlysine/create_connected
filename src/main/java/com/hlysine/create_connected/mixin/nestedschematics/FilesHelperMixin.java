package com.hlysine.create_connected.mixin.nestedschematics;

import com.hlysine.create_connected.config.CServer;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.simibubi.create.foundation.utility.Lang;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FilesHelper.class, remap = false)
public class FilesHelperMixin {
    @Inject(
            at = @At("HEAD"),
            method = "slug(Ljava/lang/String;)Ljava/lang/String;",
            cancellable = true
    )
    private static void slug(String name, CallbackInfoReturnable<String> cir) {
        if (CServer.SchematicsNestingDepth.get() > 0) {
            cir.setReturnValue(Lang.asId(name)
                    .replaceAll("[^\\w/\\\\]+", "_")
                    .replaceAll("[/\\\\]+", "/"));
        }
    }
}
