package com.hlysine.create_connected.datagen.recipes;

import com.google.gson.JsonObject;
import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.compat.CopycatsManager;
import com.hlysine.create_connected.compat.Mods;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class FeatureEnabledInCopycatsCondition implements ICondition {
    private static final ResourceLocation NAME = CreateConnected.asResource("feature_enabled_in_copycats");
    private final ResourceLocation feature;

    public FeatureEnabledInCopycatsCondition(ResourceLocation feature) {
        this.feature = feature;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        return Mods.COPYCATS.runIfInstalled(() -> () -> CopycatsManager.isFeatureEnabled(feature)).orElse(false);
    }

    public static class Serializer implements IConditionSerializer<FeatureEnabledInCopycatsCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, FeatureEnabledInCopycatsCondition value) {
            json.addProperty("feature", value.feature.toString());
        }

        @Override
        public FeatureEnabledInCopycatsCondition read(JsonObject json) {
            return new FeatureEnabledInCopycatsCondition(
                    new ResourceLocation(GsonHelper.getAsString(json, "feature"))
            );
        }

        @Override
        public ResourceLocation getID() {
            return NAME;
        }
    }
}
