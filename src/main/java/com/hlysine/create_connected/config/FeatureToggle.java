package com.hlysine.create_connected.config;

import com.hlysine.create_connected.compat.CreateConnectedJEI;
import com.hlysine.create_connected.compat.Mods;
import com.hlysine.create_connected.mixin.featuretoggle.CreativeModeTabsAccessor;
import com.tterrag.registrate.builders.Builder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class FeatureToggle {
    public static final Set<ResourceLocation> TOGGLEABLE_FEATURES = new HashSet<>();
    public static final Map<ResourceLocation, ResourceLocation> DEPENDENT_FEATURES = new HashMap<>();
    public static final Map<ResourceLocation, Set<FeatureCategory>> FEATURE_CATEGORIES = new HashMap<>();
    public static final Map<ResourceLocation, Supplier<Boolean>> FEATURE_CONDITIONS = new HashMap<>();

    public static void register(ResourceLocation key) {
        TOGGLEABLE_FEATURES.add(key);
    }

    public static void register(ResourceLocation key, FeatureCategory... categories) {
        register(key);
        FEATURE_CATEGORIES.put(key, Set.of(categories));
    }

    public static void registerDependent(ResourceLocation key, ResourceLocation dependency) {
        DEPENDENT_FEATURES.put(key, dependency);
    }

    public static void registerDependent(ResourceLocation key, ResourceLocation dependency, FeatureCategory... categories) {
        registerDependent(key, dependency);
        FEATURE_CATEGORIES.put(key, Set.of(categories));
    }

    public static void addCondition(ResourceLocation key, Supplier<Boolean> condition) {
        FEATURE_CONDITIONS.put(key, condition);
    }

    /**
     * Register this object to be a feature that is toggleable by the user
     */
    public static <R, T extends R, P, S extends Builder<R, T, P, S>> NonNullUnaryOperator<S> register() {
        return b -> {
            register(ResourceLocation.fromNamespaceAndPath(b.getOwner().getModid(), b.getName()));
            return b;
        };
    }

    /**
     * Register this object to be a feature that is toggleable by the user
     */
    public static <R, T extends R, P, S extends Builder<R, T, P, S>> NonNullUnaryOperator<S> register(FeatureCategory... categories) {
        return b -> {
            register(ResourceLocation.fromNamespaceAndPath(b.getOwner().getModid(), b.getName()), categories);
            return b;
        };
    }

    /**
     * Register this object to be dependent on another feature.
     * This object cannot be toggled directly, and will only be enabled if the dependency is enabled.
     */
    public static <R, T extends R, P, S extends Builder<R, T, P, S>> NonNullUnaryOperator<S> registerDependent(ResourceLocation dependency) {
        return b -> {
            registerDependent(ResourceLocation.fromNamespaceAndPath(b.getOwner().getModid(), b.getName()), dependency);
            return b;
        };
    }

    /**
     * Register this object to be dependent on another feature.
     * This object cannot be toggled directly, and will only be enabled if the dependency is enabled.
     */
    public static <R, T extends R, P, S extends Builder<R, T, P, S>> NonNullUnaryOperator<S> registerDependent(ResourceLocation dependency, FeatureCategory... categories) {
        return b -> {
            registerDependent(ResourceLocation.fromNamespaceAndPath(b.getOwner().getModid(), b.getName()), dependency, categories);
            return b;
        };
    }

    /**
     * Register this object to be dependent on another feature.
     * This object cannot be toggled directly, and will only be enabled if the dependency is enabled.
     */
    public static <R, T extends R, P, S extends Builder<R, T, P, S>> NonNullUnaryOperator<S> registerDependent(BlockEntry<?> dependency) {
        return b -> {
            registerDependent(ResourceLocation.fromNamespaceAndPath(b.getOwner().getModid(), b.getName()), dependency.getId());
            return b;
        };
    }

    /**
     * Register this object to be dependent on another feature.
     * This object cannot be toggled directly, and will only be enabled if the dependency is enabled.
     */
    public static <R, T extends R, P, S extends Builder<R, T, P, S>> NonNullUnaryOperator<S> registerDependent(BlockEntry<?> dependency, FeatureCategory... categories) {
        return b -> {
            registerDependent(ResourceLocation.fromNamespaceAndPath(b.getOwner().getModid(), b.getName()), dependency.getId(), categories);
            return b;
        };
    }

    /**
     * Add a condition to this feature.
     */
    public static <R, T extends R, P, S extends Builder<R, T, P, S>> NonNullUnaryOperator<S> addCondition(Supplier<Boolean> condition) {
        return b -> {
            addCondition(ResourceLocation.fromNamespaceAndPath(b.getOwner().getModid(), b.getName()), condition);
            return b;
        };
    }

    private static CFeatures getToggles() {
        return CCConfigs.common().toggle;
    }

    private static CFeatureCategories getCategories() {
        return CCConfigs.common().categories;
    }

    /**
     * Check whether a feature is enabled.
     * If the provided {@link ResourceLocation} is not registered with this feature toggle, it is assumed to be enabled.
     *
     * @param key The {@link ResourceLocation} of the feature.
     * @return Whether the feature is enabled.
     */
    public static boolean isEnabled(ResourceLocation key) {
        if (FEATURE_CATEGORIES.containsKey(key)) {
            Set<FeatureCategory> categories = FEATURE_CATEGORIES.get(key);
            for (FeatureCategory category : categories) {
                if (!getCategories().isEnabled(category)) return false;
            }
        }
        if (FEATURE_CONDITIONS.containsKey(key)) {
            if (!FEATURE_CONDITIONS.get(key).get()) return false;
        }
        if (getToggles().hasToggle(key)) {
            return getToggles().isEnabled(key);
        } else {
            ResourceLocation dependency = DEPENDENT_FEATURES.get(key);
            if (dependency != null) return isEnabled(dependency);
        }
        return true;
    }

    static void refreshItemVisibility() {
        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> {
            CreativeModeTab.ItemDisplayParameters cachedParameters = CreativeModeTabsAccessor.getCACHED_PARAMETERS();
            if (cachedParameters != null) {
                CreativeModeTabsAccessor.callBuildAllTabContents(cachedParameters);
            }
            Mods.JEI.executeIfInstalled(() -> CreateConnectedJEI::refreshItemList);
        });
    }
}
