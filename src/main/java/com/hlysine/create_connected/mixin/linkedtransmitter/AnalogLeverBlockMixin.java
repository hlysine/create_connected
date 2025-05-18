package com.hlysine.create_connected.mixin.linkedtransmitter;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.CCItems;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnalogLeverBlock.class)
public abstract class AnalogLeverBlockMixin extends FaceAttachedHorizontalDirectionalBlock {

    protected AnalogLeverBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(
            cancellable = true,
            at = @At("HEAD"),
            method = "useWithoutItem"
    )
    private void use(BlockState state, Level worldIn, BlockPos pos, Player player, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (player.isHolding(CCItems.LINKED_TRANSMITTER.get()) && !state.is(CCBlocks.LINKED_ANALOG_LEVER.get())) {
            cir.setReturnValue(InteractionResult.PASS);
            cir.cancel();
        }
    }
}
