package com.hlysine.create_connected;

import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = CreateConnected.MODID, dist = Dist.CLIENT)
public class CreateConnectedClient {
    public CreateConnectedClient(IEventBus modEventBus) {
        CCPartialModels.register();
        modEventBus.addListener(CreateConnectedClient::init);
    }

    public static void init(final FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new CCPonderPlugin());
    }
}
