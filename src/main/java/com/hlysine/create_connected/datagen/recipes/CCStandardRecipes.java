package com.hlysine.create_connected.datagen.recipes;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.CCItems;
import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.compat.CopycatsManager;
import com.hlysine.create_connected.compat.Mods;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import com.simibubi.create.foundation.mixin.accessor.MappedRegistryAccessor;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@SuppressWarnings("unused")
public class CCStandardRecipes extends CreateRecipeProvider {
    private final Marker KINETICS = enterFolder("kinetics");

    GeneratedRecipe ENCASED_CHAIN_COGWHEEL = create(CCBlocks.ENCASED_CHAIN_COGWHEEL).unlockedBy(AllBlocks.ENCASED_CHAIN_DRIVE::get)
            .requiresResultFeature()
            .viaShapeless(b -> b
                    .requires(AllBlocks.ENCASED_CHAIN_DRIVE)
                    .requires(AllBlocks.COGWHEEL)
            );

    GeneratedRecipe CRANK_WHEEL = create(CCBlocks.CRANK_WHEEL).unlockedBy(AllBlocks.COGWHEEL::get)
            .requiresResultFeature()
            .viaShapeless(b -> b
                    .requires(AllBlocks.HAND_CRANK)
                    .requires(AllBlocks.COGWHEEL)
            );

    GeneratedRecipe LARGE_CRANK_WHEEL = create(CCBlocks.LARGE_CRANK_WHEEL).unlockedBy(AllBlocks.LARGE_COGWHEEL::get)
            .requiresResultFeature()
            .viaShapeless(b -> b
                    .requires(AllBlocks.HAND_CRANK)
                    .requires(AllBlocks.LARGE_COGWHEEL)
            );

    GeneratedRecipe INVERTED_CLUTCH_CYCLE =
            conversionCycle(ImmutableList.of(AllBlocks.CLUTCH, CCBlocks.INVERTED_CLUTCH));

    GeneratedRecipe INVERTED_GEARSHIFT_CYCLE =
            conversionCycle(ImmutableList.of(AllBlocks.GEARSHIFT, CCBlocks.INVERTED_GEARSHIFT));

    GeneratedRecipe PARALLEL_GEARBOX = create(CCBlocks.PARALLEL_GEARBOX).unlockedBy(AllBlocks.LARGE_COGWHEEL::get)
            .requiresResultFeature()
            .viaShapeless(b -> b
                    .requires(AllBlocks.GEARBOX)
                    .requires(AllBlocks.LARGE_COGWHEEL)
            );

    GeneratedRecipe PARALLEL_GEARBOX_CYCLE =
            conversionCycle(ImmutableList.of(CCBlocks.PARALLEL_GEARBOX, CCItems.VERTICAL_PARALLEL_GEARBOX));

    GeneratedRecipe SIX_WAY_GEARBOX = create(CCBlocks.SIX_WAY_GEARBOX).unlockedBy(AllBlocks.LARGE_COGWHEEL::get)
            .requiresResultFeature()
            .viaShaped(b -> b
                    .define('c', AllBlocks.COGWHEEL)
                    .define('l', AllBlocks.LARGE_COGWHEEL)
                    .define('s', AllBlocks.ANDESITE_CASING)
                    .pattern("lc ")
                    .pattern("csc")
                    .pattern(" cl")
            );

    GeneratedRecipe SIX_WAY_GEARBOX_FROM_GEARBOX = create(CCBlocks.SIX_WAY_GEARBOX).withSuffix("_from_gearbox").unlockedBy(AllBlocks.GEARBOX::get)
            .requiresResultFeature()
            .viaShapeless(b -> b
                    .requires(AllBlocks.GEARBOX)
                    .requires(AllBlocks.LARGE_COGWHEEL)
                    .requires(AllBlocks.LARGE_COGWHEEL)
            );

    GeneratedRecipe SIX_WAY_GEARBOX_FROM_PARALLEL = create(CCBlocks.SIX_WAY_GEARBOX).withSuffix("_from_parallel").unlockedBy(CCBlocks.PARALLEL_GEARBOX::get)
            .requiresResultFeature()
            .requiresFeature(CCBlocks.PARALLEL_GEARBOX)
            .viaShapeless(b -> b
                    .requires(CCBlocks.PARALLEL_GEARBOX)
                    .requires(AllBlocks.LARGE_COGWHEEL)
            );

