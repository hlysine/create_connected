package com.hlysine.create_connected.content.contraption.jukebox;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.RecordItem;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ContraptionMusicManager {
    private static final Map<Pair<Integer, BlockPos>, SoundInstance> playingContraptionRecords = new HashMap<>();

    public static void playContraptionMusic(@Nullable SoundEvent soundEvent,
                                            AbstractContraptionEntity entity,
                                            BlockPos localPos,
                                            BlockPos worldPos,
                                            @Nullable RecordItem recordItem,
                                            boolean silent) {
        Pair<Integer, BlockPos> contraption = Pair.of(entity.getId(), localPos);
        SoundInstance soundInstance = playingContraptionRecords.get(contraption);
        if (soundInstance != null) {
            Minecraft.getInstance().getSoundManager().stop(soundInstance);
            playingContraptionRecords.remove(contraption);
        }

        if (soundEvent != null) {
            if (recordItem != null && !silent) {
                Minecraft.getInstance().gui.setNowPlaying(recordItem.getDisplayName());
            }

            SoundInstance newInstance = new ContraptionRecordSoundInstance(
                    soundEvent,
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
        Minecraft.getInstance().levelRenderer.notifyNearbyEntities(Minecraft.getInstance().level, worldPos, soundEvent != null);
    }
}
