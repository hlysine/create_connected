package com.hlysine.create_connected.config;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CFeatures extends SyncConfigBase {

    @Override
    public @NotNull String getName() {
        return "features";
    }

    final Map<ResourceLocation, ModConfigSpec.ConfigValue<Boolean>> toggles = new HashMap<>();

    Map<ResourceLocation, Boolean> synchronizedToggles;

    @Override
    public void registerAll(ModConfigSpec.Builder builder) {
        FeatureToggle.TOGGLEABLE_FEATURES.forEach((r) -> toggles.put(r, builder.define(r.getPath(), true)));
    }

    public boolean hasToggle(ResourceLocation key) {
        return (synchronizedToggles != null && synchronizedToggles.containsKey(key)) || toggles.containsKey(key);
    }

    public boolean isEnabled(ResourceLocation key) {
        if (this.synchronizedToggles != null) {
            Boolean synced = synchronizedToggles.get(key);
            if (synced != null) return synced;
        }
        ModConfigSpec.ConfigValue<Boolean> value = toggles.get(key);
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

    @Override
    protected void readSyncConfig(CompoundTag nbt) {
        synchronizedToggles = new HashMap<>();
        for (String key : nbt.getAllKeys()) {
            ResourceLocation location = ResourceLocation.parse(key);
            synchronizedToggles.put(location, nbt.getBoolean(key));
        }
        FeatureToggle.refreshItemVisibility();
    }

    @Override
    protected void writeSyncConfig(CompoundTag nbt) {
        toggles.forEach((key, value) -> nbt.putBoolean(key.toString(), value.get()));
    }
}