package com.hlysine.create_connected;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CCJukeboxSongs {
    private static final DeferredRegister<JukeboxSong> JUKEBOX_SONGS = DeferredRegister.create(Registries.JUKEBOX_SONG, CreateConnected.MODID);

    public static final DeferredHolder<JukeboxSong, JukeboxSong> INTERLUDE = register("interlude", CCSoundEvents.INTERLUDE_MUSIC.getMainEventHolder(), 189, 14);
    public static final DeferredHolder<JukeboxSong, JukeboxSong> ELEVATOR = register("elevator", CCSoundEvents.ELEVATOR_MUSIC.getMainEventHolder(), 240, 15);

    private static DeferredHolder<JukeboxSong, JukeboxSong> register(String key, Holder<SoundEvent> soundEvent, int lengthInSeconds, int comparatorOutput) {
        return JUKEBOX_SONGS.register(key, () -> new JukeboxSong(soundEvent, Component.translatable(translationId(key)), (float) lengthInSeconds, comparatorOutput));// 41 43
    }

    private static String translationId(String key) {
        return "item." + CreateConnected.MODID + ".music_disc_" + key + ".desc";
    }

    public static void register(IEventBus modEventBus) {
        JUKEBOX_SONGS.register(modEventBus);
    }
}
