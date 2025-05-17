package com.hlysine.create_connected.content.attributefilter;

import com.hlysine.create_connected.CCItemAttributes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record ItemIdAttribute(String word) implements ItemAttribute {
    public static final MapCodec<ItemIdAttribute> CODEC = Codec.STRING
            .xmap(ItemIdAttribute::new, ItemIdAttribute::word)
            .fieldOf("value");

    public static final StreamCodec<ByteBuf, ItemIdAttribute> STREAM_CODEC = ByteBufCodecs.STRING_UTF8
            .map(ItemIdAttribute::new, ItemIdAttribute::word);

    @Override
    public boolean appliesTo(ItemStack stack, Level world) {
        return stack.getItem().toString().contains(word);
    }

    @Override
    public ItemAttributeType getType() {
        return CCItemAttributes.ID_CONTAINS;
    }

    @Override
    public String getTranslationKey() {
        return "id_contains";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{word};
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
