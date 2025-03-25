package com.hlysine.create_connected;

import com.hlysine.create_connected.content.attributefilter.ItemDamageAttribute;
import com.hlysine.create_connected.content.attributefilter.ItemIdAttribute;
import com.hlysine.create_connected.content.attributefilter.ItemStackCountAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Function;
import java.util.function.Supplier;

public class CCItemAttributes {
    // The register method may be called multiple times due to how it is being patched into ItemAttribute
    private static boolean registered = false;

    public static void register() {
        if (registered) return;

        ItemAttribute.LegacyDeserializer.ALL.add(new ItemIdAttribute.Deserializer());
        ItemAttribute.LegacyDeserializer.ALL.add(new ItemStackCountAttribute.Deserializer());
        ItemAttribute.LegacyDeserializer.ALL.add(new ItemDamageAttribute.Deserializer());
        registered = true;
    }
}
