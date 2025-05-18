package com.hlysine.create_connected;

import com.hlysine.create_connected.content.attributefilter.ItemDamageAttribute;
import com.hlysine.create_connected.content.attributefilter.ItemIdAttribute;
import com.hlysine.create_connected.content.attributefilter.ItemStackCountAttribute;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import net.minecraft.core.Registry;

public class CCItemAttributes {

    public static final ItemAttributeType MAX_DAMAGE = register("max_damage", new ItemDamageAttribute.Type());
    public static final ItemAttributeType ID_CONTAINS = register("id_contains", new ItemIdAttribute.Type());
    public static final ItemAttributeType STACK_SIZE = register("stack_size", new ItemStackCountAttribute.Type());

    public static void register() {
    }

    private static ItemAttributeType register(String id, ItemAttributeType type) {
        return Registry.register(CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE, CreateConnected.asResource(id), type);
    }
}
