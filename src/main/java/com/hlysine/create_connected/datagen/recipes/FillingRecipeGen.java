package com.hlysine.create_connected.datagen.recipes;

import com.hlysine.create_connected.CCBlocks;
import com.simibubi.create.AllRecipeTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.material.Fluids;

@SuppressWarnings("unused")
public class FillingRecipeGen extends ProcessingRecipeGen {

    GeneratedRecipe FAN_BLASTING_CATALYST = create("fan_blasting_catalyst", b -> b.require(Fluids.LAVA, 1000)
            .require(CCBlocks.EMPTY_FAN_CATALYST.get())
            .withCondition(new FeatureEnabledCondition(CCBlocks.EMPTY_FAN_CATALYST.getId()))
            .output(CCBlocks.FAN_BLASTING_CATALYST.get()));

    GeneratedRecipe FAN_SPLASHING_CATALYST = create("fan_splashing_catalyst", b -> b.require(Fluids.WATER, 1000)
            .require(CCBlocks.EMPTY_FAN_CATALYST.get())
            .withCondition(new FeatureEnabledCondition(CCBlocks.EMPTY_FAN_CATALYST.getId()))
            .output(CCBlocks.FAN_SPLASHING_CATALYST.get()));

    public FillingRecipeGen(DataGenerator output) {
        super(output);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.FILLING;
    }

}

