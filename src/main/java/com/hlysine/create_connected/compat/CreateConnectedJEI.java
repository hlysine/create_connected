package com.hlysine.create_connected.compat;

import com.hlysine.create_connected.CreateConnected;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class CreateConnectedJEI implements IModPlugin {
    private static final ResourceLocation ID = CreateConnected.asResource("jei_plugin");

    public static IIngredientManager MANAGER;

    @Override
    @NotNull
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerAdvanced(IAdvancedRegistration registration) {
        MANAGER = registration.getJeiHelpers().getIngredientManager();
    }
}
