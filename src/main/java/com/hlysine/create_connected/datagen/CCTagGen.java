package com.hlysine.create_connected.datagen;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.CCItems;
import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.compat.Mods;
import com.simibubi.create.AllTags;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class CCTagGen {
    public static void addGenerators() {
        CreateConnected.getRegistrate().addDataGenerator(ProviderType.BLOCK_TAGS, CCTagGen::genBlockTags);
        CreateConnected.getRegistrate().addDataGenerator(ProviderType.ITEM_TAGS, CCTagGen::genItemTags);
    }

    private static void genBlockTags(RegistrateTagsProvider<Block> prov) {
        prov.tag(BlockTags.create(Mods.DIAGONAL_FENCES.rl("non_diagonal_fences")))
                .add(CCBlocks.COPYCAT_FENCE.get())
                .add(CCBlocks.WRAPPED_COPYCAT_FENCE.get());
        prov.tag(BlockTags.create(Mods.DREAMS_DESIRES.rl("fan_processing_catalysts_freezing")))
                .add(CCBlocks.FAN_FREEZING_CATALYST.get());
        prov.tag(BlockTags.create(Mods.GARNISHED.rl("fan_processing_catalysts/freezing")))
                .add(CCBlocks.FAN_FREEZING_CATALYST.get());
    }

    private static void genItemTags(RegistrateTagsProvider<Item> prov) {
        prov.tag(ItemTags.CREEPER_DROP_MUSIC_DISCS)
                .add(CCItems.MUSIC_DISC_ELEVATOR.get())
                .add(CCItems.MUSIC_DISC_INTERLUDE.get());
        prov.tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
                .add(Items.JUKEBOX, Items.NOTE_BLOCK);
    }
}
