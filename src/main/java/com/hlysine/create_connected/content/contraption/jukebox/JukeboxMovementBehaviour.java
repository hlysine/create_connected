package com.hlysine.create_connected.content.contraption.jukebox;

import com.hlysine.create_connected.content.contraption.AutoPlayMovementBehaviour;
import com.simibubi.create.content.contraptions.ContraptionWorld;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class JukeboxMovementBehaviour extends AutoPlayMovementBehaviour {
    public static final Map<Integer, Map<BlockPos, BlockPos>> JUKEBOX_MAP = new HashMap<>();

    @Override
    public void stopMoving(MovementContext context) {
        updateMapPos(context);
        MovingInteractionBehaviour interactor = context.contraption.getInteractors().get(context.localPos);
        if (!(interactor instanceof JukeboxInteractionBehaviour jukeboxInteraction)) return;
        BlockState currentState = context.contraption.getBlocks().get(context.localPos).state();
        jukeboxInteraction.withTempBlockEntity(context.contraption, context.localPos, currentState, JukeboxBlockEntity::stopPlaying);

        Map<BlockPos, BlockPos> contraptionMap = JUKEBOX_MAP.computeIfAbsent(context.contraption.entity.getId(), $ -> new HashMap<>());
        contraptionMap.remove(context.localPos);
        if (contraptionMap.isEmpty())
            JUKEBOX_MAP.remove(context.contraption.entity.getId());
    }

    @Override
    public void tick(MovementContext context) {
        super.tick(context);
        if (!context.world.isClientSide()) return;

        updateMapPos(context);
    }

    private void updateMapPos(MovementContext context) {
        BlockPos realPos = BlockPos.containing(context.contraption.entity.toGlobalVector(Vec3.atCenterOf(context.localPos), 1));
        JUKEBOX_MAP.computeIfAbsent(context.contraption.entity.getId(), $ -> new HashMap<>()).put(context.localPos, realPos);
    }

    @Override
    protected void update(MovementContext context, BlockState state, ContraptionWorld contraptionWorld, BlockPos contraptionPos, Level realWorld, BlockPos realPos, boolean wasActive, boolean isActive) {
        if (context.world.isClientSide()) return;
        MovingInteractionBehaviour interactor = context.contraption.getInteractors().get(context.localPos);
        if (!(interactor instanceof JukeboxInteractionBehaviour jukeboxInteraction)) return;
        jukeboxInteraction.withTempBlockEntity(context.contraption, context.localPos, state, be -> {
            if (!isActive) {
                if (!be.isRecordPlaying()) be.startPlaying();
            } else {
                be.stopPlaying();
            }
        });
    }
}
