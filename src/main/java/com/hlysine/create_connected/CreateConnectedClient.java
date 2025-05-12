package com.hlysine.create_connected;


import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.eventbus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class CreateConnectedClient {
    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        CCPartialModels.register();
        modEventBus.addListener(CreateConnectedClient::init);
    }

    public static void init(final FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new CCPonderPlugin());
    }
}
