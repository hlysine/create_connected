package com.hlysine.create_connected.content.attributefilter;

import com.hlysine.create_connected.CCItemAttributes;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemStackCountAttribute implements ItemAttribute {

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
        return CCItemAttributes.STACK_SIZE;
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
    public void save(CompoundTag nbt) {
        nbt.putInt("stackSize", this.stackSize);
    }

    @Override
    public void load(CompoundTag nbt) {
        if (nbt.contains("stackSize"))
            this.stackSize = nbt.getInt("stackSize");
    }

    public static class Type implements ItemAttributeType {
        @Override
        public @NotNull ItemAttribute createAttribute() {
            return new ItemStackCountAttribute(64);
        }

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
            List<ItemAttribute> attributes = new ArrayList<>();
            attributes.add(new ItemStackCountAttribute(stack.getMaxStackSize()));
            return attributes;
        }
    }

    @SuppressWarnings("deprecation")
    public static class LegacyDeserializer implements ItemAttribute.LegacyDeserializer {

        @Override
        public String getNBTKey() {
            return "stackSize";
        }

        @Override
        public ItemAttribute readNBT(CompoundTag nbt) {
            ItemAttribute attribute = new ItemStackCountAttribute(0);
            attribute.load(nbt);
            return attribute;
        }
    }
}
