package com.hlysine.create_connected.datagen.recipes;

import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class CreateConnectedProcessingRecipeGen {
    public static void registerAllProcessing(DataGenerator gen, PackOutput output) {
        final List<ProcessingRecipeGen> GENERATORS = new ArrayList<>();

        GENERATORS.add(new CuttingRecipeGen(output));
        GENERATORS.add(new FillingRecipeGen(output));
        GENERATORS.add(new ItemApplicationRecipeGen(output));

        gen.addProvider(true, new DataProvider() {

            @Override
            public String getName() {
                return "Create: Connected's Processing Recipes";
            }

            @Override
            public CompletableFuture<?> run(CachedOutput dc) {
                return CompletableFuture.allOf(GENERATORS.stream()
                        .map(gen -> gen.run(dc))
                        .toArray(CompletableFuture[]::new));
            }
        });
    }
}