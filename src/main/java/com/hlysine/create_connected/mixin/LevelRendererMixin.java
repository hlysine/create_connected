package com.hlysine.create_connected.mixin;

import com.google.common.collect.Maps;
import com.hlysine.create_connected.content.jukebox.ContraptionJukeboxLevelRenderer;
import com.hlysine.create_connected.content.jukebox.ContraptionRecordSoundInstance;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static com.hlysine.create_connected.content.jukebox.JukeboxMovementBehaviour.JUKEBOX_MAP;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin implements ContraptionJukeboxLevelRenderer {
    @Unique
    private final Map<Integer, Map<BlockPos, SoundInstance>> playingContraptionRecords = Maps.newHashMap();

    @Override
    @Unique
    public Map<Integer, Map<BlockPos, SoundInstance>> getPlayingContraptionRecords() {
        return playingContraptionRecords;
    }

    @Nullable
    @Shadow
    private ClientLevel level;

    @Invoker
    abstract void callNotifyNearbyEntities(Level pLevel, BlockPos pPos, boolean pPlaying);

    @Unique
    private Pair<Integer, BlockPos> getJukeboxByPos(BlockPos pos) {
        Pair<Integer, BlockPos> ret = null;
        BlockPos target = null;
        for (Map.Entry<Integer, Map<BlockPos, BlockPos>> contraption : JUKEBOX_MAP.entrySet()) {
            for (Map.Entry<BlockPos, BlockPos> entry : contraption.getValue().entrySet()) {
                if (ret == null || pos.distSqr(entry.getValue()) < pos.distSqr(target)) {
                    ret = Pair.of(contraption.getKey(), entry.getKey());
                    target = entry.getValue();
                }
            }
        }
        return ret;
    }

    @Unique
    private void playContraptionMusic(@Nullable SoundEvent soundEvent, BlockPos pPos, @Nullable RecordItem recordItem) {
        Pair<Integer, BlockPos> jukeboxPos = getJukeboxByPos(pPos);
        if (jukeboxPos == null) return;
        Map<BlockPos, SoundInstance> contraptionMap = playingContraptionRecords.get(jukeboxPos.getFirst());
        if (contraptionMap != null) {
            SoundInstance soundinstance = contraptionMap.get(jukeboxPos.getSecond());
            if (soundinstance != null) {
                Minecraft.getInstance().getSoundManager().stop(soundinstance);
                contraptionMap.remove(jukeboxPos.getSecond());
                if (contraptionMap.isEmpty()) {
                    playingContraptionRecords.remove(jukeboxPos.getFirst());
                }
            }
        }

        if (soundEvent != null) {
            if (recordItem != null) {
                Minecraft.getInstance().gui.setNowPlaying(recordItem.getDisplayName());
            }

            Entity entity = level.getEntity(jukeboxPos.getFirst());
            if (!(entity instanceof AbstractContraptionEntity contraptionEntity)) return;
            SoundInstance soundInstance = new ContraptionRecordSoundInstance(
                    soundEvent,
                    SoundSource.RECORDS,
                    4.0F,
                    1.0F,
                    SoundInstance.createUnseededRandom(),
                    false,
                    0,
                    SoundInstance.Attenuation.LINEAR,
                    contraptionEntity,
                    jukeboxPos.getSecond()
            );
            playingContraptionRecords.computeIfAbsent(entity.getId(), $ -> new HashMap<>()).put(jukeboxPos.getSecond(), soundInstance);
            Minecraft.getInstance().getSoundManager().play(soundInstance);
        }

        callNotifyNearbyEntities(level, pPos, soundEvent != null);
    }

    @Inject(
            at = @At("HEAD"),
            method = "levelEvent(ILnet/minecraft/core/BlockPos;I)V",
            cancellable = true
    )
    public void contraptionRecordLevelEvent(int pType, BlockPos pPos, int pData, CallbackInfo ci) {
        if (pType == ~1010) {
            Item item = Item.byId(pData);
            if (item instanceof RecordItem recordItem) {
                playContraptionMusic(recordItem.getSound(), pPos, recordItem);
            }
            ci.cancel();
        } else if (pType == ~1011) {
            playContraptionMusic(null, pPos, null);
            ci.cancel();
        }
    }
}
