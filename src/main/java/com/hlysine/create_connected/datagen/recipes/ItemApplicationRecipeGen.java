package com.hlysine.create_connected.datagen.recipes;

import com.hlysine.create_connected.CCBlocks;
import com.simibubi.create.AllRecipeTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ItemApplicationRecipeGen extends ProcessingRecipeGen {

    GeneratedRecipe BLASTING_CATALYST = fanCatalystFromEmpty(
            "blasting_catalyst", Items.LAVA_BUCKET::asItem, CCBlocks.FAN_BLASTING_CATALYST::asItem);
    GeneratedRecipe SMOKING_CATALYST = fanCatalystFromEmpty(
            "smoking_catalyst", Items.NETHERRACK::asItem, CCBlocks.FAN_SMOKING_CATALYST::asItem);
    GeneratedRecipe SPLASHING_CATALYST = fanCatalystFromEmpty(
            "splashing_catalyst", Items.WATER_BUCKET::asItem, CCBlocks.FAN_SPLASHING_CATALYST::asItem);
    GeneratedRecipe HAUNTING_CATALYST = fanCatalystFromEmpty(
            "haunting_catalyst", Items.SOUL_SAND::asItem, CCBlocks.FAN_HAUNTING_CATALYST::asItem);
    GeneratedRecipe FREEZING_CATALYST = fanCatalystFromEmpty(
            "freezing_catalyst", Items.POWDER_SNOW_BUCKET::asItem, CCBlocks.FAN_FREEZING_CATALYST::asItem);

    protected GeneratedRecipe fanCatalystFromEmpty(String type, Supplier<ItemLike> ingredient, Supplier<ItemLike> output) {
        return create(type + "_from_empty", b -> b.require(CCBlocks.EMPTY_FAN_CATALYST)
                .require(ingredient.get())
                .withCondition(new FeatureEnabledCondition(CCBlocks.EMPTY_FAN_CATALYST.getId()))
                .output(output.get()));
    }

    public ItemApplicationRecipeGen(PackOutput output) {
        super(output);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.ITEM_APPLICATION;
    }

}
