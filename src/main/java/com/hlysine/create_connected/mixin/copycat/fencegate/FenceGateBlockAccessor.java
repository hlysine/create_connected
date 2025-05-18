package com.hlysine.create_connected.mixin.copycat.fencegate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FenceGateBlock.class)
public interface FenceGateBlockAccessor {
    @Invoker
    InteractionResult callUseWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult);
}
