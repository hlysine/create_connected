package com.hlysine.create_connected.content.contraption.jukebox;

import com.hlysine.create_connected.CCPackets;
import com.mojang.datafixers.util.Function7;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

public class PlayContraptionJukeboxPacket implements ClientboundPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayContraptionJukeboxPacket> STREAM_CODEC = composite(
            ResourceLocation.STREAM_CODEC, packet -> packet.level,
            ByteBufCodecs.VAR_INT, packet -> packet.contraptionId,
            BlockPos.STREAM_CODEC, packet -> packet.contraptionPos,
            BlockPos.STREAM_CODEC, packet -> packet.worldPos,
            ByteBufCodecs.VAR_INT, packet -> packet.recordId,
            ByteBufCodecs.BOOL, packet -> packet.play,
            ByteBufCodecs.BOOL, packet -> packet.silent,
            PlayContraptionJukeboxPacket::new
    );

    protected ResourceLocation level;
    protected int contraptionId;
    protected BlockPos contraptionPos;
    protected BlockPos worldPos;
    protected int recordId;
    protected boolean play;
    protected boolean silent;

    public PlayContraptionJukeboxPacket(ResourceLocation level, int contraptionId, BlockPos contraptionPos, BlockPos worldPos, int recordId, boolean play, boolean silent) {
        this.level = level;
        this.contraptionId = contraptionId;
        this.contraptionPos = contraptionPos;
        this.worldPos = worldPos;
        this.recordId = recordId;
        this.play = play;
        this.silent = silent;
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CCPackets.PLAY_CONTRAPTION_JUKEBOX;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(LocalPlayer player) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null || !world.dimension().location().equals(level))
            return;
        if (!world.isLoaded(worldPos))
            return;
        Entity entity = world.getEntity(contraptionId);
        if (!(entity instanceof AbstractContraptionEntity contraptionEntity))
            return;
        if (play) {
            Item item = Item.byId(recordId);
            Optional<JukeboxSong> song = ContraptionMusicManager.getSongFromItem(item, world.registryAccess());
            if (song.isEmpty())
                return;
            ContraptionMusicManager.playContraptionMusic(
                    song.get(),
                    contraptionEntity,
                    contraptionPos,
                    worldPos,
                    item,
                    silent
            );
        } else {
            ContraptionMusicManager.playContraptionMusic(
                    null,
                    contraptionEntity,
                    contraptionPos,
                    worldPos,
                    null,
                    silent
            );
        }
    }

    private static <B, C, T1, T2, T3, T4, T5, T6, T7> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> codec1,
            final Function<C, T1> getter1,
            final StreamCodec<? super B, T2> codec2,
            final Function<C, T2> getter2,
            final StreamCodec<? super B, T3> codec3,
            final Function<C, T3> getter3,
            final StreamCodec<? super B, T4> codec4,
            final Function<C, T4> getter4,
            final StreamCodec<? super B, T5> codec5,
            final Function<C, T5> getter5,
            final StreamCodec<? super B, T6> codec6,
            final Function<C, T6> getter6,
            final StreamCodec<? super B, T7> codec7,
            final Function<C, T7> getter7,
            final Function7<T1, T2, T3, T4, T5, T6, T7, C> factory
    ) {
        return new StreamCodec<B, C>() {// 258
            public @NotNull C decode(@NotNull B from) {
                T1 t1 = (T1) codec1.decode(from);
                T2 t2 = (T2) codec2.decode(from);
                T3 t3 = (T3) codec3.decode(from);
                T4 t4 = (T4) codec4.decode(from);
                T5 t5 = (T5) codec5.decode(from);
                T6 t6 = (T6) codec6.decode(from);
                T7 t7 = (T7) codec7.decode(from);
                return (C) factory.apply(t1, t2, t3, t4, t5, t6, t7);
            }

            public void encode(@NotNull B from, @NotNull C to) {
                codec1.encode(from, getter1.apply(to));
                codec2.encode(from, getter2.apply(to));
                codec3.encode(from, getter3.apply(to));
                codec4.encode(from, getter4.apply(to));
                codec5.encode(from, getter5.apply(to));
                codec6.encode(from, getter6.apply(to));
                codec7.encode(from, getter7.apply(to));
            }
        };
    }
}
