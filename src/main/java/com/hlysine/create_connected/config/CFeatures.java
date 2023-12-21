package com.hlysine.create_connected.config;

import com.simibubi.create.foundation.config.ConfigBase;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CFeatures extends ConfigBase implements IsSynchronized {

    final Map<ResourceLocation, ForgeConfigSpec.ConfigValue<Boolean>> toggles = new HashMap<>();

    Map<ResourceLocation, Boolean> synchronizedToggles;

    public Map<ResourceLocation, ForgeConfigSpec.ConfigValue<Boolean>> getToggles() {
        return toggles;
    }

    @Override
    public void registerAll(ForgeConfigSpec.Builder builder) {
        FeatureToggle.TOGGLEABLE_FEATURES.forEach((r) -> getToggles().put(r, builder.define(r.getPath(), true)));
    }

    public boolean isEnabled(ResourceLocation key) {
        if (this.synchronizedToggles != null) {
            Boolean synced = synchronizedToggles.get(key);
            if (synced != null) return synced;
        }
        ForgeConfigSpec.ConfigValue<Boolean> value = getToggles().get(key);
        if (value != null)
            return value.get();
        return true;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        FeatureToggle.refreshItemVisibility();
    }

    @Override
    public void onReload() {
        super.onReload();
        FeatureToggle.refreshItemVisibility();
    }

    public boolean hasToggle(ResourceLocation key) {
        return getToggles().containsKey(key);
    }

    @Override
    public String getName() {
        return "features";
    }

    @Override
    public void onReceiveConfig(SynchronizedConfig config) {
        List<Pair<ResourceLocation, Boolean>> map = config.map();
        synchronizedToggles = new HashMap<>();
        for (Pair<ResourceLocation, Boolean> pair : map) {
            synchronizedToggles.put(pair.getFirst(), pair.getSecond());
        }
        FeatureToggle.refreshItemVisibility();
    }
}