package com.hlysine.create_connected.content.attributefilter;

import com.simibubi.create.content.logistics.filter.ItemAttribute;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemIdAttribute implements ItemAttribute {

    public static void register() {
        ItemAttribute.register(new ItemIdAttribute("dummy"));
    }

    String word;

    public ItemIdAttribute(String word) {
        this.word = word;
    }

    @Override
    public boolean appliesTo(ItemStack itemStack) {
        return itemStack.getItem().toString().contains(word);
    }

    @Override
    public List<ItemAttribute> listAttributesOf(ItemStack itemStack) {
        String[] words = itemStack.getItem().toString().split("_");

        List<ItemAttribute> atts = new ArrayList<>();
        for (String word : words) {
            if (word.length() > 2) {
                atts.add(new ItemIdAttribute(word));
            }
        }
        return atts;
    }

    @Override
    public String getTranslationKey() {
        return "id_contains";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{word};
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        nbt.putString("word", this.word);
    }

    @Override
    public ItemAttribute readNBT(CompoundTag nbt) {
        return new ItemIdAttribute(nbt.getString("word"));
    }
}
