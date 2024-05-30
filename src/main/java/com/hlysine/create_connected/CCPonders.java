package com.hlysine.create_connected;

import com.hlysine.create_connected.ponder.*;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;

/**
 * Steps to add ponders for an existing component:
 * 1. Create a schematic .nbt file in resources/ponder
 * 2. Create a [Component]Scenes file in src/ponder with scene instructions
 * 3. Register the file and the scenes here
 * 4. Remove the placeholder tooltip in resources/lang/default/tooltips.json
 */
public class CCPonders {

    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(CreateConnected.MODID);

    public static void register() {
        HELPER.forComponents(CCBlocks.ENCASED_CHAIN_COGWHEEL)
                .addStoryBoard("chain_cogwheel", ChainCogwheelScenes::chainCogwheelAsRelay, AllPonderTags.KINETIC_RELAYS);
        HELPER.forComponents(CCBlocks.CRANK_WHEEL, CCBlocks.LARGE_CRANK_WHEEL)
                .addStoryBoard("crank_wheel", CrankWheelScenes::crankWheel, AllPonderTags.KINETIC_SOURCES);
        HELPER.forComponents(CCBlocks.INVERTED_CLUTCH)
                .addStoryBoard("inverted_clutch", InvertedClutchScenes::invertedClutch, AllPonderTags.KINETIC_RELAYS);
        HELPER.forComponents(CCBlocks.INVERTED_GEARSHIFT)
                .addStoryBoard("inverted_gearshift", InvertedGearshiftScenes::invertedGearshift, AllPonderTags.KINETIC_RELAYS);
        HELPER.forComponents(CCBlocks.PARALLEL_GEARBOX, CCItems.VERTICAL_PARALLEL_GEARBOX)
                .addStoryBoard("parallel_gearbox", ParallelGearboxScenes::parallelGearbox, AllPonderTags.KINETIC_RELAYS);
        HELPER.forComponents(CCBlocks.SEQUENCED_PULSE_GENERATOR)
                .addStoryBoard("sequenced_pulse_generator", SequencedPulseGeneratorScenes::pulseGenerator, AllPonderTags.REDSTONE);
        HELPER.forComponents(CCItems.LINKED_TRANSMITTER)
                .addStoryBoard("linked_transmitter", LinkedTransmitterScenes::linkedTransmitter, AllPonderTags.REDSTONE);
        HELPER.forComponents(CCBlocks.INVENTORY_ACCESS_PORT)
                .addStoryBoard("inventory_access_port", InventoryAccessPortScenes::inventoryAccessPort, AllPonderTags.LOGISTICS);
        HELPER.forComponents(CCBlocks.INVENTORY_BRIDGE)
                .addStoryBoard("inventory_bridge", InventoryBridgeScenes::inventoryBridge, AllPonderTags.LOGISTICS);

        PonderRegistry.TAGS.forTag(AllPonderTags.KINETIC_SOURCES)
                .add(CCBlocks.CRANK_WHEEL);
        PonderRegistry.TAGS.forTag(AllPonderTags.KINETIC_RELAYS)
                .add(CCBlocks.ENCASED_CHAIN_COGWHEEL)
                .add(CCBlocks.INVERTED_CLUTCH)
                .add(CCBlocks.INVERTED_GEARSHIFT)
                .add(CCBlocks.PARALLEL_GEARBOX);
        PonderRegistry.TAGS.forTag(AllPonderTags.REDSTONE)
                .add(CCBlocks.SEQUENCED_PULSE_GENERATOR)
                .add(CCItems.LINKED_TRANSMITTER);
        PonderRegistry.TAGS.forTag(AllPonderTags.LOGISTICS)
                .add(CCBlocks.INVENTORY_ACCESS_PORT)
                .add(CCBlocks.INVENTORY_BRIDGE);
    }
}
