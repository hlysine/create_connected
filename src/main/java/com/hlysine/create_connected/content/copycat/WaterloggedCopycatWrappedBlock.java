package com.hlysine.create_connected.content.copycat;

import com.simibubi.create.AllTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public abstract class WaterloggedCopycatWrappedBlock extends MigratingWaterloggedCopycatBlock implements ICopycatWithWrappedBlock {

    public WaterloggedCopycatWrappedBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        InteractionResult result = super.useWithoutItem(state, level, pos, player, hitResult);
        if (!result.consumesAction()) {
            return ICopycatWithWrappedBlock.wrappedState(getWrappedBlock(), state).useWithoutItem(level, player, hitResult);
        }
        return result;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemInteractionResult result = super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        if (!result.consumesAction() && !player.getItemInHand(hand).is(Tags.Items.TOOLS_WRENCH)) {
            return ICopycatWithWrappedBlock.wrappedState(getWrappedBlock(), state).useItemOn(stack, level, player, hand, hitResult);
        }
        return result;
    }
}
