package com.hlysine.create_connected.datagen.recipes;

import com.hlysine.create_connected.CCBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ItemApplicationRecipeGen extends ProcessingRecipeGen {

    GeneratedRecipe BLASTING_CATALYST = fanCatalystFromEmpty(
            "blasting_catalyst", Items.LAVA_BUCKET, CCBlocks.FAN_BLASTING_CATALYST::asItem);
    GeneratedRecipe SMOKING_CATALYST = fanCatalystFromEmpty(
            "smoking_catalyst", Items.NETHERRACK, CCBlocks.FAN_SMOKING_CATALYST::asItem);
    GeneratedRecipe SPLASHING_CATALYST = fanCatalystFromEmpty(
            "splashing_catalyst", Items.WATER_BUCKET, CCBlocks.FAN_SPLASHING_CATALYST::asItem);
    GeneratedRecipe HAUNTING_CATALYST = fanCatalystFromEmpty(
            "haunting_catalyst", Items.SOUL_SAND, CCBlocks.FAN_HAUNTING_CATALYST::asItem);
    GeneratedRecipe FREEZING_CATALYST = fanCatalystFromEmpty(
            "freezing_catalyst", Items.POWDER_SNOW_BUCKET, CCBlocks.FAN_FREEZING_CATALYST::asItem);
    GeneratedRecipe SEETHING_CATALYST = fanCatalystFromEmpty(
            "seething_catalyst", AllItems.BLAZE_CAKE, CCBlocks.FAN_SEETHING_CATALYST::asItem);
    GeneratedRecipe SANDING_CATALYST = fanCatalystFromEmpty(
            "sanding_catalyst", Blocks.SAND, CCBlocks.FAN_SANDING_CATALYST::asItem);

    protected GeneratedRecipe fanCatalystFromEmpty(String type, ItemLike ingredient, Supplier<ItemLike> output) {
        return fanCatalystFromEmpty(type, Ingredient.of(ingredient), output);
    }

    protected GeneratedRecipe fanCatalystFromEmpty(String type, Ingredient ingredient, Supplier<ItemLike> output) {
        return create(type + "_from_empty", b -> b.require(CCBlocks.EMPTY_FAN_CATALYST)
                .require(ingredient)
                .withCondition(new FeatureEnabledCondition(CCBlocks.EMPTY_FAN_CATALYST.getId()))
                .output(output.get()));
    }

    protected GeneratedRecipe fanCatalystFromEmpty(String type, Ingredient ingredient, Supplier<ItemLike> output, ICondition condition) {
        return create(type + "_from_empty", b -> b.require(CCBlocks.EMPTY_FAN_CATALYST)
                .require(ingredient)
                .withCondition(new FeatureEnabledCondition(CCBlocks.EMPTY_FAN_CATALYST.getId()))
                .withCondition(condition)
                .output(output.get()));
    }

    public ItemApplicationRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.ITEM_APPLICATION;
    }

}
