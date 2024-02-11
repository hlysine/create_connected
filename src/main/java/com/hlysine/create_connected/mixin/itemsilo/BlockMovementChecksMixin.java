package com.hlysine.create_connected.mixin.itemsilo;

import com.hlysine.create_connected.content.itemsilo.ItemSiloBlock;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.contraptions.BlockMovementChecks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockMovementChecks.class, remap = false)
public class BlockMovementChecksMixin {

    @Inject(
            at = @At("HEAD"),
            method = "isBlockAttachedTowardsFallback(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z",
            cancellable = true
    )
    private static void isItemSiloAttached(BlockState state, Level world, BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (state.getBlock() instanceof ItemSiloBlock)
            cir.setReturnValue(ConnectivityHandler.isConnected(world, pos, pos.relative(direction)));
    }
}
