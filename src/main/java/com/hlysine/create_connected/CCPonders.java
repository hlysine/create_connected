package com.hlysine.create_connected;

import com.hlysine.create_connected.ponder.ChainCogwheelScenes;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;

public class CCPonders {

    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(CreateConnected.MODID);

    public static void register() {
        HELPER.forComponents(CCBlocks.ENCASED_CHAIN_COGWHEEL)
                .addStoryBoard("chain_cogwheel/relay", ChainCogwheelScenes::chainCogwheelAsRelay, AllPonderTags.KINETIC_RELAYS);

        PonderRegistry.TAGS.forTag(AllPonderTags.KINETIC_RELAYS)
                .add(CCBlocks.ENCASED_CHAIN_COGWHEEL);
    }
}
