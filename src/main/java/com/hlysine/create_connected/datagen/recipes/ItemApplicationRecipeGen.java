package com.hlysine.create_connected.datagen.recipes;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.compat.Mods;
import com.hlysine.create_connected.registries.CCBlocks;
import com.simibubi.create.AllFluids;
import com.simibubi.create.AllItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.OrCondition;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ItemApplicationRecipeGen extends com.simibubi.create.api.data.recipe.ItemApplicationRecipeGen {

    GeneratedRecipe BLASTING_CATALYST = fanCatalystFromEmpty(
            "blasting_catalyst", Items.LAVA_BUCKET, CCBlocks.FAN_BLASTING_CATALYST::asItem);
    GeneratedRecipe SMOKING_CATALYST = fanCatalystFromEmpty(
            "smoking_catalyst", Items.NETHERRACK, CCBlocks.FAN_SMOKING_CATALYST::asItem);
    GeneratedRecipe SPLASHING_CATALYST = fanCatalystFromEmpty(
            "splashing_catalyst", Items.WATER_BUCKET, CCBlocks.FAN_SPLASHING_CATALYST::asItem);
    GeneratedRecipe HAUNTING_CATALYST = fanCatalystFromEmpty(
            "haunting_catalyst", Items.SOUL_SAND, CCBlocks.FAN_HAUNTING_CATALYST::asItem);
    GeneratedRecipe FREEZING_CATALYST = fanCatalystFromEmpty(
            "freezing_catalyst", Items.POWDER_SNOW_BUCKET, CCBlocks.FAN_FREEZING_CATALYST::asItem,
            new OrCondition(
                    new ModLoadedCondition(Mods.DREAMS_DESIRES.id()),
                    new ModLoadedCondition(Mods.HENRY.id()),
                    new ModLoadedCondition(Mods.GARNISHED.id()),
                    new ModLoadedCondition(Mods.DRAGONS_PLUS.id())
            ));
    GeneratedRecipe SEETHING_CATALYST = fanCatalystFromEmpty(
            "seething_catalyst", AllItems.BLAZE_CAKE, CCBlocks.FAN_SEETHING_CATALYST::asItem,
            new OrCondition(
                    new ModLoadedCondition(Mods.DREAMS_DESIRES.id()),
                    new ModLoadedCondition(Mods.HENRY.id())
            ));
    GeneratedRecipe SANDING_CATALYST = fanCatalystFromEmpty(
            "sanding_catalyst", Blocks.SAND, CCBlocks.FAN_SANDING_CATALYST::asItem,
            new OrCondition(
                    new ModLoadedCondition(Mods.DREAMS_DESIRES.id()),
                    new ModLoadedCondition(Mods.HENRY.id()),
                    new ModLoadedCondition(Mods.DRAGONS_PLUS.id())
            ));
    GeneratedRecipe ENRICHED_CATALYST = fanCatalystFromEmpty(
            "enriched_catalyst", new SimpleDatagenIngredient(Mods.NUCLEAR, "enriched_soul_soil"), CCBlocks.FAN_ENRICHED_CATALYST::asItem,
            new ModLoadedCondition(Mods.NUCLEAR.id()));
    GeneratedRecipe ENDING_CATALYST_DRAGONS_BREATH = fanCatalystFromEmpty(
            "ending_catalyst_dragons_breath", new SimpleDatagenIngredient(Mods.DRAGONS_PLUS, "dragon_breath_bucket"), CCBlocks.FAN_ENDING_CATALYST_DRAGONS_BREATH::asItem,
            new ModLoadedCondition(Mods.DRAGONS_PLUS.id()));
    GeneratedRecipe ENDING_CATALYST_DRAGON_HEAD = fanCatalystFromEmpty(
            "ending_catalyst_dragon_head", Blocks.DRAGON_HEAD, CCBlocks.FAN_ENDING_CATALYST_DRAGON_HEAD::asItem,
            new OrCondition(
                    new ModLoadedCondition(Mods.DRAGONS_PLUS.id()),
                    new ModLoadedCondition(Mods.HENRY.id())
            ));
    GeneratedRecipe WITHERING_CATALYST = fanCatalystFromEmpty(
            "withering_catalyst", Items.WITHER_ROSE, CCBlocks.FAN_WITHERING_CATALYST::asItem,
            new ModLoadedCondition(Mods.HENRY.id()));
    GeneratedRecipe CHOCOLATE_COATING_CATALYST = fanCatalystFromEmpty(
            "chocolate_coating_catalyst", AllFluids.CHOCOLATE.getBucket().get(), CCBlocks.FAN_CHOCOLATE_COATING_CATALYST::asItem,
            new ModLoadedCondition(Mods.MORE_CATALYSTS.id()));
    GeneratedRecipe HONEY_COATING_CATALYST = fanCatalystFromEmpty(
            "honey_coating_catalyst", AllFluids.HONEY.getBucket().get(), CCBlocks.FAN_HONEY_COATING_CATALYST::asItem,
            new ModLoadedCondition(Mods.MORE_CATALYSTS.id()));
    GeneratedRecipe EXPLODING_CATALYST = fanCatalystFromEmpty(
            "exploding_catalyst", Blocks.CREEPER_HEAD, CCBlocks.FAN_EXPLODING_CATALYST::asItem,
            new ModLoadedCondition(Mods.MORE_CATALYSTS.id()));
    GeneratedRecipe RESONANCE_CATALYST = fanCatalystFromEmpty(
            "resonance_catalyst", Items.AMETHYST_CLUSTER, CCBlocks.FAN_RESONANCE_CATALYST::asItem,
            new ModLoadedCondition(Mods.MORE_CATALYSTS.id()));
    GeneratedRecipe SCULKING_CATALYST = fanCatalystFromEmpty(
            "sculking_catalyst", Blocks.SCULK_CATALYST, CCBlocks.FAN_SCULKING_CATALYST::asItem,
            new ModLoadedCondition(Mods.MORE_CATALYSTS.id()));
    GeneratedRecipe PURIFYING_CATALYST = fanCatalystFromEmpty(
            "purifying_catalyst", Blocks.BEACON, CCBlocks.FAN_PURIFYING_CATALYST::asItem,
            new ModLoadedCondition(Mods.MORE_CATALYSTS.id()));
    /*
    GeneratedRecipe TRANSMUTATION_CATALYST = fanCatalystFromEmpty(
            "transmutation_catalyst", new SimpleDatagenIngredient(Mods.SHIMMER, "shimmer_bucket"), CCBlocks.FAN_TRANSMUTATION_CATALYST::asItem,
            new ModLoadedCondition(Mods.SHIMMER.id()));
    GeneratedRecipe GLOOMING_CATALYST = fanCatalystFromEmpty(
            "glooming_catalyst", new SimpleDatagenIngredient(Mods.TWILIGHT_FOREST, "exanimate_essence"), CCBlocks.FAN_GLOOMING_CATALYST::asItem,
            new ModLoadedCondition(Mods.SHIMMER.id()));
    GeneratedRecipe SOUL_STRIPPING_CATALYST = fanCatalystFromEmpty(
            "soul_stripping_catalyst", new SimpleDatagenIngredient(Mods.NETHER_INDUSTRY, "mysterious_ancient_mechanism"), CCBlocks.FAN_SOUL_STRIPPING_CATALYST::asItem,
            new ModLoadedCondition(Mods.NETHER_INDUSTRY.id()));
     */

    GeneratedRecipe FAN_DYEING_CATALYSTS = applyFanDyeingCatalysts();

    private GeneratedRecipe applyFanDyeingCatalysts() {
        CCBlocks.FAN_DYEING_CATALYSTS.forEach((color, block) -> {
            fanCatalystFromEmpty(
                    color.getName() + "_dyeing_catalyst_dragons_plus",
                    new SimpleDatagenIngredient(Mods.DRAGONS_PLUS, color.getName() + "_dye_bucket"),
                    block::asItem,
                    new ModLoadedCondition(Mods.DRAGONS_PLUS.id())
            );
            fanCatalystFromEmpty(
                    color.getName() + "_dyeing_catalyst_garnished",
                    new SimpleDatagenIngredient(Mods.GARNISHED, color.getName() + "_mastic_resin_bucket"),
                    block::asItem,
                    new ModLoadedCondition(Mods.GARNISHED.id())
            );
        });
        return null;
    }

    protected GeneratedRecipe fanCatalystFromEmpty(String type, ItemLike ingredient, Supplier<ItemLike> output) {
        return fanCatalystFromEmpty(type, Ingredient.of(ingredient), output);
    }

    protected GeneratedRecipe fanCatalystFromEmpty(String type, ItemLike ingredient, Supplier<ItemLike> output, ICondition condition) {
        return fanCatalystFromEmpty(type, Ingredient.of(ingredient), output, condition);
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

    public ItemApplicationRecipeGen(PackOutput output) {
        super(output, CreateConnected.MODID);
    }
}
