package com.hlysine.create_connected;

import net.createmod.ponder.foundation.PonderIndex;

/**
 * Steps to add ponders for an existing component:
 * 1. Create a schematic .nbt file in resources/ponder
 * 2. Create a [Component]Scenes file in src/ponder with scene instructions
 * 3. Register the file and the scenes here
 * 4. Remove the placeholder tooltip in resources/lang/default/tooltips.json
 */
public class CCPonders {

    public static void register() {
        PonderIndex.addPlugin(new CCPlugin());
    }
}
