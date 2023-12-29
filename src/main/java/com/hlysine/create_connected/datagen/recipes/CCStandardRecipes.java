package com.hlysine.create_connected.datagen.recipes;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.CCItems;
import com.hlysine.create_connected.CreateConnected;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@SuppressWarnings("unused")
public class CCStandardRecipes extends CreateRecipeProvider {
    private final Marker KINETICS = enterFolder("kinetics");

    GeneratedRecipe ENCASED_CHAIN_COGWHEEL = create(CCBlocks.ENCASED_CHAIN_COGWHEEL).unlockedBy(AllBlocks.ENCASED_CHAIN_DRIVE::get)
            .requiresResultFeature()
            .viaShapeless(b -> b
                    .requires(AllBlocks.ENCASED_CHAIN_DRIVE.get())
                    .requires(AllBlocks.COGWHEEL.get())
            );

    GeneratedRecipe INVERTED_CLUTCH_CYCLE =
            conversionCycle(ImmutableList.of(AllBlocks.CLUTCH, CCBlocks.INVERTED_CLUTCH));

    GeneratedRecipe INVERTED_GEARSHIFT_CYCLE =
            conversionCycle(ImmutableList.of(AllBlocks.GEARSHIFT, CCBlocks.INVERTED_GEARSHIFT));

    GeneratedRecipe PARALLEL_GEARBOX = create(CCBlocks.PARALLEL_GEARBOX).unlockedBy(AllBlocks.LARGE_COGWHEEL::get)
            .requiresResultFeature()
            .viaShapeless(b -> b
                    .requires(AllBlocks.GEARBOX.get())
                    .requires(AllBlocks.LARGE_COGWHEEL.get())
            );

    GeneratedRecipe PARALLEL_GEARBOX_CYCLE =
            conversionCycle(ImmutableList.of(CCBlocks.PARALLEL_GEARBOX, CCItems.VERTICAL_PARALLEL_GEARBOX));

    GeneratedRecipe SIX_WAY_GEARBOX = create(CCBlocks.SIX_WAY_GEARBOX).unlockedBy(AllBlocks.LARGE_COGWHEEL::get)
            .requiresResultFeature()
            .viaShaped(b -> b
                    .define('c', AllBlocks.COGWHEEL.get())
                    .define('l', AllBlocks.LARGE_COGWHEEL.get())
                    .define('s', AllBlocks.ANDESITE_CASING.get())
                    .pattern("lc ")
                    .pattern("csc")
                    .pattern(" cl")
            );

    GeneratedRecipe SIX_WAY_GEARBOX_FROM_GEARBOX = create(CCBlocks.SIX_WAY_GEARBOX).withSuffix("_from_gearbox").unlockedBy(AllBlocks.GEARBOX::get)
            .requiresResultFeature()
            .viaShapeless(b -> b
                    .requires(AllBlocks.GEARBOX.get())
                    .requires(AllBlocks.LARGE_COGWHEEL.get())
                    .requires(AllBlocks.LARGE_COGWHEEL.get())
            );

    GeneratedRecipe SIX_WAY_GEARBOX_FROM_PARALLEL = create(CCBlocks.SIX_WAY_GEARBOX).withSuffix("_from_parallel").unlockedBy(CCBlocks.PARALLEL_GEARBOX::get)
            .requiresResultFeature()
            .requiresFeature(CCBlocks.PARALLEL_GEARBOX)
            .viaShapeless(b -> b
                    .requires(CCBlocks.PARALLEL_GEARBOX.get())
                    .requires(AllBlocks.LARGE_COGWHEEL.get())
            );

    GeneratedRecipe SIX_WAY_GEARBOX_CYCLE =
            conversionCycle(ImmutableList.of(CCBlocks.SIX_WAY_GEARBOX, CCItems.VERTICAL_SIX_WAY_GEARBOX));

