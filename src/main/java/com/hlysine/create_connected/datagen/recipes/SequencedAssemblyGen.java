package com.hlysine.create_connected.datagen.recipes;

import com.hlysine.create_connected.CCItems;
import com.hlysine.create_connected.CreateConnected;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.SequencedAssemblyRecipeGen;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public class SequencedAssemblyGen extends SequencedAssemblyRecipeGen {
    GeneratedRecipe CONTROL_CHIP = create("control_chip", b -> b.require(AllItems.GOLDEN_SHEET)
            .transitionTo(CCItems.INCOMPLETE_CONTROL_CHIP.get())
            .addOutput(CCItems.CONTROL_CHIP.get(), 120)
            .addOutput(Items.REDSTONE, 8)
            .addOutput(AllItems.ELECTRON_TUBE.get(), 8)
            .addOutput(AllItems.GOLDEN_SHEET.get(), 5)
            .addOutput(Items.GOLD_NUGGET, 3)
            .addOutput(AllItems.IRON_SHEET.get(), 2)
            .addOutput(AllItems.CRUSHED_GOLD.get(), 2)
            .addOutput(Items.QUARTZ, 1)
            .addOutput(Items.COMPASS, 1)
            .loops(3)
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(AllItems.ELECTRON_TUBE))
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(Items.REDSTONE))
            .addStep(PressingRecipe::new, rb -> rb));

    public SequencedAssemblyGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateConnected.MODID);
    }
}
