package com.hlysine.create_connected.content.attributefilter;

import com.simibubi.create.content.logistics.filter.ItemAttribute;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemDamageAttribute implements ItemAttribute {

    public static void register() {
        ItemAttribute.register(new ItemDamageAttribute(0));
    }

    int maxDamage;

    public ItemDamageAttribute(int maxDamage) {
        this.maxDamage = maxDamage;
    }

    @Override
    public boolean appliesTo(ItemStack itemStack) {
        return itemStack.getMaxDamage() == maxDamage;
    }

    @Override
    public List<ItemAttribute> listAttributesOf(ItemStack itemStack) {
        List<ItemAttribute> atts = new ArrayList<>();
        atts.add(new ItemDamageAttribute(itemStack.getMaxDamage()));
        return atts;
    }

    @Override
    public String getTranslationKey() {
        return "max_damage";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{maxDamage};
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        nbt.putInt("maxDamage", this.maxDamage);
    }

    @Override
    public ItemAttribute readNBT(CompoundTag nbt) {
        return new ItemDamageAttribute(nbt.getInt("maxDamage"));
    }
}
