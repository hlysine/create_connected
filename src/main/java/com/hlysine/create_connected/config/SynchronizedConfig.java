package com.hlysine.create_connected.config;

import com.hlysine.create_connected.CreateConnected;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public record SynchronizedConfig(List<Pair<ResourceLocation, Boolean>> map) {

    static SynchronizedConfig decode(FriendlyByteBuf buf) {
        CFeatures toggle = CCConfigs.common().toggle;
        List<ResourceLocation> keys = toggle.toggles.keySet().stream().sorted().toList();
        List<Pair<ResourceLocation, Boolean>> map = new LinkedList<>();
        for (ResourceLocation key : keys) {
            map.add(Pair.of(key, buf.readBoolean()));
        }
        return new SynchronizedConfig(map);
    }

    void encode(FriendlyByteBuf buf) {
        map.forEach(pair -> buf.writeBoolean(pair.getSecond()));
    }

    static SynchronizedConfig create() {
        CFeatures toggle = CCConfigs.common().toggle;
        List<ResourceLocation> keys = toggle.toggles.keySet().stream().sorted().toList();
        List<Pair<ResourceLocation, Boolean>> map = new LinkedList<>();
        for (ResourceLocation key : keys) {
            map.add(Pair.of(key, CCConfigs.common().toggle.toggles.get(key).get()));
        }
        return new SynchronizedConfig(map);
    }

    void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            if (ctx.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                CCConfigs.common().onReceiveConfig(this);
                CreateConnected.LOGGER.debug("Received and applied server config");
            } else {
                CreateConnected.LOGGER.debug("Received server config of " + ctx.getDirection());
            }
        });
        ctx.setPacketHandled(true);
    }

    public static class Network {
        private static final String VERSION = "1.0.0";
        static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
                CreateConnected.asResource("config_channel"),
                () -> VERSION,
                VERSION::equals,
                VERSION::equals
        );

        public static void register() {
            CHANNEL.registerMessage(
                    0,
                    SynchronizedConfig.class,
                    SynchronizedConfig::encode,
                    SynchronizedConfig::decode,
                    SynchronizedConfig::handle
            );
        }
    }
}
