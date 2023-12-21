package com.hlysine.create_connected.config;

import com.hlysine.create_connected.CreateConnected;
import com.simibubi.create.foundation.config.ConfigBase;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.network.PacketDistributor;

public class CCommon extends ConfigBase implements IsSynchronized {

    public final CFeatures toggle = nested(0, CFeatures::new, Comments.toggle);

    @Override
    public String getName() {
        return "common";
    }

    @Override
    public void onReceiveConfig(SynchronizedConfig config) {
        for (ConfigBase child : children) {
            if (child instanceof IsSynchronized sync) {
                sync.onReceiveConfig(config);
            }
        }
    }

    public static void syncConfig(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (player == null) return;
        CreateConnected.LOGGER.debug("Sending server config to " + player.getScoreboardName());
        SynchronizedConfig.Network.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), SynchronizedConfig.create());
    }

    private static class Comments {
        static String toggle = "Enable/disable features";
    }
}
