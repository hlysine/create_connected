package com.hlysine.create_connected.content.attributefilter;

import com.simibubi.create.Create;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ItemIdAttribute implements ItemAttribute {
    private static final String TRANSLATION_KEY = "id_contains";
    private static final String WORD_KEY = "word";
    private static final String DEFAULT_WORD = "dummy";
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
        return Registry.register(CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE, Create.asResource(getTranslationKey()), new ItemIdAttribute.Type());
    }


    @Override
    public String getTranslationKey() {
        return TRANSLATION_KEY;
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{word};
    }

    @Override
    public void save(CompoundTag nbt) {
        nbt.putString(WORD_KEY, this.word);
    }

    @Override
    public void load(CompoundTag nbt) {
        word = nbt.getString(WORD_KEY);
    }

    public static class Type implements ItemAttributeType {
        @Override
        public @NotNull ItemAttribute createAttribute() {
            return new ItemIdAttribute(DEFAULT_WORD);
        }

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack itemStack, Level level) {
            if (itemStack == null) {
                return Collections.emptyList();
            }

            String itemName = itemStack.getItem().toString();
            if (itemName.isEmpty()) {
                return Collections.emptyList();
            }

            return Arrays.stream(itemName.split("_"))
                    .filter(word -> word.length() > 2)
                    .map(ItemIdAttribute::new)
                    .collect(Collectors.toList());
        }
    }


    public static class Deserializer implements ItemAttribute.LegacyDeserializer {
        private static final ItemIdAttribute.Type ITEM_ID_ATTRIBUTE_TYPE = new ItemIdAttribute.Type();

        private static final Function<CompoundTag, ItemAttribute> FUNC = tag -> {
            ItemAttribute attribute = ITEM_ID_ATTRIBUTE_TYPE.createAttribute();
            attribute.load(tag);
            return attribute;
        };

        @Override
        public String getNBTKey() {
            return WORD_KEY;
        }

        @Override
        public ItemAttribute readNBT(CompoundTag nbt) {
            return FUNC.apply(nbt);
        }
    }
}
