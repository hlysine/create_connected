package com.hlysine.create_connected.registries;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.datagen.recipes.FeatureEnabledCondition;
import com.hlysine.create_connected.datagen.recipes.FeatureEnabledInCopycatsCondition;
import net.minecraftforge.common.crafting.CraftingHelper;

@SuppressWarnings("unused")
public class CCCraftingConditions {
    public static void register() {
        CraftingHelper.register(FeatureEnabledCondition.Serializer.INSTANCE);
        CraftingHelper.register(FeatureEnabledInCopycatsCondition.Serializer.INSTANCE);
    }
}
