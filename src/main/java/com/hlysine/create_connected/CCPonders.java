package com.hlysine.create_connected;

import com.hlysine.create_connected.ponder.ChainCogwheelScenes;
import com.hlysine.create_connected.ponder.InvertedClutchScenes;
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
                .addStoryBoard("chain_cogwheel/relay", ChainCogwheelScenes::chainCogwheelAsRelay, AllPonderTags.KINETIC_RELAYS);
        HELPER.forComponents(CCBlocks.INVERTED_CLUTCH)
                .addStoryBoard("inverted_clutch", InvertedClutchScenes::invertedClutch, AllPonderTags.KINETIC_RELAYS);

        PonderRegistry.TAGS.forTag(AllPonderTags.KINETIC_RELAYS)
                .add(CCBlocks.ENCASED_CHAIN_COGWHEEL)
                .add(CCBlocks.INVERTED_CLUTCH);
    }
}
