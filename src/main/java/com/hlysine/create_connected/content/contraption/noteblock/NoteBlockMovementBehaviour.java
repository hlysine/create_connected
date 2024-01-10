package com.hlysine.create_connected.content.contraption.noteblock;

import com.hlysine.create_connected.content.contraption.AutoPlayMovementBehaviour;
import com.simibubi.create.content.contraptions.ContraptionWorld;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import static net.minecraft.world.level.block.NoteBlock.INSTRUMENT;

public class NoteBlockMovementBehaviour extends AutoPlayMovementBehaviour {

    @Override
    protected void update(MovementContext context, BlockState state, ContraptionWorld contraptionWorld, BlockPos contraptionPos, Level realWorld, BlockPos realPos, boolean wasActive, boolean isActive) {
        if (!isActive) return;
        if (state.getValue(INSTRUMENT).worksAboveNoteBlock() || contraptionWorld.getBlockState(contraptionPos.above()).isAir()) {
            state.triggerEvent(realWorld, realPos, 0, 0);
            realWorld.gameEvent(context.contraption.entity, GameEvent.NOTE_BLOCK_PLAY, realPos);
        }
    }
}