    GeneratedRecipe SIX_WAY_GEARBOX_CYCLE =
            conversionCycle(ImmutableList.of(CCBlocks.SIX_WAY_GEARBOX, CCItems.VERTICAL_SIX_WAY_GEARBOX));

    GeneratedRecipe BRASS_GEARBOX = create(CCBlocks.BRASS_GEARBOX).unlockedBy(AllBlocks.ROTATION_SPEED_CONTROLLER::get)
            .requiresResultFeature()
            .viaShaped(b -> b
                    .define('c', AllBlocks.COGWHEEL)
                    .define('s', AllBlocks.ROTATION_SPEED_CONTROLLER)
                    .pattern(" c ")
                    .pattern("csc")
                    .pattern(" c ")
            );

    GeneratedRecipe BRASS_GEARBOX_CYCLE =
            conversionCycle(ImmutableList.of(CCBlocks.BRASS_GEARBOX, CCItems.VERTICAL_BRASS_GEARBOX));

    GeneratedRecipe CROSS_CONNECTOR = create(CCBlocks.CROSS_CONNECTOR).unlockedBy(AllBlocks.GEARBOX::get)
            .requiresResultFeature()
            .viaShaped(b -> b
                    .define('g', AllBlocks.GEARBOX)
                    .define('s', AllBlocks.SHAFT)
                    .pattern(" s ")
                    .pattern("sgs")
                    .pattern(" s ")
            );

    GeneratedRecipe OVERSTRESS_CLUTCH = create(CCBlocks.OVERSTRESS_CLUTCH).unlockedBy(AllItems.ELECTRON_TUBE::get)
            .requiresResultFeature()
            .viaShapeless(b -> b
                    .requires(AllBlocks.ANDESITE_CASING)
                    .requires(AllBlocks.SHAFT)
                    .requires(AllItems.IRON_SHEET)
                    .requires(AllItems.ELECTRON_TUBE)
            );

    GeneratedRecipe CENTRIFUGAL_CLUTCH = create(CCBlocks.CENTRIFUGAL_CLUTCH).unlockedBy(AllBlocks.SPEEDOMETER::get)
            .requiresResultFeature()
            .viaShapeless(b -> b
                    .requires(AllBlocks.ANDESITE_CASING)
                    .requires(AllBlocks.SHAFT)
                    .requires(AllItems.IRON_SHEET)
                    .requires(AllBlocks.SPEEDOMETER)
            );

    GeneratedRecipe FREEWHEEL_CLUTCH = create(CCBlocks.FREEWHEEL_CLUTCH).unlockedBy(AllBlocks.COGWHEEL::get)
            .requiresResultFeature()
            .viaShapeless(b -> b
                    .requires(AllBlocks.ANDESITE_CASING)
                    .requires(AllBlocks.SHAFT)
                    .requires(AllItems.IRON_SHEET)
                    .requires(AllBlocks.COGWHEEL)
            );

    GeneratedRecipe BRAKE = create(CCBlocks.BRAKE).unlockedBy(Blocks.OBSIDIAN::asItem)
            .requiresResultFeature()
            .viaShapeless(b -> b
                    .requires(AllBlocks.ANDESITE_CASING)
                    .requires(AllBlocks.SHAFT)
                    .requires(Blocks.REDSTONE_WIRE)
                    .requires(Blocks.OBSIDIAN)
            );

    GeneratedRecipe KINETIC_BRIDGE = create(CCBlocks.KINETIC_BRIDGE).unlockedBy(AllBlocks.CLUTCH::get)
            .requiresResultFeature()
            .viaShaped(b -> b
                    .define('c', AllBlocks.CLUTCH)
                    .define('s', AllBlocks.SHAFT)
                    .define('b', AllBlocks.BRASS_CASING)
                    .pattern(" b ")
                    .pattern("scs")
                    .pattern(" b ")
            );

