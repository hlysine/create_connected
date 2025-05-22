package com.hlysine.create_connected.mixin.kineticbattery;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.CCItems;
import com.simibubi.create.content.kinetics.deployer.DeployerHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DeployerHandler.class, remap = false)
public class DeployerHandlerMixin {
    @Inject(
            method = "shouldActivate",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void activateForBattery(ItemStack held, Level world, BlockPos targetPos, Direction facing, CallbackInfoReturnable<Boolean> cir) {
        if (held.is(CCBlocks.KINETIC_BATTERY.asItem()) || held.is(CCItems.CHARGED_KINETIC_BATTERY.asItem()))
            if (world.getBlockState(targetPos).is(CCBlocks.KINETIC_BATTERY.get()))
                cir.setReturnValue(true);

    }
}