    GeneratedRecipe BRASS_GEARBOX = create(CCBlocks.BRASS_GEARBOX).unlockedBy(AllBlocks.ROTATION_SPEED_CONTROLLER::get)
            .requiresResultFeature()
            .viaShaped(b -> b
                    .define('c', AllBlocks.COGWHEEL.get())
                    .define('s', AllBlocks.ROTATION_SPEED_CONTROLLER.get())
                    .pattern(" c ")
                    .pattern("csc")
                    .pattern(" c ")
            );

    GeneratedRecipe BRASS_GEARBOX_CYCLE =
            conversionCycle(ImmutableList.of(CCBlocks.BRASS_GEARBOX, CCItems.VERTICAL_BRASS_GEARBOX));

    GeneratedRecipe OVERSTRESS_CLUTCH = create(CCBlocks.OVERSTRESS_CLUTCH).unlockedBy(AllItems.ELECTRON_TUBE::get)
            .requiresResultFeature()
            .viaShapeless(b -> b
                    .requires(AllBlocks.ANDESITE_CASING.get())
                    .requires(AllBlocks.SHAFT.get())
                    .requires(AllItems.IRON_SHEET.get())
                    .requires(AllItems.ELECTRON_TUBE.get())
            );

    GeneratedRecipe CENTRIFUGAL_CLUTCH = create(CCBlocks.CENTRIFUGAL_CLUTCH).unlockedBy(AllBlocks.SPEEDOMETER::get)
            .requiresResultFeature()
            .viaShapeless(b -> b
                    .requires(AllBlocks.ANDESITE_CASING.get())
                    .requires(AllBlocks.SHAFT.get())
                    .requires(AllItems.IRON_SHEET.get())
                    .requires(AllBlocks.SPEEDOMETER.get())
            );

    GeneratedRecipe FREEWHEEL_CLUTCH = create(CCBlocks.FREEWHEEL_CLUTCH).unlockedBy(AllBlocks.COGWHEEL::get)
            .requiresResultFeature()
            .viaShapeless(b -> b
                    .requires(AllBlocks.ANDESITE_CASING.get())
                    .requires(AllBlocks.SHAFT.get())
                    .requires(AllItems.IRON_SHEET.get())
                    .requires(AllBlocks.COGWHEEL.get())
            );

    GeneratedRecipe BRAKE = create(CCBlocks.BRAKE).unlockedBy(Blocks.OBSIDIAN::asItem)
            .requiresResultFeature()
            .viaShapeless(b -> b
                    .requires(AllBlocks.ANDESITE_CASING.get())
                    .requires(AllBlocks.SHAFT.get())
                    .requires(Blocks.REDSTONE_WIRE)
                    .requires(Blocks.OBSIDIAN)
            );

    GeneratedRecipe SEQUENCED_PULSE_GENERATOR = create(CCBlocks.SEQUENCED_PULSE_GENERATOR).unlockedBy(CCItems.CONTROL_CHIP::get)
            .requiresResultFeature()
            .viaShaped(b -> b
                    .define('E', AllItems.ELECTRON_TUBE)
                    .define('B', AllItems.BRASS_SHEET)
                    .define('C', CCItems.CONTROL_CHIP)
                    .define('T', Blocks.REDSTONE_TORCH)
                    .define('S', Tags.Items.STONE)
                    .pattern("EC ")
                    .pattern("EBT")
                    .pattern("SSS")
            );

    GeneratedRecipe ITEM_SILO = create(CCBlocks.ITEM_SILO).unlockedByTag(() -> Tags.Items.BARRELS_WOODEN)
            .requiresResultFeature()
            .viaShaped(b -> b
                    .define('B', AllItems.IRON_SHEET.get())
                    .define('C', Tags.Items.BARRELS_WOODEN)
                    .pattern("BCB")
            );

    GeneratedRecipe ITEM_SILO_CYCLE =
            conversionCycle(ImmutableList.of(CCBlocks.ITEM_SILO, AllBlocks.ITEM_VAULT));

