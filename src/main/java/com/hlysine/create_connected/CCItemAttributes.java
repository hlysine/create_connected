package com.hlysine.create_connected;

import com.hlysine.create_connected.content.attributefilter.ItemDamageAttribute;
import com.hlysine.create_connected.content.attributefilter.ItemIdAttribute;
import com.hlysine.create_connected.content.attributefilter.ItemStackCountAttribute;

public class CCItemAttributes {
    // The register method may be called multiple times due to how it is being patched into ItemAttribute
    private static boolean registered = false;

    public static void register() {
        if (registered) return;
        ItemIdAttribute.register();
        ItemStackCountAttribute.register();
        ItemDamageAttribute.register();
        registered = true;
    }
}
