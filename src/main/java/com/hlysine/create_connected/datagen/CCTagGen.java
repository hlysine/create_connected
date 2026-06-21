package com.hlysine.create_connected.datagen;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.compat.Mods;
import com.hlysine.create_connected.registries.CCBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class CCTagGen {
    public static void addGenerators() {
        CreateConnected.getRegistrate().addDataGenerator(ProviderType.BLOCK_TAGS, CCTagGen::genBlockTags);
        CreateConnected.getRegistrate().addDataGenerator(ProviderType.ITEM_TAGS, CCTagGen::genItemTags);
    }

    private static void genBlockTags(RegistrateTagsProvider<Block> provIn) {
        TagGen.CreateTagsProvider<Block> prov = new TagGen.CreateTagsProvider<>(provIn, Block::builtInRegistryHolder);
        prov.tag(BlockTags.create(Mods.DIAGONAL_FENCES.rl("non_diagonal_fences")))
                .add(CCBlocks.COPYCAT_FENCE.get())
                .add(CCBlocks.WRAPPED_COPYCAT_FENCE.get());
        prov.tag(BlockTags.create(Mods.DREAMS_DESIRES.rl("fan_processing_catalysts/freezing")))
                .add(CCBlocks.FAN_FREEZING_CATALYST.get());
        prov.tag(BlockTags.create(Mods.DREAMS_DESIRES.rl("fan_processing_catalysts/seething")))
                .add(CCBlocks.FAN_SEETHING_CATALYST.get());
        prov.tag(BlockTags.create(Mods.DREAMS_DESIRES.rl("fan_processing_catalysts/sanding")))
                .add(CCBlocks.FAN_SANDING_CATALYST.get());
        prov.tag(BlockTags.create(Mods.DREAMS_DESIRES.rl("fan_processing_catalysts_freezing")))
                .add(CCBlocks.FAN_FREEZING_CATALYST.get());
        prov.tag(BlockTags.create(Mods.DREAMS_DESIRES.rl("fan_processing_catalysts_superheating")))
                .add(CCBlocks.FAN_SEETHING_CATALYST.get());
        prov.tag(BlockTags.create(Mods.DREAMS_DESIRES.rl("fan_processing_catalysts_sanding")))
                .add(CCBlocks.FAN_SANDING_CATALYST.get());
        prov.tag(BlockTags.create(Mods.GARNISHED.rl("fan_processing_catalysts/freezing")))
                .add(CCBlocks.FAN_FREEZING_CATALYST.get());
        prov.tag(BlockTags.create(Mods.NUCLEAR.rl("fan_processing_catalysts/enriched")))
                .add(CCBlocks.FAN_ENRICHED_CATALYST.get());
        prov.tag(BlockTags.create(Mods.DRAGONS_PLUS.rl("passive_block_freezers")))
                .add(CCBlocks.FAN_FREEZING_CATALYST.get());
        prov.tag(BlockTags.create(Mods.DRAGONS_PLUS.rl("fan_processing_catalysts/sanding")))
                .add(CCBlocks.FAN_SANDING_CATALYST.get());
        prov.tag(BlockTags.create(Mods.DRAGONS_PLUS.rl("fan_processing_catalysts/ending")))
                .add(CCBlocks.FAN_ENDING_CATALYST_DRAGONS_BREATH.get())
                .add(CCBlocks.FAN_ENDING_CATALYST_DRAGON_HEAD.get());
        prov.tag(BlockTags.create(Mods.MORE_CATALYSTS.rl("fan_catalysts/chocolate_coating")))
                .add(CCBlocks.FAN_CHOCOLATE_COATING_CATALYST.get());
        prov.tag(BlockTags.create(Mods.MORE_CATALYSTS.rl("fan_catalysts/honey_coating")))
                .add(CCBlocks.FAN_HONEY_COATING_CATALYST.get());
        prov.tag(BlockTags.create(Mods.MORE_CATALYSTS.rl("fan_catalysts/exploding")))
                .add(CCBlocks.FAN_EXPLODING_CATALYST.get());
        prov.tag(BlockTags.create(Mods.MORE_CATALYSTS.rl("fan_catalysts/resonance")))
                .add(CCBlocks.FAN_RESONANCE_CATALYST.get());
        prov.tag(BlockTags.create(Mods.MORE_CATALYSTS.rl("fan_catalysts/sculking")))
                .add(CCBlocks.FAN_SCULKING_CATALYST.get());
        prov.tag(BlockTags.create(Mods.MORE_CATALYSTS.rl("fan_catalysts/purifying")))
                .add(CCBlocks.FAN_PURIFYING_CATALYST.get());
        prov.tag(BlockTags.create(Mods.SHIMMER.rl("fan_transmutation_catalysts")))
                .add(CCBlocks.FAN_TRANSMUTATION_CATALYST.get());
        prov.tag(BlockTags.create(Mods.SHIMMER.rl("fan_glooming_catalysts")))
                .add(CCBlocks.FAN_GLOOMING_CATALYST.get());
        prov.tag(BlockTags.create(Mods.NETHER_INDUSTRY.rl("fan_soul_stripping_catalysts")))
                .add(CCBlocks.FAN_SOUL_STRIPPING_CATALYST.get());
        CCBlocks.FAN_DYEING_CATALYSTS.forEach((color, block) -> {
            prov.tag(BlockTags.create(Mods.GARNISHED.rl("fan_processing_catalysts/dye/" + color.getName())))
                    .addOptional(block.getId());
            prov.tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "dyes/" + color.getName())))
                    .addOptional(block.getId());
            prov.tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "dyes")))
                    .addOptional(block.getId());
        });
    }

    private static void genItemTags(RegistrateTagsProvider<Item> provIn) {
        TagGen.CreateTagsProvider<Item> prov = new TagGen.CreateTagsProvider<>(provIn, Item::builtInRegistryHolder);

        prov.tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
                .add(Items.JUKEBOX, Items.NOTE_BLOCK);
    }
}
