package com.hlysine.create_connected.content.contraption.jukebox;

import com.hlysine.create_connected.content.contraption.AutoPlayMovementBehaviour;
import com.simibubi.create.content.contraptions.ContraptionWorld;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class JukeboxMovementBehaviour extends AutoPlayMovementBehaviour {

    private void stopPlaying(MovementContext context) {
        if (context.world.isClientSide()) return;
        MovingInteractionBehaviour interactor = context.contraption.getInteractors().get(context.localPos);
        if (!(interactor instanceof JukeboxInteractionBehaviour jukeboxInteraction)) return;
        BlockState currentState = context.contraption.getBlocks().get(context.localPos).state;
        jukeboxInteraction.withTempBlockEntity(context.contraption, context.localPos, currentState, be -> {
            be.getLevel().levelEvent(1010, be.getBlockPos(), 0);
        }, true);
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
                be.getLevel().levelEvent(1010, be.getBlockPos(), Item.getId(be.getRecord().getItem()));
            } else {
                be.getLevel().levelEvent(1010, be.getBlockPos(), 0);
            }
        }, true);
    }
}
