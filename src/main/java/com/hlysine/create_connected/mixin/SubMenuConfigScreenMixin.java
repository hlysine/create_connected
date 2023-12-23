package com.hlysine.create_connected.mixin;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.config.CCConfigs;
import com.simibubi.create.foundation.config.ui.ConfigScreen;
import com.simibubi.create.foundation.config.ui.SubMenuConfigScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SubMenuConfigScreen.class, remap = false)
public class SubMenuConfigScreenMixin {
    @Inject(
            method = "saveChanges()V",
            at = @At("TAIL")
    )
    private void saveChangesAndRefresh(CallbackInfo ci) {
        if (ConfigScreen.modID.equals(CreateConnected.MODID)) {
            if (EffectiveSide.get().isServer() || FMLEnvironment.dist == Dist.CLIENT)
                LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER).submit(() -> CCConfigs.common().syncToAllPlayers());
        }
    }
}
