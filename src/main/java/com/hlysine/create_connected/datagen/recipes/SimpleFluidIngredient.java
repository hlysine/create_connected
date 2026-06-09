package com.hlysine.create_connected.datagen.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hlysine.create_connected.compat.Mods;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class SimpleFluidIngredient extends Ingredient {

    private final Mods mod;
    private final String id;
    private final int amount;

    public SimpleFluidIngredient(Mods mod, String id, int amount) {
        super(Stream.empty());
        this.mod = mod;
        this.id = id;
        this.amount = amount;
    }

    @Override
    public @NotNull JsonElement toJson() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("fluid", mod.rl(id)
                .toString());
        jsonobject.addProperty("amount", amount);
        jsonobject.add("nbt", new JsonObject());
        return jsonobject;
    }

}
