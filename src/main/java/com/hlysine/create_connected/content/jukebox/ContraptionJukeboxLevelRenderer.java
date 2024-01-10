package com.hlysine.create_connected.content.jukebox;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;

import java.util.Map;

public interface ContraptionJukeboxLevelRenderer {
    Map<BlockPos, SoundInstance> getPlayingContraptionRecords();
}