    GeneratedRecipe KINETIC_BATTERY = create(CCBlocks.KINETIC_BATTERY).unlockedBy(AllItems.ELECTRON_TUBE::get)
            .requiresResultFeature()
            .returns(8)
            .viaShaped(b -> b
                    .define('p', AllItems.PRECISION_MECHANISM)
                    .define('b', AllBlocks.BRASS_CASING)
                    .define('r', Blocks.REDSTONE_WIRE)
                    .define('i', AllItems.IRON_SHEET)
                    .pattern(" p ")
                    .pattern(" b ")
                    .pattern("iri")
            );

    GeneratedRecipe SEQUENCED_PULSE_GENERATOR = create(CCBlocks.SEQUENCED_PULSE_GENERATOR).unlockedBy(CCItems.CONTROL_CHIP::get)
            .requiresResultFeature()
            .viaShaped(b -> b
                    .define('E', AllItems.ELECTRON_TUBE)
                    .define('B', AllItems.BRASS_SHEET)
                    .define('C', CCItems.CONTROL_CHIP)
                    .define('T', Blocks.REDSTONE_TORCH)
                    .define('S', Tags.Items.STONES)
                    .pattern("EC ")
                    .pattern("EBT")
                    .pattern("SSS")
            );

    GeneratedRecipe LINKED_TRANSMITTER_CYCLE = conversionCycle(ImmutableList.of(CCItems.LINKED_TRANSMITTER, AllBlocks.REDSTONE_LINK));

    GeneratedRecipe REDSTONE_LINK_WILDCARD = create(CCItems.REDSTONE_LINK_WILDCARD).unlockedBy(AllItems.TRANSMITTER::get)
            .requiresResultFeature()
            .viaShapeless(b -> b
                    .requires(AllItems.TRANSMITTER)
                    .requires(AllItems.CRAFTER_SLOT_COVER)
            );

    GeneratedRecipe ITEM_SILO = create(CCBlocks.ITEM_SILO).unlockedByTag(() -> Tags.Items.BARRELS_WOODEN)
            .requiresResultFeature()
            .viaShaped(b -> b
                    .define('B', AllItems.IRON_SHEET)
                    .define('C', Tags.Items.BARRELS_WOODEN)
                    .pattern("BCB")
            );

    GeneratedRecipe ITEM_SILO_CYCLE =
            conversionCycle(ImmutableList.of(CCBlocks.ITEM_SILO, AllBlocks.ITEM_VAULT));


    GeneratedRecipe FLUID_VESSEL = create(CCBlocks.FLUID_VESSEL).unlockedByTag(() -> Tags.Items.BARRELS_WOODEN)
            .requiresResultFeature()
            .viaShaped(b -> b.define('B', AllItems.COPPER_SHEET)
                    .define('C', Tags.Items.BARRELS_WOODEN)
                    .pattern("BCB"));

    GeneratedRecipe FLUID_VESSEL_CYCLE =
            conversionCycle(ImmutableList.of(CCBlocks.FLUID_VESSEL, AllBlocks.FLUID_TANK));

    GeneratedRecipe INVENTORY_ACCESS_PORT = create(CCBlocks.INVENTORY_ACCESS_PORT).unlockedBy(AllItems.ELECTRON_TUBE::get)
            .requiresResultFeature()
            .returns(2)
            .viaShaped(b -> b
                    .define('B', AllBlocks.BRASS_CASING)
                    .define('C', AllBlocks.CHUTE)
                    .define('E', AllItems.ELECTRON_TUBE)
                    .pattern("B")
                    .pattern("C")
                    .pattern("E")
            );

    GeneratedRecipe INVENTORY_BRIDGE = create(CCBlocks.INVENTORY_BRIDGE).unlockedBy(AllItems.ELECTRON_TUBE::get)
            .requiresResultFeature()
            .viaShapeless(b -> b
                    .requires(CCBlocks.INVENTORY_ACCESS_PORT)
                    .requires(CCBlocks.INVENTORY_ACCESS_PORT)
            );

