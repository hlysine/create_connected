package com.hlysine.create_connected.content.brasschute;

import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class BrassChuteBlock extends ChuteBlock {
    public BrassChuteBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Shape shape = state.getValue(SHAPE);
        boolean down = state.getValue(FACING) == Direction.DOWN;
        if (shape == Shape.INTERSECTION)
            return InteractionResult.PASS;
        Level level = context.getLevel();
        if (level.isClientSide)
            return InteractionResult.SUCCESS;
        if (shape == Shape.ENCASED) {
            level.setBlockAndUpdate(context.getClickedPos(), state.setValue(SHAPE, Shape.NORMAL));
            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, context.getClickedPos(),
                    Block.getId(AllBlocks.BRASS_BLOCK.getDefaultState()));
            return InteractionResult.SUCCESS;
        }
        if (down)
            level.setBlockAndUpdate(context.getClickedPos(),
                    state.setValue(SHAPE, shape != Shape.NORMAL ? Shape.NORMAL : Shape.WINDOW));
        return InteractionResult.SUCCESS;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        Shape shape = state.getValue(SHAPE);
        if (!AllBlocks.BRASS_BLOCK.isIn(stack))
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        if (shape == Shape.INTERSECTION || shape == Shape.ENCASED)
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        if (player == null || level.isClientSide)
            return ItemInteractionResult.SUCCESS;

        level.setBlockAndUpdate(pos, state.setValue(SHAPE, Shape.ENCASED));
        level.playSound(null, pos, SoundEvents.NETHERITE_BLOCK_HIT, SoundSource.BLOCKS, 0.5f, 1.05f);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public BlockEntityType<? extends ChuteBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.BRASS_CHUTE.get();
    }
}
