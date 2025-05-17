package com.hlysine.create_connected.compat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * For compatibility with and without another mod present, we have to define load conditions of the specific code
 */
public enum Mods {
    JEI("jei"),
    COPYCATS("copycats"),
    DIAGONAL_FENCES("diagonalfences"),
    DREAMS_DESIRES("create_dd"),
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
        return ResourceLocation.fromNamespaceAndPath(id, path);
    }

    public Item getItem(String id) {
        return BuiltInRegistries.ITEM.get(rl(id));
    }

    public Item getItem(ResourceLocation id) {
        return BuiltInRegistries.ITEM.get(id);
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