    GeneratedRecipe EMPTY_FAN_CATALYST = create(CCBlocks.EMPTY_FAN_CATALYST).unlockedBy(AllBlocks.BRASS_BLOCK::get)
            .requiresResultFeature()
            .viaShaped(b -> b
                    .define('b', AllItems.BRASS_INGOT)
                    .define('i', Blocks.IRON_BARS)
                    .pattern("bib")
                    .pattern("i i")
                    .pattern("bib")
            );

    GeneratedRecipe EMPTY_CATALYST_FROM_BLASTING = clearFanCatalyst("blasting", CCBlocks.FAN_BLASTING_CATALYST);
    GeneratedRecipe EMPTY_CATALYST_FROM_SMOKING = clearFanCatalyst("smoking", CCBlocks.FAN_SMOKING_CATALYST);
    GeneratedRecipe EMPTY_CATALYST_FROM_SPLASHING = clearFanCatalyst("splashing", CCBlocks.FAN_SPLASHING_CATALYST);
    GeneratedRecipe EMPTY_CATALYST_FROM_HAUNTING = clearFanCatalyst("haunting", CCBlocks.FAN_HAUNTING_CATALYST);
    GeneratedRecipe EMPTY_CATALYST_FROM_FREEZING = clearFanCatalyst("freezing", CCBlocks.FAN_FREEZING_CATALYST);
    GeneratedRecipe EMPTY_CATALYST_FROM_SEETHING = clearFanCatalyst("seething", CCBlocks.FAN_SEETHING_CATALYST);
    GeneratedRecipe EMPTY_CATALYST_FROM_SANDING = clearFanCatalyst("sanding", CCBlocks.FAN_SANDING_CATALYST);
    GeneratedRecipe EMPTY_CATALYST_FROM_ENRICHED = clearFanCatalyst("enriched", CCBlocks.FAN_ENRICHED_CATALYST);
    GeneratedRecipe EMPTY_CATALYST_FROM_ENDING_DRAGONS_BREATH = clearFanCatalyst("ending_dragons_breath", CCBlocks.FAN_ENDING_CATALYST_DRAGONS_BREATH);
    GeneratedRecipe EMPTY_CATALYST_FROM_ENDING_DRAGON_HEAD = clearFanCatalyst("ending_dragon_head", CCBlocks.FAN_ENDING_CATALYST_DRAGON_HEAD);
    GeneratedRecipe EMPTY_CATALYST_FROM_WITHERING = clearFanCatalyst("withering", CCBlocks.FAN_WITHERING_CATALYST);

    private final Marker PALETTES = enterFolder("palettes");

    GeneratedRecipe COPYCAT_SLAB = copycat(CCBlocks.COPYCAT_SLAB, 2);

    GeneratedRecipe COPYCAT_SLAB_FROM_PANELS = create(CCBlocks.COPYCAT_SLAB).withSuffix("_from_panels").unlockedBy(AllBlocks.COPYCAT_PANEL::get)
            .requiresResultFeature()
            .disabledInCopycats()
            .viaShaped(b -> b
                    .define('p', AllBlocks.COPYCAT_PANEL)
                    .pattern("p")
                    .pattern("p")
            );

    GeneratedRecipe COPYCAT_SLAB_FROM_STEPS = create(CCBlocks.COPYCAT_SLAB).withSuffix("_from_steps").unlockedBy(AllBlocks.COPYCAT_STEP::get)
            .requiresResultFeature()
            .disabledInCopycats()
            .viaShaped(b -> b
                    .define('s', AllBlocks.COPYCAT_STEP)
                    .pattern("ss")
            );

    GeneratedRecipe COPYCAT_SLAB_FROM_BEAMS = create(CCBlocks.COPYCAT_SLAB).withSuffix("_from_beams").unlockedBy(CCBlocks.COPYCAT_BEAM::get)
            .requiresResultFeature()
            .disabledInCopycats()
            .requiresFeature(CCBlocks.COPYCAT_BEAM)
            .viaShaped(b -> b
                    .define('s', CCBlocks.COPYCAT_BEAM)
                    .pattern("ss")
            );

    GeneratedRecipe COPYCAT_BLOCK = copycat(CCBlocks.COPYCAT_BLOCK, 1);

