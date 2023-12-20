package com.hlysine.create_connected.datagen.recipes;

import com.hlysine.create_connected.CCBlocks;
import com.simibubi.create.AllRecipeTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.material.Fluids;

@SuppressWarnings("unused")
public class FillingRecipeGen extends ProcessingRecipeGen {

    GeneratedRecipe FAN_BLASTING_CATALYST = create("fan_blasting_catalyst", b -> b.require(Fluids.LAVA, 1000)
            .require(CCBlocks.EMPTY_FAN_CATALYST)
            .output(CCBlocks.FAN_BLASTING_CATALYST));

    GeneratedRecipe FAN_SPLASHING_CATALYST = create("fan_splashing_catalyst", b -> b.require(Fluids.WATER, 1000)
            .require(CCBlocks.EMPTY_FAN_CATALYST)
            .output(CCBlocks.FAN_SPLASHING_CATALYST));

    public FillingRecipeGen(PackOutput output) {
        super(output);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.FILLING;
    }

}

