package com.hlysine.create_connected.content.attributefilter;

import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemIdAttribute implements ItemAttribute {

    String word;

    public ItemIdAttribute(String word) {
        this.word = word;
    }

    @Override
    public boolean appliesTo(ItemStack stack, Level world) {
        return stack.getItem().toString().contains(word);
    }

    @Override
    public ItemAttributeType getType() {
        return null;
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
    public void save(CompoundTag nbt) {
        nbt.putString("word", this.word);
    }

    @Override
    public void load(CompoundTag nbt) {
        this.word = nbt.getString("word");
    }

    public static class Type implements ItemAttributeType {
        @Override
        public @NotNull ItemAttribute createAttribute() {
            return new ItemIdAttribute("dummy");
        }

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
            String[] words = stack.getItem().toString().split("_");

            List<ItemAttribute> attributes = new ArrayList<>();
            for (String word : words) {
                if (word.length() > 2) {
                    attributes.add(new ItemIdAttribute(word));
                }
            }
            return attributes;
        }
    }

    @SuppressWarnings("deprecation")
    public static class LegacyDeserializer implements ItemAttribute.LegacyDeserializer {

        @Override
        public String getNBTKey() {
            return "word";
        }

        @Override
        public ItemAttribute readNBT(CompoundTag nbt) {
            ItemAttribute attribute = new ItemIdAttribute("dummy");
            attribute.load(nbt);
            return attribute;
        }
    }
}