    GeneratedRecipe COPYCAT_BLOCK_FROM_SLABS = create(CCBlocks.COPYCAT_BLOCK).withSuffix("_from_slabs").unlockedBy(CCBlocks.COPYCAT_SLAB::get)
            .requiresResultFeature()
            .disabledInCopycats()
            .requiresFeature(CCBlocks.COPYCAT_SLAB)
            .viaShaped(b -> b
                    .define('s', CCBlocks.COPYCAT_SLAB)
                    .pattern("s")
                    .pattern("s")
            );

    GeneratedRecipe COPYCAT_BEAM = copycat(CCBlocks.COPYCAT_BEAM, 4);

    GeneratedRecipe COPYCAT_STEP_CYCLE =
            copycatCycle(ImmutableList.of(AllBlocks.COPYCAT_STEP, CCBlocks.COPYCAT_VERTICAL_STEP));

    GeneratedRecipe COPYCAT_VERTICAL_STEP = copycat(CCBlocks.COPYCAT_VERTICAL_STEP, 4);

    GeneratedRecipe COPYCAT_STAIRS = copycat(CCBlocks.COPYCAT_STAIRS, 1);

    GeneratedRecipe COPYCAT_FENCE = copycat(CCBlocks.COPYCAT_FENCE, 1);

    GeneratedRecipe COPYCAT_FENCE_GATE = copycat(CCBlocks.COPYCAT_FENCE_GATE, 1);

    GeneratedRecipe COPYCAT_WALL = copycat(CCBlocks.COPYCAT_WALL, 1);

    GeneratedRecipe COPYCAT_BOARD = copycat(CCBlocks.COPYCAT_BOARD, 8);

    GeneratedRecipe COPYCAT_BOX = create(CCItems.COPYCAT_BOX).unlockedBy(CCBlocks.COPYCAT_BOARD::get)
            .requiresResultFeature()
            .disabledInCopycats()
            .viaShaped(b -> b
                    .define('s', CCBlocks.COPYCAT_BOARD)
                    .pattern("ss ")
                    .pattern("s s")
                    .pattern(" ss")
            );

    GeneratedRecipe COPYCAT_CATWALK = create(CCItems.COPYCAT_CATWALK).unlockedBy(CCBlocks.COPYCAT_BOARD::get)
            .requiresResultFeature()
            .disabledInCopycats()
            .viaShaped(b -> b
                    .define('s', CCBlocks.COPYCAT_BOARD)
                    .pattern("s s")
                    .pattern(" s ")
            );

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

    GeneratedRecipeBuilder create(ItemProviderEntry<? extends ItemLike, ? extends ItemLike> result) {
        return create(result::get);
    }

    GeneratedRecipe createSpecial(Function<CraftingBookCategory, Recipe<?>> builder, String recipeType,
                                  String path) {
        ResourceLocation location = CreateConnected.asResource(recipeType + "/" + currentFolder + "/" + path);
        return register(consumer -> {
            SpecialRecipeBuilder b = SpecialRecipeBuilder.special(builder);
            b.save(consumer, location.toString());
        });
    }

    GeneratedRecipe copycatCycle(List<ItemProviderEntry<? extends ItemLike, ?>> cycle) {
        GeneratedRecipe result = null;
        for (int i = 0; i < cycle.size(); i++) {
            ItemProviderEntry<? extends ItemLike, ? extends ItemLike> currentEntry = cycle.get(i);
            ItemProviderEntry<? extends ItemLike, ? extends ItemLike> nextEntry = cycle.get((i + 1) % cycle.size());
            result = create(nextEntry).withSuffix("_from_conversion")
                    .unlockedBy(currentEntry::get)
                    .requiresFeature(currentEntry.getId())
                    .disabledInCopycats()
                    .requiresFeature(nextEntry.getId())
                    .viaShapeless(b -> b.requires(currentEntry.get()));
        }
        return result;
    }

    GeneratedRecipe conversionCycle(List<ItemProviderEntry<? extends ItemLike, ? extends ItemLike>> cycle) {
        GeneratedRecipe result = null;
        for (int i = 0; i < cycle.size(); i++) {
            ItemProviderEntry<? extends ItemLike, ? extends ItemLike> currentEntry = cycle.get(i);
            ItemProviderEntry<? extends ItemLike, ? extends ItemLike> nextEntry = cycle.get((i + 1) % cycle.size());
            result = create(nextEntry).withSuffix("_from_conversion")
                    .unlockedBy(currentEntry::get)
                    .requiresFeature(currentEntry.getId())
                    .requiresFeature(nextEntry.getId())
                    .viaShapeless(b -> b.requires(currentEntry.get()));
        }
        return result;
    }

