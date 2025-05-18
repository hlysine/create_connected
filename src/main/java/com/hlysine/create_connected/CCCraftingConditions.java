package com.hlysine.create_connected;

import com.hlysine.create_connected.datagen.recipes.FeatureEnabledCondition;
import com.hlysine.create_connected.datagen.recipes.FeatureEnabledInCopycatsCondition;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class CCCraftingConditions {
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS =
            DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, CreateConnected.MODID);

    public static final Supplier<MapCodec<FeatureEnabledCondition>> FEATURE_ENABLED =
            CONDITION_CODECS.register("feature_enabled", () -> FeatureEnabledCondition.CODEC);

    public static final Supplier<MapCodec<FeatureEnabledInCopycatsCondition>> FEATURE_ENABLED_IN_COPYCATS =
            CONDITION_CODECS.register("feature_enabled_in_copycats", () -> FeatureEnabledInCopycatsCondition.CODEC);

    public static void register() {
    }
}
