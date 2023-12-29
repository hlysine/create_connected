package com.hlysine.create_connected;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class CreateConnectedClient {
    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        CCPartialModels.register();
        modEventBus.addListener(CreateConnectedClient::init);
    }

    public static void init(final FMLClientSetupEvent event) {
        CCPonders.register();
    }
}
