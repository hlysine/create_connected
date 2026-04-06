package com.hlysine.create_connected;

import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

@Mod(value = CreateConnected.MODID, dist = Dist.CLIENT)
public class CreateConnectedClient {
    public CreateConnectedClient(IEventBus modEventBus) {
        CCPartialModels.register();
        modEventBus.addListener(CreateConnectedClient::init);
        modEventBus.addListener(CreateConnectedClient::registerModelLoader);
    }

    public static void init(final FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new CCPonderPlugin());
    }

    public static void registerModelLoader(ModelEvent.RegisterGeometryLoaders event) {
        event.register(GatedModelLoader.ID, GatedModelLoader.INSTANCE);
    }
}
