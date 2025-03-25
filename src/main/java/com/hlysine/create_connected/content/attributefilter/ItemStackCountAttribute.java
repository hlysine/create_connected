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

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class ItemStackCountAttribute implements ItemAttribute {
    private static final String TRANSLATION_KEY = "stack_size";
    private static final String STACK_SIZE_KEY = "stack_size";
    private static final int DEFAULT_STACK_SIZE = 64;

    int stackSize;

    public ItemStackCountAttribute(int stackSize) {
        this.stackSize = stackSize;
    }

    @Override
    public boolean appliesTo(ItemStack stack, Level world) {
        return stack.getMaxStackSize() == stackSize;
    }

    @Override
    public ItemAttributeType getType() {
        return Registry.register(CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE, Create.asResource(getTranslationKey()), new ItemStackCountAttribute.Type());
    }

    @Override
    public String getTranslationKey() {
        return TRANSLATION_KEY;
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{stackSize};
    }

    @Override
    public void save(CompoundTag nbt) {
        nbt.putInt(STACK_SIZE_KEY, this.stackSize);
    }

    @Override
    public void load(CompoundTag nbt) {
        stackSize = nbt.getInt(STACK_SIZE_KEY);
    }

    public static class Type implements ItemAttributeType {
        @Override
        public @NotNull ItemAttribute createAttribute() {
            return new ItemStackCountAttribute(DEFAULT_STACK_SIZE);
        }

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack itemStack, Level level) {
            return Collections.singletonList(new ItemStackCountAttribute(itemStack.getMaxStackSize()));
        }
    }

    public static class Deserializer implements ItemAttribute.LegacyDeserializer {
        private static final ItemStackCountAttribute.Type ITEM_STACK_COUNT_ATTRIBUTE_TYPE = new ItemStackCountAttribute.Type();

        private static final Function<CompoundTag, ItemAttribute> FUNC = tag -> {
            ItemAttribute attribute = ITEM_STACK_COUNT_ATTRIBUTE_TYPE.createAttribute();
            attribute.load(tag);
            return attribute;
        };

        @Override
        public String getNBTKey() {
            return STACK_SIZE_KEY;
        }

        @Override
        public ItemAttribute readNBT(CompoundTag nbt) {
            return FUNC.apply(nbt);
        }
    }
}
