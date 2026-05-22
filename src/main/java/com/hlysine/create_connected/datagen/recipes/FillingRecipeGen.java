package com.hlysine.create_connected.datagen.recipes;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.CCTags;
import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.compat.Mods;
import com.simibubi.create.AllFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class FillingRecipeGen extends com.simibubi.create.api.data.recipe.FillingRecipeGen {

    GeneratedRecipe FAN_BLASTING_CATALYST = create("fan_blasting_catalyst", b -> b.require(Fluids.LAVA, 1000)
            .require(CCBlocks.EMPTY_FAN_CATALYST)
            .withCondition(new FeatureEnabledCondition(CCBlocks.EMPTY_FAN_CATALYST.getId()))
            .output(CCBlocks.FAN_BLASTING_CATALYST));

    GeneratedRecipe FAN_SPLASHING_CATALYST = create("fan_splashing_catalyst", b -> b.require(Fluids.WATER, 1000)
            .require(CCBlocks.EMPTY_FAN_CATALYST)
            .withCondition(new FeatureEnabledCondition(CCBlocks.EMPTY_FAN_CATALYST.getId()))
            .output(CCBlocks.FAN_SPLASHING_CATALYST));

    GeneratedRecipe FAN_ENDING_CATALYST_DRAGONS_BREATH = create("fan_ending_catalyst_dragons_breath", b -> b.require(CCTags.Fluids.FAN_PROCESSING_CATALYSTS_ENDING.tag, 1000)
            .require(CCBlocks.EMPTY_FAN_CATALYST)
            .withCondition(new FeatureEnabledCondition(CCBlocks.EMPTY_FAN_CATALYST.getId()))
            .withCondition(new ModLoadedCondition(Mods.DRAGONS_PLUS.id()))
            .output(CCBlocks.FAN_ENDING_CATALYST_DRAGONS_BREATH));

    GeneratedRecipe FAN_CHOCOLATE_COATING_CATALYST = create("fan_chocolate_coating_catalyst", b -> b.require(AllFluids.CHOCOLATE.get(), 1000)
            .require(CCBlocks.EMPTY_FAN_CATALYST)
            .withCondition(new FeatureEnabledCondition(CCBlocks.EMPTY_FAN_CATALYST.getId()))
            .withCondition(new ModLoadedCondition(Mods.MORE_CATALYSTS.id()))
            .output(CCBlocks.FAN_CHOCOLATE_COATING_CATALYST));

    GeneratedRecipe FAN_HONEY_COATING_CATALYST = create("fan_honey_coating_catalyst", b -> b.require(AllFluids.HONEY.get(), 1000)
            .require(CCBlocks.EMPTY_FAN_CATALYST)
            .withCondition(new FeatureEnabledCondition(CCBlocks.EMPTY_FAN_CATALYST.getId()))
            .withCondition(new ModLoadedCondition(Mods.MORE_CATALYSTS.id()))
            .output(CCBlocks.FAN_HONEY_COATING_CATALYST));

    GeneratedRecipe FAN_TRANSMUTATION_CATALYST = create("fan_transmutation_catalyst", b -> b.require(new SizedFluidIngredient(new SimpleFluidIngredient(Mods.SHIMMER, "shimmer"), 1000))
            .require(CCBlocks.EMPTY_FAN_CATALYST)
            .withCondition(new FeatureEnabledCondition(CCBlocks.EMPTY_FAN_CATALYST.getId()))
            .withCondition(new ModLoadedCondition(Mods.SHIMMER.id()))
            .output(CCBlocks.FAN_TRANSMUTATION_CATALYST));

    public FillingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateConnected.MODID);
    }
}

