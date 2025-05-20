package com.hlysine.create_connected.compat;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * For compatibility with and without another mod present, we have to define load conditions of the specific code
 */
public enum Mods {
    JEI("jei"),
    COPYCATS("copycats"),
    DIAGONAL_FENCES("diagonalfences"),
    DREAMS_DESIRES("create_henry"),
    HENRY("create_henry"),
    ADDITIONAL_PLACEMENTS("additionalplacements"),
    GARNISHED("garnished");

    private final String id;

    Mods(String id) {
        this.id = id;
    }

    /**
     * @return the mod id
     */
    public String id() {
        return id;
    }

    public ResourceLocation rl(String path) {
        return new ResourceLocation(id, path);
    }

    public Item getItem(String id) {
        return ForgeRegistries.ITEMS.getValue(rl(id));
    }

    public Item getItem(ResourceLocation id) {
        return ForgeRegistries.ITEMS.getValue(id);
    }

    /**
     * Get an ingredient for data generation of crafting recipes without having the mod installed.
     */
    public Ingredient getIngredient(String id) {
        return new Ingredient(Stream.of(new Ingredient.Value() {
            @Override
            public @NotNull Collection<ItemStack> getItems() {
                return List.of();
            }

            @Override
            public @NotNull JsonObject serialize() {
                JsonObject jsonobject = new JsonObject();// 248
                jsonobject.addProperty("item", rl(id).toString());// 249
                return jsonobject;// 250
            }
        }));
    }

    /**
     * @return a boolean of whether the mod is loaded or not based on mod id
     */
    public boolean isLoaded() {
        return ModList.get().isLoaded(id);
    }

    /**
     * Simple hook to run code if a mod is installed
     *
     * @param toRun will be run only if the mod is loaded
     * @return Optional.empty() if the mod is not loaded, otherwise an Optional of the return value of the given supplier
     */
    public <T> Optional<T> runIfInstalled(Supplier<Supplier<T>> toRun) {
        if (isLoaded())
            return Optional.of(toRun.get().get());
        return Optional.empty();
    }

    /**
     * Simple hook to execute code if a mod is installed
     *
     * @param toExecute will be executed only if the mod is loaded
     */
    public void executeIfInstalled(Supplier<Runnable> toExecute) {
        if (isLoaded()) {
            toExecute.get().run();
        }
    }
}

