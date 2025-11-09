package com.hlysine.create_connected.config;

import com.hlysine.create_connected.CreateConnected;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = CreateConnected.MODID)
public class CCommon extends SyncConfigBase {

    @Override
    public @NotNull String getName() {
        return "common";
    }

    public final ConfigBool migrateCopycatsOnBlockUpdate = b(true, "migrateCopycatsOnBlockUpdate", Comments.migrateCopycatsOnBlockUpdate);

    public final ConfigBool migrateCopycatsOnInitialize = b(true, "migrateCopycatsOnInitialize", Comments.migrateCopycatsOnInitialize);

    public final CFeatures toggle = nested(0, CFeatures::new, Comments.toggle);

    public final CFeatureCategories categories = nested(0, CFeatureCategories::new, Comments.categories);

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        CCConfigs.common().registerAsSyncRoot(event, "2.0.0");
    }

    @SubscribeEvent
    public static void register(final RegisterConfigurationTasksEvent event) {
        event.register(new CommonSyncConfigTask(event.getListener()));
    }

    private static class Comments {
        static String toggle = "Enable/disable features. Values on server override clients";
        static String categories = "Enable/disable categories of features. Disabling a category hides all related features. Values on server override clients";
        static String migrateCopycatsOnBlockUpdate = "Migrate copycats to Create: Copycats+ when they receive a block update";
        static String migrateCopycatsOnInitialize = "Migrate copycats to Create: Copycats+ when their block entities are initialized";
    }

    public static class CommonSyncConfigTask extends SyncConfigTask {
        public CommonSyncConfigTask(ServerConfigurationPacketListener listener) {
            super(listener);
        }

        @Override
        protected SyncConfigBase getSyncConfig() {
            return CCConfigs.common();
        }
    }
}
