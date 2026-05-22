package com.hlysine.create_connected.content.linkedtransmitter;

import com.hlysine.create_connected.CCShapes;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public interface LinkedTransmitterBlock {
    Block getBlock();

    Block getBase();

    void replaceBase(BlockState baseState, Level world, BlockPos pos);

    default VoxelShape getTransmitterShape(BlockState state) {
        Direction facing = state.getValue(FaceAttachedHorizontalDirectionalBlock.FACING);
        return switch (state.getValue(FaceAttachedHorizontalDirectionalBlock.FACE)) {
            case FLOOR -> CCShapes.FLOOR_LINKED_TRANSMITTER.get(facing);
            case WALL -> CCShapes.WALL_LINKED_TRANSMITTER.get(facing);
            case CEILING -> CCShapes.CEILING_LINKED_TRANSMITTER.get(facing);
        };
    }

    default boolean isHittingBase(BlockState state, BlockGetter level, BlockPos pos, HitResult hit) {
        return !getTransmitterShape(state).bounds().inflate(0.01 / 16).move(pos).contains(hit.getLocation());
    }

    default @NotNull InteractionResult useTransmitter(@NotNull BlockState state,
                                                      @NotNull Level level,
                                                      @NotNull BlockPos pos,
                                                      @NotNull Player player) {
        if (state.getValue(BlockStateProperties.LOCKED))
            return InteractionResult.CONSUME;
        return InteractionResult.PASS;
    }

    default ItemInteractionResult useWax(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.HONEYCOMB) && !state.getValue(BlockStateProperties.LOCKED)) {
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
            }
            if (!player.isCreative())
                stack.shrink(1);
            BlockState newState = state.setValue(BlockStateProperties.LOCKED, true);
            level.setBlock(pos, newState, Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_ALL);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));
            level.levelEvent(player, 3003, pos, 0);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (stack.is(ItemTags.AXES) && state.getValue(BlockStateProperties.LOCKED)) {
            level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3004, pos, 0);
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, pos, stack);
            }
            BlockState newState = state.setValue(BlockStateProperties.LOCKED, false);
            level.setBlock(pos, newState, Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_ALL);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));
            if (player != null && !player.isCreative()) {
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            }

            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
