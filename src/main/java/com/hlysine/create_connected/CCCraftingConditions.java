package com.hlysine.create_connected;

import com.hlysine.create_connected.datagen.recipes.FeatureEnabledCondition;
import net.minecraftforge.common.crafting.CraftingHelper;

@SuppressWarnings("unused")
public class CCCraftingConditions {
    public static void register() {
        CraftingHelper.register(FeatureEnabledCondition.Serializer.INSTANCE);
    }
}
