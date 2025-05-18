package com.hlysine.create_connected.datagen.recipes;

import com.hlysine.create_connected.config.FeatureToggle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

public record FeatureEnabledCondition(ResourceLocation feature) implements ICondition {
    public static final MapCodec<FeatureEnabledCondition> CODEC = RecordCodecBuilder.mapCodec((builder) -> builder
            .group(ResourceLocation.CODEC.fieldOf("tag").forGetter(FeatureEnabledCondition::feature))
            .apply(builder, FeatureEnabledCondition::new)
    );

    @Override
    public boolean test(@NotNull IContext context) {
        return FeatureToggle.isEnabled(feature);
    }

    @Override
    public @NotNull MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

    @Override
    public String toString() {
        return "feature_enabled(\"" + feature + "\")";
    }
}
