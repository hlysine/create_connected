package com.hlysine.create_connected.content.attributefilter;

import com.simibubi.create.content.logistics.filter.ItemAttribute;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemStackCountAttribute implements ItemAttribute {

    public static void register() {
        ItemAttribute.register(new ItemStackCountAttribute(ItemStack.EMPTY.getMaxStackSize()));
    }

    int stackSize;

    public ItemStackCountAttribute(int stackSize) {
        this.stackSize = stackSize;
    }

    @Override
    public boolean appliesTo(ItemStack itemStack) {
        return itemStack.getMaxStackSize() == stackSize;
    }

    @Override
    public List<ItemAttribute> listAttributesOf(ItemStack itemStack) {
        List<ItemAttribute> atts = new ArrayList<>();
        atts.add(new ItemStackCountAttribute(itemStack.getMaxStackSize()));
        return atts;
    }

    @Override
    public String getTranslationKey() {
        return "stack_size";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{stackSize};
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        nbt.putInt("stackSize", this.stackSize);
    }

    @Override
    public ItemAttribute readNBT(CompoundTag nbt) {
        return new ItemStackCountAttribute(nbt.getInt("stackSize"));
    }
}
