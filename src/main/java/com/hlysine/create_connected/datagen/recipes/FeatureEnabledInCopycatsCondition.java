package com.hlysine.create_connected.datagen.recipes;

import com.hlysine.create_connected.compat.CopycatsManager;
import com.hlysine.create_connected.compat.Mods;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

public record FeatureEnabledInCopycatsCondition(ResourceLocation feature) implements ICondition {
    public static final MapCodec<FeatureEnabledInCopycatsCondition> CODEC = RecordCodecBuilder.mapCodec((builder) -> builder
            .group(ResourceLocation.CODEC.fieldOf("tag").forGetter(FeatureEnabledInCopycatsCondition::feature))
            .apply(builder, FeatureEnabledInCopycatsCondition::new)
    );

    @Override
    public boolean test(@NotNull IContext context) {
        return Mods.COPYCATS.runIfInstalled(() -> () -> CopycatsManager.isFeatureEnabled(feature)).orElse(false);
    }

    @Override
    public @NotNull MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

    @Override
    public String toString() {
        return "feature_enabled_in_copycats(\"" + feature + "\")";
    }
}
