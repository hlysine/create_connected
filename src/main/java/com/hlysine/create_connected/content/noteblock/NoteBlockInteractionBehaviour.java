package com.hlysine.create_connected.content.noteblock;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.SimpleBlockMovingInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.world.level.block.NoteBlock.INSTRUMENT;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.NOTE;

public class NoteBlockInteractionBehaviour extends SimpleBlockMovingInteraction {

    @Override
    protected BlockState handle(Player player, Contraption contraption, BlockPos contraptionPos, BlockState currentState) {
        AbstractContraptionEntity contraptionEntity = contraption.entity;
        Level contraptionWorld = contraption.getContraptionWorld();
        Level realWorld = player.level();
        BlockPos realPos = BlockPos.containing(contraptionEntity.toGlobalVector(Vec3.atCenterOf(contraptionPos), 1));
        int _new = net.minecraftforge.common.ForgeHooks.onNoteChange(contraptionWorld,
                contraptionPos,
                currentState,
                currentState.getValue(NOTE),
                currentState.cycle(NOTE).getValue(NOTE)
        );
        if (_new == -1) return currentState;
        currentState = currentState.setValue(NOTE, _new);

        if (currentState.getValue(INSTRUMENT).worksAboveNoteBlock() || contraptionWorld.getBlockState(contraptionPos.above()).isAir()) {
            currentState.triggerEvent(realWorld, realPos, 0, 0);
            realWorld.gameEvent(player, GameEvent.NOTE_BLOCK_PLAY, realPos);
        }

        player.awardStat(Stats.TUNE_NOTEBLOCK);
        return currentState;
    }
}
