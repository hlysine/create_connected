package com.hlysine.create_connected.datagen.recipes;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.CCTags;
import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.compat.Mods;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;

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

    GeneratedRecipe FAN_ENDING_CATALYST_DRAGONS_BREATH = create("fan_ending_catalyst_dragons_breath", b -> b.require(FluidIngredient.fromTag(CCTags.Fluids.FAN_PROCESSING_CATALYSTS_ENDING.tag, 1000))
            .require(CCBlocks.EMPTY_FAN_CATALYST)
            .withCondition(new FeatureEnabledCondition(CCBlocks.EMPTY_FAN_CATALYST.getId()))
            .withCondition(new ModLoadedCondition(Mods.DRAGONS_PLUS.id()))
            .output(CCBlocks.FAN_ENDING_CATALYST_DRAGONS_BREATH));

    public FillingRecipeGen(PackOutput output) {
        super(output, CreateConnected.MODID);
    }
}

