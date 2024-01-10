package com.hlysine.create_connected.mixin;

import com.google.common.collect.Maps;
import com.hlysine.create_connected.content.jukebox.ContraptionJukeboxLevelRenderer;
import com.hlysine.create_connected.content.jukebox.ContraptionRecordSoundInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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
import java.util.Map;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin implements ContraptionJukeboxLevelRenderer {
    @Unique
    private final Map<BlockPos, SoundInstance> playingContraptionRecords = Maps.newHashMap();

    @Override
    @Unique
    public Map<BlockPos, SoundInstance> getPlayingContraptionRecords() {
        return playingContraptionRecords;
    }

    @Nullable
    @Shadow
    private ClientLevel level;

    @Invoker
    abstract void callNotifyNearbyEntities(Level pLevel, BlockPos pPos, boolean pPlaying);

    @Unique
    private void playContraptionMusic(@Nullable SoundEvent soundEvent, BlockPos pPos, @Nullable RecordItem recordItem) {
        SoundInstance soundinstance = playingContraptionRecords.get(pPos);
        if (soundinstance != null) {
            Minecraft.getInstance().getSoundManager().stop(soundinstance);
            playingContraptionRecords.remove(pPos);
        }

        if (soundEvent != null) {
            if (recordItem != null) {
                Minecraft.getInstance().gui.setNowPlaying(recordItem.getDisplayName());
            }

            SoundInstance soundInstance = new ContraptionRecordSoundInstance(
                    soundEvent,
                    SoundSource.RECORDS,
                    4.0F,
                    1.0F,
                    SoundInstance.createUnseededRandom(),
                    false,
                    0,
                    SoundInstance.Attenuation.LINEAR
            );
            playingContraptionRecords.put(pPos, soundInstance);
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
