package com.hlysine.create_connected.content.contraption.jukebox;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour;
import com.simibubi.create.foundation.utility.worldWrappers.WrappedWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HAS_RECORD;

public class JukeboxInteractionBehaviour extends MovingInteractionBehaviour {

    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos contraptionPos,
                                           AbstractContraptionEntity contraptionEntity) {
        if (player.level().isClientSide()) {
            return true;
        }
        Contraption contraption = contraptionEntity.getContraption();
        StructureTemplate.StructureBlockInfo info = contraption.getBlocks().get(contraptionPos);
        BlockState currentState = info.state();

        if (currentState.getValue(HAS_RECORD)) {
            withTempBlockEntity(contraption, contraptionPos, currentState, JukeboxBlockEntity::popOutRecord);
        } else {
            ItemStack item = player.getItemInHand(activeHand);
            if (item.is(ItemTags.MUSIC_DISCS)) {
                withTempBlockEntity(contraption, contraptionPos, currentState, be -> {
                    be.setFirstItem(item.copy());
                    be.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, be.getBlockPos(), GameEvent.Context.of(player, currentState));
                    item.shrink(1);
                    player.awardStat(Stats.PLAY_RECORD);
                });
            }
        }
        return true;
    }

    public void withTempBlockEntity(Contraption contraption, BlockPos contraptionPos, BlockState currentState, Consumer<JukeboxBlockEntity> action) {
        AtomicReference<BlockState> state = new AtomicReference<>(currentState);
        AbstractContraptionEntity contraptionEntity = contraption.entity;
        BlockPos realPos = BlockPos.containing(contraptionEntity.toGlobalVector(Vec3.atCenterOf(contraptionPos), 1));
        JukeboxBlockEntity be = new JukeboxBlockEntity(realPos, currentState);
        be.load(contraption.getBlocks().get(contraptionPos).nbt());
        be.setLevel(new WrappedWorld(contraptionEntity.level()) {
            @Override
            public boolean setBlock(BlockPos pos, BlockState newState, int flags) {
                if (pos.equals(realPos)) {
                    state.set(newState);
                    return true;
                }
                return false;
            }

            @Override
            public BlockState getBlockState(@Nullable BlockPos pos) {
                if (pos.equals(realPos))
                    return state.get();
                return super.getBlockState(pos);
            }

            @Override
            public void levelEvent(@Nullable Player player, int type, BlockPos pos, int data) {
                contraptionEntity.level().levelEvent(player, ~type, pos, data);
            }
        });
        action.accept(be);
        setContraptionBlockData(contraptionEntity, contraptionPos, new StructureTemplate.StructureBlockInfo(contraptionPos, state.get(), be.saveWithoutMetadata()));
    }
}