    GeneratedRecipe EMPTY_FAN_CATALYST = create(CCBlocks.EMPTY_FAN_CATALYST).unlockedBy(AllBlocks.BRASS_BLOCK::get)
            .requiresResultFeature()
            .viaShaped(b -> b
                    .define('b', AllItems.BRASS_INGOT.get())
                    .define('i', Blocks.IRON_BARS)
                    .pattern("bib")
                    .pattern("i i")
                    .pattern("bib")
            );

    GeneratedRecipe EMPTY_CATALYST_FROM_BLASTING = clearFanCatalyst("blasting", CCBlocks.FAN_BLASTING_CATALYST);
    GeneratedRecipe EMPTY_CATALYST_FROM_SMOKING = clearFanCatalyst("smoking", CCBlocks.FAN_SMOKING_CATALYST);
    GeneratedRecipe EMPTY_CATALYST_FROM_SPLASHING = clearFanCatalyst("splashing", CCBlocks.FAN_SPLASHING_CATALYST);
    GeneratedRecipe EMPTY_CATALYST_FROM_HAUNTING = clearFanCatalyst("haunting", CCBlocks.FAN_HAUNTING_CATALYST);

    private final Marker PALETTES = enterFolder("palettes");

    GeneratedRecipe COPYCAT_SLAB = copycat(CCBlocks.COPYCAT_SLAB, 2);

    GeneratedRecipe COPYCAT_SLAB_FROM_PANELS = create(CCBlocks.COPYCAT_SLAB).withSuffix("_from_panels").unlockedBy(AllBlocks.COPYCAT_PANEL::get)
            .requiresResultFeature()
            .viaShaped(b -> b
                    .define('p', AllBlocks.COPYCAT_PANEL.get())
                    .pattern("p")
                    .pattern("p")
            );

    GeneratedRecipe COPYCAT_SLAB_FROM_STEPS = create(CCBlocks.COPYCAT_SLAB).withSuffix("_from_steps").unlockedBy(AllBlocks.COPYCAT_STEP::get)
            .requiresResultFeature()
            .viaShaped(b -> b
                    .define('s', AllBlocks.COPYCAT_STEP.get())
                    .pattern("ss")
            );

    GeneratedRecipe COPYCAT_SLAB_FROM_BEAMS = create(CCBlocks.COPYCAT_SLAB).withSuffix("_from_beams").unlockedBy(CCBlocks.COPYCAT_BEAM::get)
            .requiresResultFeature()
            .requiresFeature(CCBlocks.COPYCAT_BEAM)
            .viaShaped(b -> b
                    .define('s', CCBlocks.COPYCAT_BEAM.get())
                    .pattern("ss")
            );

    GeneratedRecipe COPYCAT_BLOCK = copycat(CCBlocks.COPYCAT_BLOCK, 1);

    GeneratedRecipe COPYCAT_BLOCK_FROM_SLABS = create(CCBlocks.COPYCAT_BLOCK).withSuffix("_from_slabs").unlockedBy(CCBlocks.COPYCAT_SLAB::get)
            .requiresResultFeature()
            .requiresFeature(CCBlocks.COPYCAT_SLAB)
            .viaShaped(b -> b
                    .define('s', CCBlocks.COPYCAT_SLAB.get())
                    .pattern("s")
                    .pattern("s")
            );

    GeneratedRecipe COPYCAT_BEAM = copycat(CCBlocks.COPYCAT_BEAM, 4);

    GeneratedRecipe COPYCAT_STEP_CYCLE =
            conversionCycle(ImmutableList.of(AllBlocks.COPYCAT_STEP, CCBlocks.COPYCAT_VERTICAL_STEP));

    GeneratedRecipe COPYCAT_VERTICAL_STEP = copycat(CCBlocks.COPYCAT_VERTICAL_STEP, 4);

    String currentFolder = "";

    Marker enterFolder(String folder) {
        currentFolder = folder;
        return new Marker();
    }

    GeneratedRecipeBuilder create(Supplier<ItemLike> result) {
        return new GeneratedRecipeBuilder(currentFolder, result);
    }

    GeneratedRecipeBuilder create(ResourceLocation result) {
        return new GeneratedRecipeBuilder(currentFolder, result);
    }

