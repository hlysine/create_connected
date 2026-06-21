package com.hlysine.create_connected.compat;

import com.hlysine.create_connected.registries.CCCreativeTabs;
import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.config.FeatureToggle;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

@JeiPlugin
public class CreateConnectedJEI implements IModPlugin {
    private static final ResourceLocation ID = CreateConnected.asResource("jei_plugin");

    public static IIngredientManager MANAGER;

    @Override
    @NotNull
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        MANAGER = jeiRuntime.getIngredientManager();
    }

    public static void refreshItemList() {
        if (MANAGER != null && Minecraft.getInstance().level != null) {
            MinecraftForge.EVENT_BUS.post(new FeatureRefreshEvent.Pre(ID, MANAGER));
            MANAGER.removeIngredientsAtRuntime(
                    VanillaTypes.ITEM_STACK,
                    CCCreativeTabs.ITEMS.stream()
                            .map(ItemProviderEntry::asStack)
                            .collect(Collectors.toList())
            );
            MANAGER.addIngredientsAtRuntime(
                    VanillaTypes.ITEM_STACK,
                    CCCreativeTabs.ITEMS.stream()
                            .filter(x -> FeatureToggle.isEnabled(x.getId()))
                            .map(ItemProviderEntry::asStack)
                            .collect(Collectors.toList())
            );
            MinecraftForge.EVENT_BUS.post(new FeatureRefreshEvent.Post(ID, MANAGER));
        }
    }
}
