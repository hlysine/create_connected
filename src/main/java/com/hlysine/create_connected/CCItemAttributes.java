package com.hlysine.create_connected;

import com.hlysine.create_connected.content.attributefilter.ItemDamageAttribute;
import com.hlysine.create_connected.content.attributefilter.ItemIdAttribute;
import com.hlysine.create_connected.content.attributefilter.ItemStackCountAttribute;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import net.minecraft.core.Registry;

public class CCItemAttributes {

    public static void register() {
        LegacyDeserializers.register();

        register("max_damage", new ItemDamageAttribute.Type());
        register("id_contains", new ItemIdAttribute.Type());
        register("stack_size", new ItemStackCountAttribute.Type());
    }

    private static void register(String id, ItemAttributeType type) {
        Registry.register(CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE, CreateConnected.asResource(id), type);
    }

    @SuppressWarnings("deprecation")
    public static class LegacyDeserializers {
        public static void register() {
            addLegacyDeserializer(new ItemDamageAttribute.LegacyDeserializer());
            addLegacyDeserializer(new ItemIdAttribute.LegacyDeserializer());
            addLegacyDeserializer(new ItemStackCountAttribute.LegacyDeserializer());
        }

        private static void addLegacyDeserializer(ItemAttribute.LegacyDeserializer legacyDeserializer) {
            ItemAttribute.LegacyDeserializer.ALL.add(legacyDeserializer);
        }
    }
}
