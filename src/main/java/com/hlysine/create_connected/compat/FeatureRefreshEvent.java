package com.hlysine.create_connected.compat;

import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

/*
 * Base class of the two feature refresh events.
 *
 * @see FeatureRefreshEvent.Pre
 * @see FeatureRefreshEvent.Post
 */
public class FeatureRefreshEvent extends Event {
    private final ResourceLocation jeiPluginId;
    private final IIngredientManager ingredientManager;

    protected FeatureRefreshEvent(ResourceLocation jeiPluginId, IIngredientManager ingredientManager) {
        this.jeiPluginId = jeiPluginId;
        this.ingredientManager = ingredientManager;
    }

    public ResourceLocation getJeiPluginId() {
        return jeiPluginId;
    }

    public IIngredientManager getIngredientManager() {
        return ingredientManager;
    }

    /**
     * Fired before Create: Connected updates the JEI item list according to enabled features.
     */
    public static class Pre extends FeatureRefreshEvent {
        public Pre(ResourceLocation jeiPluginId, IIngredientManager ingredientManager) {
            super(jeiPluginId, ingredientManager);
        }
    }

    /**
     * Fired after Create: Connected updates the JEI item list according to enabled features.
     */
    public static class Post extends FeatureRefreshEvent {
        public Post(ResourceLocation jeiPluginId, IIngredientManager ingredientManager) {
            super(jeiPluginId, ingredientManager);
        }
    }
}
