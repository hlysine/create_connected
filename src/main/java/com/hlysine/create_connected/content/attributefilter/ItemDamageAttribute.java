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

public class ItemDamageAttribute implements ItemAttribute {
    private static final String TRANSLATION_KEY = "max_damage";
    private static final String MAX_DAMAGE_KEY = "maxDamage";
    private static final int DEFAULT_MAX_DAMAGE = 0;

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
        return Registry.register(CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE, Create.asResource(getTranslationKey()), new ItemDamageAttribute.Type());
    }

    @Override
    public String getTranslationKey() {
        return TRANSLATION_KEY;
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{maxDamage};
    }

    @Override
    public void save(CompoundTag nbt) {
        nbt.putInt(MAX_DAMAGE_KEY, this.maxDamage);
    }


    @Override
    public void load(CompoundTag nbt) {
        maxDamage = nbt.getInt(MAX_DAMAGE_KEY);
    }


    public static class Type implements ItemAttributeType {
        @Override
        public @NotNull ItemAttribute createAttribute() {
            return new ItemDamageAttribute(DEFAULT_MAX_DAMAGE);
        }

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack itemStack, Level level) {
            return Collections.singletonList(new ItemDamageAttribute(itemStack.getMaxDamage()));
        }
    }

    public static class Deserializer implements ItemAttribute.LegacyDeserializer {
        private static final ItemDamageAttribute.Type ITEM_DAMAGE_ATTRIBUTE_TYPE = new ItemDamageAttribute.Type();

        private static final Function<CompoundTag, ItemAttribute> FUNC = tag -> {
            ItemAttribute attribute = ITEM_DAMAGE_ATTRIBUTE_TYPE.createAttribute();
            attribute.load(tag);
            return attribute;
        };

        @Override
        public String getNBTKey() {
            return MAX_DAMAGE_KEY;
        }

        @Override
        public ItemAttribute readNBT(CompoundTag nbt) {
            return FUNC.apply(nbt);
        }
    }
}
