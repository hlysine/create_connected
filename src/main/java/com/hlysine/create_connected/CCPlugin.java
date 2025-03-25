package com.hlysine.create_connected;

import com.hlysine.create_connected.ponder.*;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CCPlugin implements PonderPlugin {
    @Override
    public @NotNull String getModId() {
        return CreateConnected.MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        register(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        register(helper);
    }

    @Override
    public void registerSharedText(SharedTextRegistrationHelper helper) {
        PonderPlugin.super.registerSharedText(helper);
    }

    @Override
    public void onPonderLevelRestore(PonderLevel ponderLevel) {
        PonderPlugin.super.onPonderLevelRestore(ponderLevel);
    }

    @Override
    public void indexExclusions(IndexExclusionHelper helper) {
        PonderPlugin.super.indexExclusions(helper);
    }

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?>> SCENE_HELPER = helper.withKeyFunction(RegistryEntry::getId);

        SCENE_HELPER.forComponents(CCBlocks.ENCASED_CHAIN_COGWHEEL)
                .addStoryBoard("chain_cogwheel", ChainCogwheelScenes::chainCogwheelAsRelay, AllCreatePonderTags.KINETIC_RELAYS);
        SCENE_HELPER.forComponents(CCBlocks.CRANK_WHEEL, CCBlocks.LARGE_CRANK_WHEEL)
                .addStoryBoard("crank_wheel", CrankWheelScenes::crankWheel, AllCreatePonderTags.KINETIC_SOURCES);
        SCENE_HELPER.forComponents(CCBlocks.INVERTED_CLUTCH)
                .addStoryBoard("inverted_clutch", InvertedClutchScenes::invertedClutch, AllCreatePonderTags.KINETIC_RELAYS);
        SCENE_HELPER.forComponents(CCBlocks.INVERTED_GEARSHIFT)
                .addStoryBoard("inverted_gearshift", InvertedGearshiftScenes::invertedGearshift, AllCreatePonderTags.KINETIC_RELAYS);
        SCENE_HELPER.forComponents(CCBlocks.PARALLEL_GEARBOX, CCItems.VERTICAL_PARALLEL_GEARBOX)
                .addStoryBoard("parallel_gearbox", ParallelGearboxScenes::parallelGearbox, AllCreatePonderTags.KINETIC_RELAYS);
        SCENE_HELPER.forComponents(CCBlocks.SEQUENCED_PULSE_GENERATOR)
                .addStoryBoard("sequenced_pulse_generator", SequencedPulseGeneratorScenes::pulseGenerator, AllCreatePonderTags.REDSTONE);
        SCENE_HELPER.forComponents(CCItems.LINKED_TRANSMITTER)
                .addStoryBoard("linked_transmitter", LinkedTransmitterScenes::linkedTransmitter, AllCreatePonderTags.REDSTONE);
        SCENE_HELPER.forComponents(CCBlocks.INVENTORY_ACCESS_PORT)
                .addStoryBoard("inventory_access_port", InventoryAccessPortScenes::inventoryAccessPort, AllCreatePonderTags.LOGISTICS);
        SCENE_HELPER.forComponents(CCBlocks.INVENTORY_BRIDGE)
                .addStoryBoard("inventory_bridge", InventoryBridgeScenes::inventoryBridge, AllCreatePonderTags.LOGISTICS)
                .addStoryBoard("inventory_bridge_filter", InventoryBridgeScenes::filtering, AllCreatePonderTags.LOGISTICS);
    }

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?>> TAG_HELPER = helper.withKeyFunction(RegistryEntry::getId);

        TAG_HELPER.addToTag(AllCreatePonderTags.KINETIC_SOURCES)
                .add(CCBlocks.CRANK_WHEEL);
        TAG_HELPER.addToTag(AllCreatePonderTags.KINETIC_RELAYS)
                .add(CCBlocks.ENCASED_CHAIN_COGWHEEL)
                .add(CCBlocks.INVERTED_CLUTCH)
                .add(CCBlocks.INVERTED_GEARSHIFT)
                .add(CCBlocks.PARALLEL_GEARBOX);
        TAG_HELPER.addToTag(AllCreatePonderTags.REDSTONE)
                .add(CCBlocks.SEQUENCED_PULSE_GENERATOR)
                .add(CCItems.LINKED_TRANSMITTER);
        TAG_HELPER.addToTag(AllCreatePonderTags.LOGISTICS)
                .add(CCBlocks.INVENTORY_ACCESS_PORT)
                .add(CCBlocks.INVENTORY_BRIDGE);
    }
}