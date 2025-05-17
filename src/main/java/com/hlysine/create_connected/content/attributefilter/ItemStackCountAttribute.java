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

public record ItemStackCountAttribute(int stackSize) implements ItemAttribute {
    public static final MapCodec<ItemStackCountAttribute> CODEC = Codec.INT
            .xmap(ItemStackCountAttribute::new, ItemStackCountAttribute::stackSize)
            .fieldOf("value");

    public static final StreamCodec<ByteBuf, ItemStackCountAttribute> STREAM_CODEC = ByteBufCodecs.INT
            .map(ItemStackCountAttribute::new, ItemStackCountAttribute::stackSize);


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
        return new Object[]{String.valueOf(stackSize)};
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
