package com.hlysine.create_connected.mixin;

import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ManualApplicationRecipe.class, remap = false)
public class ManualApplicationRecipeMixin {
    @Inject(
            method = "manualApplicationRecipesApplyInWorld(Lnet/minecraftforge/event/entity/player/PlayerInteractEvent$RightClickBlock;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"),
            remap = true
    )
    private static void craftingRemainingItemOnApplication(PlayerInteractEvent.RightClickBlock event, CallbackInfo info) {
        ItemStack heldItem = event.getItemStack();
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player))
            return;
        InteractionHand hand = event.getHand();
        ItemStack leftover = heldItem.hasContainerItem() ? heldItem.getContainerItem() : ItemStack.EMPTY;

        heldItem.shrink(1);

        if (heldItem.isEmpty()) {
            player.setItemInHand(hand, leftover);
        } else {
            heldItem.grow(1); // Create shrinks the stack again after this inject
            if (!player.getInventory().add(leftover)) {
                player.drop(leftover, false);
            }
        }
    }
}
