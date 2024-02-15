package com.hlysine.create_connected.mixin.compat;

import com.copycatsplus.copycats.content.copycat.board.CopycatBoardBlock;
import com.copycatsplus.copycats.content.copycat.slab.CopycatSlabBlock;
import com.hlysine.create_connected.compat.CopycatsManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({CopycatSlabBlock.class, CopycatBoardBlock.class})
public class CopycatBlockMixin {
    @Redirect(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"),
            method = "canBeReplaced(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/item/context/BlockPlaceContext;)Z",
            require = 0
    )
    private boolean convertItem(ItemStack instance, Item pItem) {
        if (CopycatsManager.convertIfEnabled(instance.getItem()).equals(pItem))
            return true;
        return instance.is(pItem);
    }
}
