package com.hlysine.create_connected;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.JsonCodecProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CCJukeboxSongs {
    private static final Map<ResourceKey<JukeboxSong>, JukeboxSong> JUKEBOX_SONGS = new HashMap<>();


    public static final ResourceKey<JukeboxSong> INTERLUDE = register("interlude",
            CCSoundEvents.INTERLUDE_MUSIC.getMainEventHolder(),
            189, 14
    );

    public static final ResourceKey<JukeboxSong> ELEVATOR = register("elevator",
            CCSoundEvents.ELEVATOR_MUSIC.getMainEventHolder(),
            240, 15
    );


    private static ResourceKey<JukeboxSong> register(String key, Holder<SoundEvent> soundEvent, int lengthInSeconds, int comparatorOutput) {
        ResourceKey<JukeboxSong> songKey = ResourceKey.create(Registries.JUKEBOX_SONG, CreateConnected.asResource(key));
        JUKEBOX_SONGS.put(
                songKey,
                new JukeboxSong(soundEvent, Component.translatable(translationId(key)), (float) lengthInSeconds, comparatorOutput)
        );
        return songKey;
    }

    private static String translationId(String key) {
        return "item." + CreateConnected.MODID + ".music_disc_" + key + ".desc";
    }

    public static JukeboxSongProvider provider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        return new JukeboxSongProvider(output, lookupProvider, existingFileHelper);
    }

    public static class JukeboxSongProvider extends JsonCodecProvider<JukeboxSong> {
        public JukeboxSongProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
            super(output, PackOutput.Target.DATA_PACK, "jukebox_song", PackType.SERVER_DATA, JukeboxSong.DIRECT_CODEC, lookupProvider, CreateConnected.MODID, existingFileHelper);
        }

        @Override
        protected void gather() {
            for (Map.Entry<ResourceKey<JukeboxSong>, JukeboxSong> entry : JUKEBOX_SONGS.entrySet()) {
                unconditional(entry.getKey().location(), entry.getValue());
            }
        }
    }
}
