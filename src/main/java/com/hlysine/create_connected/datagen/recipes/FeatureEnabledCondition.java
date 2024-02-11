package com.hlysine.create_connected.datagen.recipes;

import com.google.gson.JsonObject;
import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.config.FeatureToggle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class FeatureEnabledCondition implements ICondition {
    private static final ResourceLocation NAME = CreateConnected.asResource("feature_enabled");
    private final ResourceLocation feature;

    public FeatureEnabledCondition(ResourceLocation feature) {
        this.feature = feature;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @SuppressWarnings("removal")
    @Override
    public boolean test() {
        return FeatureToggle.isEnabled(feature);
    }

    public static class Serializer implements IConditionSerializer<FeatureEnabledCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, FeatureEnabledCondition value) {
            json.addProperty("feature", value.feature.toString());
        }

        @Override
        public FeatureEnabledCondition read(JsonObject json) {
            return new FeatureEnabledCondition(
                    new ResourceLocation(GsonHelper.getAsString(json, "feature"))
            );
        }

        @Override
        public ResourceLocation getID() {
            return NAME;
        }
    }
}
