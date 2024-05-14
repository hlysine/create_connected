package com.hlysine.create_connected.datagen;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.CCItems;
import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.compat.Mods;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("deprecation")
public class CCTagGen {
    public static void addGenerators() {
        CreateConnected.getRegistrate().addDataGenerator(ProviderType.BLOCK_TAGS, CCTagGen::genBlockTags);
        CreateConnected.getRegistrate().addDataGenerator(ProviderType.ITEM_TAGS, CCTagGen::genItemTags);
    }

    private static void genBlockTags(RegistrateTagsProvider<Block> provIn) {
        TagGen.CreateTagsProvider<Block> prov = new TagGen.CreateTagsProvider<>(provIn, Block::builtInRegistryHolder);
        prov.tag(TagKey.create(ForgeRegistries.Keys.BLOCKS, Mods.DIAGONAL_FENCES.rl("non_diagonal_fences")))
                .add(CCBlocks.COPYCAT_FENCE.get())
                .add(CCBlocks.WRAPPED_COPYCAT_FENCE.get());
    }

    private static void genItemTags(RegistrateTagsProvider<Item> provIn) {
        TagGen.CreateTagsProvider<Item> prov = new TagGen.CreateTagsProvider<>(provIn, Item::builtInRegistryHolder);

        prov.tag(ItemTags.CREEPER_DROP_MUSIC_DISCS)
                .add(CCItems.MUSIC_DISC_ELEVATOR.get())
                .add(CCItems.MUSIC_DISC_INTERLUDE.get());
        prov.tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
                .add(Items.JUKEBOX, Items.NOTE_BLOCK);
    }
}
