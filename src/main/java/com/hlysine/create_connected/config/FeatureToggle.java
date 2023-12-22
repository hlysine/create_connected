package com.hlysine.create_connected.config;

import com.hlysine.create_connected.CCCreativeTabs;
import com.hlysine.create_connected.compat.CreateConnectedJEI;
import com.hlysine.create_connected.compat.Mods;
import com.hlysine.create_connected.mixin.CreativeModeTabsAccessor;
import com.tterrag.registrate.builders.Builder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import mezz.jei.api.constants.VanillaTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FeatureToggle {
    public static final Set<ResourceLocation> TOGGLEABLE_FEATURES = new HashSet<>();
    public static final Map<ResourceLocation, ResourceLocation> DEPENDENT_FEATURES = new HashMap<>();

    public static void register(ResourceLocation key) {
        TOGGLEABLE_FEATURES.add(key);
    }

    public static void registerDependent(ResourceLocation key, ResourceLocation dependency) {
        DEPENDENT_FEATURES.put(key, dependency);
    }

    public static <R, T extends R, P, S extends Builder<R, T, P, S>> NonNullUnaryOperator<S> register() {
        return b -> {
            register(new ResourceLocation(b.getOwner().getModid(), b.getName()));
            return b;
        };
    }

    public static <R, T extends R, P, S extends Builder<R, T, P, S>> NonNullUnaryOperator<S> registerDependent(ResourceLocation dependency) {
        return b -> {
            registerDependent(new ResourceLocation(b.getOwner().getModid(), b.getName()), dependency);
            return b;
        };
    }

    public static <R, T extends R, P, S extends Builder<R, T, P, S>> NonNullUnaryOperator<S> registerDependent(BlockEntry<?> dependency) {
        return b -> {
            registerDependent(new ResourceLocation(b.getOwner().getModid(), b.getName()), dependency.getId());
            return b;
        };
    }

    private static CFeatures getToggles() {
        return CCConfigs.common().toggle;
    }

    public static boolean isEnabled(ResourceLocation key) {
        if (getToggles().hasToggle(key)) {
            return getToggles().isEnabled(key);
        } else {
            ResourceLocation dependency = DEPENDENT_FEATURES.get(key);
            if (dependency != null) return isEnabled(dependency);
        }
        return true;
    }

    static void refreshItemVisibility() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                LogicalSidedProvider.WORKQUEUE.get(LogicalSide.CLIENT).submit(() -> {
                    CreativeModeTab.ItemDisplayParameters cachedParameters = CreativeModeTabsAccessor.getCACHED_PARAMETERS();
                    if (cachedParameters != null) {
                        CreativeModeTabsAccessor.callBuildAllTabContents(cachedParameters);
                    }
                    Mods.JEI.executeIfInstalled(() -> () -> {
                        if (CreateConnectedJEI.MANAGER != null) {
                            CreateConnectedJEI.MANAGER.removeIngredientsAtRuntime(
                                    VanillaTypes.ITEM_STACK,
                                    CCCreativeTabs.ITEMS.stream()
                                            .map(ItemProviderEntry::asStack)
                                            .collect(Collectors.toList())
                            );
                            CreateConnectedJEI.MANAGER.addIngredientsAtRuntime(
                                    VanillaTypes.ITEM_STACK,
                                    CCCreativeTabs.ITEMS.stream()
                                            .filter(x -> isEnabled(x.getId()))
                                            .map(ItemProviderEntry::asStack)
                                            .collect(Collectors.toList())
                            );
                        }
                    });
                })
        );
    }
}