    GeneratedRecipe clearFanCatalyst(String key, ItemProviderEntry<? extends ItemLike, ? extends ItemLike> from) {
        return create(CCBlocks.EMPTY_FAN_CATALYST)
                .withSuffix("_from_" + key)
                .unlockedBy(CCBlocks.EMPTY_FAN_CATALYST::get)
                .requiresResultFeature()
                .viaShapeless(b -> b
                        .requires(from)
                );
    }

    GeneratedRecipe copycat(ItemProviderEntry<? extends ItemLike, ? extends ItemLike> result, int resultCount) {
        if (CopycatsManager.convert(result) != result)
            create(() -> CopycatsManager.convert(result)).withSuffix("_compat")
                    .requiresFeature(RegisteredObjectsHelper.getKeyOrThrow(result.asItem()))
                    .unlockedBy(result::get)
                    .enabledInCopycats()
                    .viaShapeless(b -> b
                            .requires(result)
                    );
        return create(result)
                .unlockedBy(AllItems.ZINC_INGOT::get)
                .requiresResultFeature()
                .disabledInCopycats()
                .viaStonecutting(Ingredient.of(AllTags.commonItemTag("ingots/zinc")), resultCount);
    }

    protected static class Marker {
    }


    class GeneratedRecipeBuilder {

        private String path;
        private String suffix;
        private Supplier<? extends ItemLike> result;
        private RecipeCategory category = null;
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

        GeneratedRecipeBuilder inCategory(RecipeCategory category) {
            this.category = category;
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

        GeneratedRecipeBuilder requiresFeature(ResourceLocation location) {
            recipeConditions.add(new FeatureEnabledCondition(location));
            return this;
        }

        GeneratedRecipeBuilder requiresFeature(BlockEntry<?> block) {
            return requiresFeature(block.getId());
        }

        GeneratedRecipeBuilder requiresResultFeature() {
            return requiresFeature(RegisteredObjectsHelper.getKeyOrThrow(result.get().asItem()));
        }

        GeneratedRecipeBuilder disabledInCopycats() {
            return withCondition(new NotCondition(new FeatureEnabledInCopycatsCondition(RegisteredObjectsHelper.getKeyOrThrow(result.get().asItem()))));
        }

        GeneratedRecipeBuilder enabledInCopycats() {
            return withCondition(new FeatureEnabledInCopycatsCondition(RegisteredObjectsHelper.getKeyOrThrow(result.get().asItem())));
        }

        GeneratedRecipeBuilder withSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        // FIXME 5.1 refactor - recipe categories as markers instead of sections?
        GeneratedRecipe viaShaped(UnaryOperator<ShapedRecipeBuilder> builder) {
            return register(consumer -> {
                ShapedRecipeBuilder b = builder.apply(ShapedRecipeBuilder.shaped(category == null ? RecipeCategory.MISC : category, result.get(), amount));
                if (unlockedBy != null)
                    b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                if (!recipeConditions.isEmpty()) {
                    consumer = consumer.withConditions(recipeConditions.toArray(new ICondition[0]));
                }
                b.save(consumer, createLocation("crafting"));
            });
        }

        GeneratedRecipe viaShapeless(UnaryOperator<ShapelessRecipeBuilder> builder) {
            return register(consumer -> {
                ShapelessRecipeBuilder b = builder.apply(ShapelessRecipeBuilder.shapeless(category == null ? RecipeCategory.MISC : category, result.get(), amount));
                if (unlockedBy != null)
                    b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                if (!recipeConditions.isEmpty()) {
                    consumer = consumer.withConditions(recipeConditions.toArray(new ICondition[0]));
                }
                b.save(consumer, createLocation("crafting"));
            });
        }

        GeneratedRecipe viaStonecutting(Ingredient ingredient, int resultCount) {
            return register(consumer -> {
                SingleItemRecipeBuilder b = SingleItemRecipeBuilder.stonecutting(ingredient, category == null ? RecipeCategory.BUILDING_BLOCKS : category, result.get(), resultCount);
                if (unlockedBy != null)
                    b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                if (!recipeConditions.isEmpty()) {
                    consumer = consumer.withConditions(recipeConditions.toArray(new ICondition[0]));
                }
                b.save(consumer, createLocation("crafting"));
            });
        }

        GeneratedRecipe viaStonecutting(Ingredient ingredient) {
            return viaStonecutting(ingredient, 1);
        }

        GeneratedRecipe viaNetheriteSmithing(Supplier<? extends Item> base, Ingredient upgradeMaterial) {
            return register(consumer -> {
                SmithingTransformRecipeBuilder b =
                        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.of(base.get()), upgradeMaterial, category == null ? RecipeCategory.COMBAT : category, result.get()
                                        .asItem());
                b.unlocks("has_item", inventoryTrigger(ItemPredicate.Builder.item()
                        .of(base.get())
                        .build()));
                if (!recipeConditions.isEmpty()) {
                    consumer = consumer.withConditions(recipeConditions.toArray(new ICondition[0]));
                }
                b.save(consumer, createLocation("crafting"));
            });
        }

