package com.hlysine.create_connected;

import com.hlysine.create_connected.content.contraption.jukebox.PlayContraptionJukeboxPacket;
import com.hlysine.create_connected.content.sequencedpulsegenerator.ConfigureSequencedPulseGeneratorPacket;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Locale;

public enum CCPackets implements BasePacketPayload.PacketTypeProvider {
    CONFIGURE_SEQUENCER(ConfigureSequencedPulseGeneratorPacket.class, ConfigureSequencedPulseGeneratorPacket.STREAM_CODEC),
    PLAY_CONTRAPTION_JUKEBOX(PlayContraptionJukeboxPacket.class, PlayContraptionJukeboxPacket::new);

    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> CCPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        String name = this.name().toLowerCase(Locale.ROOT);
        this.type = new CatnipPacketRegistry.PacketType<>(
                new CustomPacketPayload.Type<>(CreateConnected.asResource(name)),
                clazz, codec
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
        return (CustomPacketPayload.Type<T>) this.type.type();
    }

    public static void register() {
        CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(CreateConnected.MODID, 1);
        for (CCPackets packet : CCPackets.values()) {
            packetRegistry.registerPacket(packet.type);
        }
        packetRegistry.registerAllPackets();
    }
}

