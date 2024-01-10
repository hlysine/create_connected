package com.hlysine.create_connected.content.jukebox;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HAS_RECORD;

public class ContraptionRecordSoundInstance extends AbstractTickableSoundInstance {
    public AbstractContraptionEntity contraptionEntity;
    public BlockPos contraptionPos;
    private float targetVolume;
    private int recordCheck = 0;

    public ContraptionRecordSoundInstance(SoundEvent pSoundEvent,
                                          SoundSource pSource,
                                          float pVolume,
                                          float pPitch,
                                          RandomSource pRandom,
                                          boolean pLooping,
                                          int pDelay,
                                          SoundInstance.Attenuation pAttenuation) {
        super(pSoundEvent, pSource, pRandom);
        this.targetVolume = pVolume;
        this.volume = 0;
        this.pitch = pPitch;
        this.looping = pLooping;
        this.delay = pDelay;
        this.attenuation = pAttenuation;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        if (contraptionEntity == null) {
            this.volume = 0;
            return;
        }
        this.volume = targetVolume;
        Vec3 vec = contraptionEntity.toGlobalVector(Vec3.atCenterOf(contraptionPos), 1);
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        if (recordCheck-- < 0) {
            recordCheck = 10;
            if (!contraptionEntity.getContraption().getBlocks().get(contraptionPos).state().getValue(HAS_RECORD))
                stop();
        }
    }
}