        private ResourceLocation createSimpleLocation(String recipeType) {
            return CreateConnected.asResource(recipeType + "/" + getRegistryName().getPath() + suffix);
        }

        private ResourceLocation createLocation(String recipeType) {
            return CreateConnected.asResource(recipeType + "/" + path + "/" + getRegistryName().getPath() + suffix);
        }

        private ResourceLocation getRegistryName() {
            return compatDatagenOutput == null ? RegisteredObjectsHelper.getKeyOrThrow(result.get()
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
                return create(RecipeSerializer.SMELTING_RECIPE, builder, SmeltingRecipe::new, 1);
            }

            GeneratedRecipe inSmoker() {
                return inSmoker(b -> b);
            }

            GeneratedRecipe inSmoker(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                create(RecipeSerializer.SMELTING_RECIPE, builder, SmeltingRecipe::new, 1);
                create(RecipeSerializer.CAMPFIRE_COOKING_RECIPE, builder, CampfireCookingRecipe::new, 3);
                return create(RecipeSerializer.SMOKING_RECIPE, builder, SmokingRecipe::new, .5f);
            }

            GeneratedRecipe inBlastFurnace() {
                return inBlastFurnace(b -> b);
            }

            GeneratedRecipe inBlastFurnace(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                create(RecipeSerializer.SMELTING_RECIPE, builder, SmeltingRecipe::new, 1);
                return create(RecipeSerializer.BLASTING_RECIPE, builder, BlastingRecipe::new, .5f);
            }

            private <T extends AbstractCookingRecipe> GeneratedRecipe create(RecipeSerializer<T> serializer,
                                                                             UnaryOperator<SimpleCookingRecipeBuilder> builder, AbstractCookingRecipe.Factory<T> factory, float cookingTimeModifier) {
                return register(recipeOutput -> {
                    boolean isOtherMod = compatDatagenOutput != null;

                    SimpleCookingRecipeBuilder b = builder.apply(SimpleCookingRecipeBuilder.generic(ingredient.get(),
                            RecipeCategory.MISC, isOtherMod ? Items.DIRT : result.get(), exp,
                            (int) (cookingTime * cookingTimeModifier), serializer, factory));
                    if (unlockedBy != null)
                        b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));

                    if (!recipeConditions.isEmpty())
                        recipeOutput = recipeOutput.withConditions(recipeConditions.toArray(new ICondition[0]));

                    b.save(
                            isOtherMod ? new ModdedCookingRecipeOutput(recipeOutput, compatDatagenOutput) : recipeOutput,
                            createSimpleLocation(RegisteredObjectsHelper.getKeyOrThrow(serializer).getPath())
                    );
                });
            }
        }
    }

    @Override
    public @NotNull String getName() {
        return "Create: Connected's Standard Recipes";
    }

    public CCStandardRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    private static class ModdedCookingRecipeOutputShim implements Recipe<RecipeInput> {

        private static final Map<RecipeType<?>, Serializer> serializers = new ConcurrentHashMap<>();

        private final Recipe<?> wrapped;
        private final ResourceLocation overrideID;

        private ModdedCookingRecipeOutputShim(Recipe<?> wrapped, ResourceLocation overrideID) {
            this.wrapped = wrapped;
            this.overrideID = overrideID;
        }

        @Override
        public boolean matches(RecipeInput recipeInput, Level level) {
            throw new AssertionError("Only for datagen output");
        }

        @Override
        public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
            throw new AssertionError("Only for datagen output");
        }

        @Override
        public boolean canCraftInDimensions(int pWidth, int pHeight) {
            throw new AssertionError("Only for datagen output");
        }

        @Override
        public ItemStack getResultItem(HolderLookup.Provider registries) {
            throw new AssertionError("Only for datagen output");
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return serializers.computeIfAbsent(
                    getType(),
                    t -> Serializer.create(wrapped)
            );
        }

        @Override
        public RecipeType<?> getType() {
            return wrapped.getType();
        }

        private record Serializer(
                MapCodec<Recipe<?>> wrappedCodec) implements RecipeSerializer<ModdedCookingRecipeOutputShim> {
            private static Serializer create(Recipe<?> wrapped) {
                RecipeSerializer<?> wrappedSerializer = wrapped.getSerializer();
                @SuppressWarnings("unchecked")
                Serializer serializer = new Serializer((MapCodec<Recipe<?>>) wrappedSerializer.codec());

                // Need to do some registry injection to get the Recipe/Registry#byNameCodec to encode the right type for this
                // getResourceKey and getId
                // byValue and toId
                // Holder.Reference: key
                if (BuiltInRegistries.RECIPE_SERIALIZER instanceof MappedRegistryAccessor<?> mra) {
                    @SuppressWarnings("unchecked")
                    MappedRegistryAccessor<RecipeSerializer<?>> mra$ = (MappedRegistryAccessor<RecipeSerializer<?>>) mra;

                    int wrappedId = mra$.getToId().getOrDefault(wrappedSerializer, -1);
                    ResourceKey<RecipeSerializer<?>> wrappedKey = mra$.getByValue().get(wrappedSerializer).key();

                    mra$.getToId().put(serializer, wrappedId);
                    //noinspection DataFlowIssue - it is ok to pass null as the owner, because this is only being used for serialization
                    mra$.getByValue().put(serializer, Holder.Reference.createStandAlone(null, wrappedKey));
                } else {
                    throw new AssertionError("ModdedCookingRecipeOutputShim will not be able to" +
                            " serialize without injecting into a registry. Expected" +
                            " BuiltInRegistries.RECIPE_SERIALIZER to be of class MappedRegistry, is of class " +
                            BuiltInRegistries.RECIPE_SERIALIZER.getClass()
                    );
                }
                return serializer;
            }

            @Override
            public MapCodec<ModdedCookingRecipeOutputShim> codec() {
                return RecordCodecBuilder.mapCodec(instance -> instance.group(
                        wrappedCodec.forGetter(i -> i.wrapped),
                        FakeItemStack.CODEC.fieldOf("result").forGetter(i -> new FakeItemStack(i.overrideID))
                ).apply(instance, (wrappedRecipe, fakeItemStack) -> {
                    throw new AssertionError("Only for datagen output");
                }));
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, ModdedCookingRecipeOutputShim> streamCodec() {
                throw new AssertionError("Only for datagen output");
            }
        }

        private record FakeItemStack(ResourceLocation id) {
            public static Codec<FakeItemStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("id").forGetter(FakeItemStack::id)
            ).apply(instance, FakeItemStack::new));
        }
    }

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    private record ModdedCookingRecipeOutput(RecipeOutput wrapped,
                                             ResourceLocation outputOverride) implements RecipeOutput {

        @Override
        public Advancement.Builder advancement() {
            return wrapped.advancement();
        }

        @Override
        public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
            wrapped.accept(id, new ModdedCookingRecipeOutputShim(recipe, outputOverride), advancement, conditions);
        }
    }
}
