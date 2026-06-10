package com.hlysine.create_connected.compat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class DyeDepotCompat {
    public static String getColorNamespace(DyeColor color) {
        if (color.getId() >= 16 && Mods.DYE_DEPOT.isLoaded()) {
            return Mods.DYE_DEPOT.id();
        }
        return ResourceLocation.DEFAULT_NAMESPACE;
    }
}
