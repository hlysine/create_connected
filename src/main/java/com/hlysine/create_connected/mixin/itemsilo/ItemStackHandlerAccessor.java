package com.hlysine.create_connected.mixin.itemsilo;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ItemStackHandler.class, remap = false)
public interface ItemStackHandlerAccessor {
    @Accessor
    NonNullList<ItemStack> getStacks();
}
