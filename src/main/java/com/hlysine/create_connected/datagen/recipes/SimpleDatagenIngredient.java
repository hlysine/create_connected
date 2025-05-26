package com.hlysine.create_connected.datagen.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hlysine.create_connected.compat.Mods;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class SimpleDatagenIngredient extends Ingredient {

    private final Mods mod;
    private final String id;

    public SimpleDatagenIngredient(Mods mod, String id) {
        super(Stream.empty());
        this.mod = mod;
        this.id = id;
    }

    @Override
    public @NotNull JsonElement toJson() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("item", mod.rl(id)
                .toString());
        return jsonobject;
    }

}
