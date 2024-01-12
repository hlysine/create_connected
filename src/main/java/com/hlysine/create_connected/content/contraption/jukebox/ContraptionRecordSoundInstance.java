package com.hlysine.create_connected.content.contraption.jukebox;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.lang.ref.WeakReference;

public class ContraptionRecordSoundInstance extends AbstractTickableSoundInstance {
    public WeakReference<AbstractContraptionEntity> contraptionEntity;
    public BlockPos contraptionPos;

    public ContraptionRecordSoundInstance(SoundEvent pSoundEvent,
                                          SoundSource pSource,
                                          float pVolume,
                                          float pPitch,
                                          RandomSource pRandom,
                                          boolean pLooping,
                                          int pDelay,
                                          SoundInstance.Attenuation pAttenuation,
                                          AbstractContraptionEntity contraptionEntity,
                                          BlockPos contraptionPos) {
        super(pSoundEvent, pSource, pRandom);
        this.volume = pVolume;
        this.pitch = pPitch;
        this.looping = pLooping;
        this.delay = pDelay;
        this.attenuation = pAttenuation;
        this.contraptionEntity = new WeakReference<>(contraptionEntity);
        this.contraptionPos = contraptionPos;
        tick();
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        AbstractContraptionEntity entity = contraptionEntity.get();
        if (entity == null || entity.isRemoved()) {
            stop();
            return;
        }
        Vec3 vec = entity.toGlobalVector(Vec3.atCenterOf(contraptionPos), 1);
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }
}
