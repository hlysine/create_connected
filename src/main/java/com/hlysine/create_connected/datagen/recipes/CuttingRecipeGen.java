package com.hlysine.create_connected.datagen.recipes;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.CreateConnected;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class CuttingRecipeGen extends com.simibubi.create.api.data.recipe.CuttingRecipeGen {

    GeneratedRecipe SHEAR_PIN = create(AllBlocks.SHAFT::get, b -> b.duration(200)
            .withCondition(new FeatureEnabledCondition(CCBlocks.SHEAR_PIN.getId()))
            .output(CCBlocks.SHEAR_PIN.get()));

    public CuttingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateConnected.MODID);
    }
}