    GeneratedRecipeBuilder create(ItemProviderEntry<? extends ItemLike> result) {
        return create(result::get);
    }

    GeneratedRecipe createSpecial(Supplier<? extends SimpleRecipeSerializer<?>> serializer, String recipeType,
                                  String path) {
        ResourceLocation location = Create.asResource(recipeType + "/" + currentFolder + "/" + path);
        return register(consumer -> {
            SpecialRecipeBuilder b = SpecialRecipeBuilder.special(serializer.get());
            b.save(consumer, location.toString());
        });
    }

    GeneratedRecipe conversionCycle(List<ItemProviderEntry<? extends ItemLike>> cycle) {
        GeneratedRecipe result = null;
        for (int i = 0; i < cycle.size(); i++) {
            ItemProviderEntry<? extends ItemLike> currentEntry = cycle.get(i);
            ItemProviderEntry<? extends ItemLike> nextEntry = cycle.get((i + 1) % cycle.size());
            result = create(nextEntry).withSuffix("_from_conversion")
                    .unlockedBy(currentEntry::get)
                    .requiresFeature(currentEntry.getId())
                    .requiresFeature(nextEntry.getId())
                    .viaShapeless(b -> b.requires(currentEntry.get()));
        }
        return result;
    }

    GeneratedRecipe clearFanCatalyst(String key, ItemProviderEntry<? extends ItemLike> from) {
        return create(CCBlocks.EMPTY_FAN_CATALYST)
                .withSuffix("_from_" + key)
                .unlockedBy(CCBlocks.EMPTY_FAN_CATALYST::get)
                .requiresResultFeature()
                .viaShapeless(b -> b
                        .requires(from.get())
                );
    }

    GeneratedRecipe copycat(ItemProviderEntry<? extends ItemLike> result, int resultCount) {
        return create(result)
                .unlockedBy(AllItems.ZINC_INGOT::get)
                .requiresResultFeature()
                .viaStonecutting(DataIngredient.tag(AllTags.forgeItemTag("ingots/zinc")), resultCount);
    }

    protected static class Marker {
    }


    class GeneratedRecipeBuilder {

        private final String path;
        private String suffix;
        private Supplier<? extends ItemLike> result;
        private ResourceLocation compatDatagenOutput;
        List<ICondition> recipeConditions;

        private Supplier<ItemPredicate> unlockedBy;
        private int amount;

        private GeneratedRecipeBuilder(String path) {
            this.path = path;
            this.recipeConditions = new ArrayList<>();
            this.suffix = "";
            this.amount = 1;
        }

        public GeneratedRecipeBuilder(String path, Supplier<? extends ItemLike> result) {
            this(path);
            this.result = result;
        }

        public GeneratedRecipeBuilder(String path, ResourceLocation result) {
            this(path);
            this.compatDatagenOutput = result;
        }

        GeneratedRecipeBuilder returns(int amount) {
            this.amount = amount;
            return this;
        }

        GeneratedRecipeBuilder unlockedBy(Supplier<? extends ItemLike> item) {
            this.unlockedBy = () -> ItemPredicate.Builder.item()
                    .of(item.get())
                    .build();
            return this;
        }

        GeneratedRecipeBuilder unlockedByTag(Supplier<TagKey<Item>> tag) {
            this.unlockedBy = () -> ItemPredicate.Builder.item()
                    .of(tag.get())
                    .build();
            return this;
        }

        GeneratedRecipeBuilder whenModLoaded(String modid) {
            return withCondition(new ModLoadedCondition(modid));
        }

        GeneratedRecipeBuilder whenModMissing(String modid) {
            return withCondition(new NotCondition(new ModLoadedCondition(modid)));
        }

        GeneratedRecipeBuilder withCondition(ICondition condition) {
            recipeConditions.add(condition);
            return this;
        }

        GeneratedRecipeBuilder requiresFeature(ResourceLocation location, boolean invert) {
            recipeConditions.add(new FeatureEnabledCondition(location, invert));
            return this;
        }

