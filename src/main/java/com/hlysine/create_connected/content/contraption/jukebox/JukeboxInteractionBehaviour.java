package com.hlysine.create_connected.content.contraption.jukebox;

import com.hlysine.create_connected.CCPackets;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour;
import com.simibubi.create.foundation.utility.worldWrappers.WrappedWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.hlysine.create_connected.content.MathHelper.blockPosContaining;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HAS_RECORD;

public class JukeboxInteractionBehaviour extends MovingInteractionBehaviour {

    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos contraptionPos,
                                           AbstractContraptionEntity contraptionEntity) {
        if (player.level.isClientSide()) {
            return true;
        }
        Contraption contraption = contraptionEntity.getContraption();
        StructureTemplate.StructureBlockInfo info = contraption.getBlocks().get(contraptionPos);
        BlockState currentState = info.state;

        if (currentState.getValue(HAS_RECORD)) {
            withTempBlockEntity(contraption, contraptionPos, currentState, be -> {
                JukeboxBlock block = (JukeboxBlock) Blocks.JUKEBOX;
                block.use(be.getBlockState(),
                        be.getLevel(),
                        be.getBlockPos(),
                        player,
                        activeHand,
                        new BlockHitResult(Vec3.atBottomCenterOf(be.getBlockPos().above()), Direction.UP, be.getBlockPos(), false));
            }, false);
        } else {
            ItemStack item = player.getItemInHand(activeHand);
            if (item.is(ItemTags.MUSIC_DISCS)) {
                withTempBlockEntity(contraption, contraptionPos, currentState, be -> {
                    JukeboxBlock block = (JukeboxBlock) Blocks.JUKEBOX;
                    block.setRecord(player, be.getLevel(), be.getBlockPos(), be.getBlockState(), item);
                    be.getLevel().levelEvent(1010, be.getBlockPos(), Item.getId(item.getItem()));
                    if (!player.isCreative())
                        item.shrink(1);
                    player.awardStat(Stats.PLAY_RECORD);
                }, false);
            }
        }
        return true;
    }

    public void withTempBlockEntity(Contraption contraption, BlockPos contraptionPos, BlockState currentState, Consumer<JukeboxBlockEntity> action, boolean silent) {
        AtomicReference<BlockState> state = new AtomicReference<>(currentState);
        AbstractContraptionEntity contraptionEntity = contraption.entity;
        BlockPos realPos = blockPosContaining(contraptionEntity.toGlobalVector(Vec3.atCenterOf(contraptionPos), 1));
        JukeboxBlockEntity be = new JukeboxBlockEntity(realPos, currentState);
        be.load(contraption.getBlocks().get(contraptionPos).nbt);
        be.setLevel(new WrappedWorld(contraptionEntity.level) {
            @Nullable
            @Override
            public BlockEntity getBlockEntity(BlockPos pos) {
                if (pos.equals(realPos)) return be;
                return super.getBlockEntity(pos);
            }

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
                if (type == 1010)
                    CCPackets.getChannel().send(
                            PacketDistributor.DIMENSION.with(this::dimension),
                            new PlayContraptionJukeboxPacket(dimension().location(),
                                    contraptionEntity.getId(),
                                    contraptionPos,
                                    pos,
                                    data,
                                    Item.byId(data) instanceof RecordItem,
                                    silent
                            )
                    );
            }
        });
        action.accept(be);
        setContraptionBlockData(contraptionEntity, contraptionPos, new StructureTemplate.StructureBlockInfo(contraptionPos, state.get(), be.saveWithoutMetadata()));
    }
}
