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

public class ItemDamageAttribute implements ItemAttribute {

    int maxDamage;

    public ItemDamageAttribute(int maxDamage) {
        this.maxDamage = maxDamage;
    }

    @Override
    public boolean appliesTo(ItemStack stack, Level world) {
        return stack.getMaxDamage() == maxDamage;
    }

    @Override
    public ItemAttributeType getType() {
        return CCItemAttributes.MAX_DAMAGE;
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
    public void save(CompoundTag nbt) {
        nbt.putInt("maxDamage", this.maxDamage);
    }

    @Override
    public void load(CompoundTag nbt) {
        if (nbt.contains("maxDamage"))
            this.maxDamage = nbt.getInt("maxDamage");
    }

    public static class Type implements ItemAttributeType {
        @Override
        public @NotNull ItemAttribute createAttribute() {
            return new ItemDamageAttribute(0);
        }

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
            List<ItemAttribute> attributes = new ArrayList<>();
            attributes.add(new ItemDamageAttribute(stack.getMaxDamage()));
            return attributes;
        }
    }

    @SuppressWarnings("deprecation")
    public static class LegacyDeserializer implements ItemAttribute.LegacyDeserializer {

        @Override
        public String getNBTKey() {
            return "maxDamage";
        }

        @Override
        public ItemAttribute readNBT(CompoundTag nbt) {
            ItemAttribute attribute = new ItemDamageAttribute(0);
            attribute.load(nbt);
            return attribute;
        }
    }
}
