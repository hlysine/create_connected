package com.hlysine.create_connected.content.attributefilter;

import com.hlysine.create_connected.CCItemAttributes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record ItemDamageAttribute(int maxDamage) implements ItemAttribute {
    public static final MapCodec<ItemDamageAttribute> CODEC = Codec.INT
            .xmap(ItemDamageAttribute::new, ItemDamageAttribute::maxDamage)
            .fieldOf("value");

    public static final StreamCodec<ByteBuf, ItemDamageAttribute> STREAM_CODEC = ByteBufCodecs.INT
            .map(ItemDamageAttribute::new, ItemDamageAttribute::maxDamage);

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
        return new Object[]{String.valueOf(maxDamage)};
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

        @Override
        public MapCodec<? extends ItemAttribute> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ? extends ItemAttribute> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
