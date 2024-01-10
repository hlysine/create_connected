package com.hlysine.create_connected.content.jukebox;

import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class JukeboxMovementBehaviour implements MovementBehaviour {
    @Override
    public void stopMoving(MovementContext context) {
        MovingInteractionBehaviour interactor = context.contraption.getInteractors().get(context.localPos);
        if (!(interactor instanceof JukeboxInteractionBehaviour jukeboxInteraction)) return;
        BlockState currentState = context.contraption.getBlocks().get(context.localPos).state();
        jukeboxInteraction.withTempBlockEntity(context.contraption, context.localPos, currentState, JukeboxBlockEntity::stopPlaying);
    }

    @Override
    public boolean mustTickWhileDisabled() {
        return true;
    }

    @Override
    public void tick(MovementContext context) {
        if (!context.world.isClientSide()) return;
        ContraptionJukeboxLevelRenderer levelRenderer = (ContraptionJukeboxLevelRenderer) Minecraft.getInstance().levelRenderer;
        SoundInstance soundInstance = levelRenderer.getPlayingContraptionRecords().get(context.localPos);
        if (!(soundInstance instanceof ContraptionRecordSoundInstance instance)) return;
        instance.contraptionEntity = context.contraption.entity;
        instance.contraptionPos = context.localPos;
    }
}