        GeneratedRecipeBuilder requiresFeature(ResourceLocation location) {
            return requiresFeature(location, false);
        }

        GeneratedRecipeBuilder requiresFeature(BlockEntry<?> block, boolean invert) {
            return requiresFeature(block.getId(), invert);
        }

        GeneratedRecipeBuilder requiresFeature(BlockEntry<?> block) {
            return requiresFeature(block, false);
        }

        GeneratedRecipeBuilder requiresResultFeature() {
            return requiresFeature(RegisteredObjects.getKeyOrThrow(result.get().asItem()));
        }

        GeneratedRecipeBuilder withSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        // FIXME 5.1 refactor - recipe categories as markers instead of sections?
        GeneratedRecipe viaShaped(UnaryOperator<ShapedRecipeBuilder> builder) {
            return handleConditions(consumer -> {
                ShapedRecipeBuilder b = builder.apply(ShapedRecipeBuilder.shaped(result.get(), amount));
                if (unlockedBy != null)
                    b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                b.save(consumer, createLocation("crafting"));
            });
        }

        GeneratedRecipe viaShapeless(UnaryOperator<ShapelessRecipeBuilder> builder) {
            return handleConditions(consumer -> {
                ShapelessRecipeBuilder b = builder.apply(ShapelessRecipeBuilder.shapeless(result.get(), amount));
                if (unlockedBy != null)
                    b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                b.save(consumer, createLocation("crafting"));
            });
        }

        GeneratedRecipe viaStonecutting(Ingredient ingredient, int resultCount) {
            return handleConditions(consumer -> {
                SingleItemRecipeBuilder b = SingleItemRecipeBuilder.stonecutting(ingredient, result.get(), resultCount);
                if (unlockedBy != null)
                    b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                b.save(consumer, createLocation("crafting"));
            });
        }

        GeneratedRecipe viaStonecutting(Ingredient ingredient) {
            return viaStonecutting(ingredient, 1);
        }


        GeneratedRecipe viaSmithing(Supplier<? extends Item> base, Supplier<Ingredient> upgradeMaterial) {
            return handleConditions(consumer -> {
                UpgradeRecipeBuilder b =
                        UpgradeRecipeBuilder.smithing(Ingredient.of(base.get()), upgradeMaterial.get(), result.get()
                                .asItem());
                b.unlocks("has_item", inventoryTrigger(ItemPredicate.Builder.item()
                        .of(base.get())
                        .build()));
                b.save(consumer, createLocation("crafting"));
            });
        }

        private GeneratedRecipe handleConditions(Consumer<Consumer<FinishedRecipe>> recipe) {
            return register(consumer -> {
                if (!recipeConditions.isEmpty()) {
                    ConditionalRecipe.Builder b = ConditionalRecipe.builder();
                    recipeConditions.forEach(b::addCondition);
                    b.addRecipe(recipe);
                    b.generateAdvancement();
                    b.build(consumer, createLocation("crafting"));
                } else {
                    recipe.accept(consumer);
                }
            });
        }

        private ResourceLocation createSimpleLocation(String recipeType) {
            return CreateConnected.asResource(recipeType + "/" + getRegistryName().getPath() + suffix);
        }

        private ResourceLocation createLocation(String recipeType) {
            return CreateConnected.asResource(recipeType + "/" + path + "/" + getRegistryName().getPath() + suffix);
        }

        private ResourceLocation getRegistryName() {
            return compatDatagenOutput == null ? RegisteredObjects.getKeyOrThrow(result.get()
                    .asItem()) : compatDatagenOutput;
        }

        GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder viaCooking(Supplier<? extends ItemLike> item) {
            return unlockedBy(item).viaCookingIngredient(() -> Ingredient.of(item.get()));
        }

        GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder viaCookingTag(Supplier<TagKey<Item>> tag) {
            return unlockedByTag(tag).viaCookingIngredient(() -> Ingredient.of(tag.get()));
        }

        GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder viaCookingIngredient(Supplier<Ingredient> ingredient) {
            return new GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder(ingredient);
        }

        class GeneratedCookingRecipeBuilder {

            private final Supplier<Ingredient> ingredient;
            private float exp;
            private int cookingTime;

            private final SimpleCookingSerializer<?> FURNACE = RecipeSerializer.SMELTING_RECIPE,
                    SMOKER = RecipeSerializer.SMOKING_RECIPE, BLAST = RecipeSerializer.BLASTING_RECIPE,
                    CAMPFIRE = RecipeSerializer.CAMPFIRE_COOKING_RECIPE;

            GeneratedCookingRecipeBuilder(Supplier<Ingredient> ingredient) {
                this.ingredient = ingredient;
                cookingTime = 200;
                exp = 0;
            }

            GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder forDuration(int duration) {
                cookingTime = duration;
                return this;
            }

            GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder rewardXP(float xp) {
                exp = xp;
                return this;
            }

            GeneratedRecipe inFurnace() {
                return inFurnace(b -> b);
            }

            GeneratedRecipe inFurnace(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                return create(FURNACE, builder, 1);
            }

            GeneratedRecipe inSmoker() {
                return inSmoker(b -> b);
            }

            GeneratedRecipe inSmoker(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                create(FURNACE, builder, 1);
                create(CAMPFIRE, builder, 3);
                return create(SMOKER, builder, .5f);
            }

            GeneratedRecipe inBlastFurnace() {
                return inBlastFurnace(b -> b);
            }

            GeneratedRecipe inBlastFurnace(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                create(FURNACE, builder, 1);
                return create(BLAST, builder, .5f);
            }

            private GeneratedRecipe create(SimpleCookingSerializer<?> serializer,
                                           UnaryOperator<SimpleCookingRecipeBuilder> builder, float cookingTimeModifier) {
                return register(consumer -> {
                    boolean isOtherMod = compatDatagenOutput != null;

                    SimpleCookingRecipeBuilder b = builder.apply(
                            SimpleCookingRecipeBuilder.cooking(ingredient.get(), isOtherMod ? Items.DIRT : result.get(),
                                    exp, (int) (cookingTime * cookingTimeModifier), serializer));
                    if (unlockedBy != null)
                        b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                    b.save(result -> {
                        consumer.accept(
                                isOtherMod ? new CCStandardRecipes.ModdedCookingRecipeResult(result, compatDatagenOutput, recipeConditions)
                                        : result);
                    }, createSimpleLocation(RegisteredObjects.getKeyOrThrow(serializer)
                            .getPath()));
                });
            }
        }
    }

//    @Override
//    public String getName() {
//        return "Create: Connected's Standard Recipes";
//    }

    public CCStandardRecipes(DataGenerator generator) {
        super(generator);
    }

    private static class ModdedCookingRecipeResult implements FinishedRecipe {

        private final FinishedRecipe wrapped;
        private final ResourceLocation outputOverride;
        private final List<ICondition> conditions;

        public ModdedCookingRecipeResult(FinishedRecipe wrapped, ResourceLocation outputOverride,
                                         List<ICondition> conditions) {
            this.wrapped = wrapped;
            this.outputOverride = outputOverride;
            this.conditions = conditions;
        }

        @Override
        public @NotNull ResourceLocation getId() {
            return wrapped.getId();
        }

        @Override
        public @NotNull RecipeSerializer<?> getType() {
            return wrapped.getType();
        }

        @Override
        public JsonObject serializeAdvancement() {
            return wrapped.serializeAdvancement();
        }

        @Override
        public ResourceLocation getAdvancementId() {
            return wrapped.getAdvancementId();
        }

        @Override
        public void serializeRecipeData(@NotNull JsonObject object) {
            wrapped.serializeRecipeData(object);
            object.addProperty("result", outputOverride.toString());

            JsonArray conds = new JsonArray();
            conditions.forEach(c -> conds.add(CraftingHelper.serialize(c)));
            object.add("conditions", conds);
        }

    }
}
