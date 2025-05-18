package com.hlysine.create_connected.content.contraption.jukebox;

import com.hlysine.create_connected.content.contraption.AutoPlayMovementBehaviour;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.ContraptionWorld;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class JukeboxMovementBehaviour extends AutoPlayMovementBehaviour {

    private void stopPlaying(MovementContext context) {
        if (context.world.isClientSide()) return;
        MovingInteractionBehaviour interactor = context.contraption.getInteractors().get(context.localPos);
        if (!(interactor instanceof JukeboxInteractionBehaviour jukeboxInteraction)) return;
        BlockState currentState = context.contraption.getBlocks().get(context.localPos).state();
        jukeboxInteraction.withTempBlockEntity(
                context.contraption,
                context.localPos,
                currentState,
                be -> be.jukeboxSongPlayer.stop(be.getLevel(), currentState),
                true
        );
    }

    @Override
    public void onDisabledByControls(MovementContext context) {
        super.onDisabledByControls(context);
        stopPlaying(context);
    }

    @Override
    public void stopMoving(MovementContext context) {
        stopPlaying(context);
    }

    @Override
    protected void update(MovementContext context, BlockState state, ContraptionWorld contraptionWorld, BlockPos contraptionPos, Level realWorld, BlockPos realPos, boolean wasActive, boolean isActive) {
        if (context.world.isClientSide()) return;
        if (context.disabled) return;
        MovingInteractionBehaviour interactor = context.contraption.getInteractors().get(context.localPos);
        if (!(interactor instanceof JukeboxInteractionBehaviour jukeboxInteraction)) return;
        jukeboxInteraction.withTempBlockEntity(context.contraption, context.localPos, state, be -> {
            if (!isActive) {
                if (!be.jukeboxSongPlayer.isPlaying())
                    JukeboxSong.fromStack(be.getLevel().registryAccess(), be.getTheItem())
                            .ifPresent(song -> be.jukeboxSongPlayer.play(be.getLevel(), song));
            } else {
                be.jukeboxSongPlayer.stop(be.getLevel(), state);
            }
        }, true);
    }
}
