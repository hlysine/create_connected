package com.hlysine.create_connected.registries;

import com.hlysine.create_connected.content.inventoryaccessport.InventoryAccessPortUnpackingHandler;
import com.hlysine.create_connected.content.inventorybridge.InventoryBridgeUnpackingHandler;
import com.simibubi.create.api.packager.unpacking.UnpackingHandler;

@SuppressWarnings("UnstableApiUsage")
public class CCUnpackingHandlers {
    public static void register() {
        UnpackingHandler.REGISTRY.register(CCBlocks.INVENTORY_ACCESS_PORT.get(), InventoryAccessPortUnpackingHandler.INSTANCE);
        UnpackingHandler.REGISTRY.register(CCBlocks.INVENTORY_BRIDGE.get(), InventoryBridgeUnpackingHandler.INSTANCE);
    }
}
