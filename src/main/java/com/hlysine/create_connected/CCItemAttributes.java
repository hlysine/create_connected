package com.hlysine.create_connected;

import com.hlysine.create_connected.content.attributefilter.ItemDamageAttribute;
import com.hlysine.create_connected.content.attributefilter.ItemIdAttribute;
import com.hlysine.create_connected.content.attributefilter.ItemStackCountAttribute;

public class CCItemAttributes {
    public static void register() {
        ItemIdAttribute.register();
        ItemStackCountAttribute.register();
        ItemDamageAttribute.register();
    }
}
