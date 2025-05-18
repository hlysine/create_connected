package com.hlysine.create_connected.content.contraption.jukebox;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ContraptionMusicManager {
    private static final Map<Pair<Integer, BlockPos>, SoundInstance> playingContraptionRecords = new HashMap<>();

    public static void playContraptionMusic(@Nullable JukeboxSong song,
                                            AbstractContraptionEntity entity,
                                            BlockPos localPos,
                                            BlockPos worldPos,
                                            boolean silent) {
        Pair<Integer, BlockPos> contraption = Pair.of(entity.getId(), localPos);
        SoundInstance soundInstance = playingContraptionRecords.get(contraption);
        if (soundInstance != null) {
            Minecraft.getInstance().getSoundManager().stop(soundInstance);
            playingContraptionRecords.remove(contraption);
        }

        if (song != null) {
            if (!silent) {
                Minecraft.getInstance().gui.setNowPlaying(song.description());
            }

            SoundInstance newInstance = new ContraptionRecordSoundInstance(
                    song.soundEvent().value(),
                    SoundSource.RECORDS,
                    4.0F,
                    1.0F,
                    SoundInstance.createUnseededRandom(),
                    false,
                    0,
                    SoundInstance.Attenuation.LINEAR,
                    entity,
                    localPos
            );
            playingContraptionRecords.put(contraption, newInstance);
            Minecraft.getInstance().getSoundManager().play(newInstance);
        }
        Minecraft.getInstance().levelRenderer.notifyNearbyEntities(Minecraft.getInstance().level, worldPos, song != null);
    }
}
