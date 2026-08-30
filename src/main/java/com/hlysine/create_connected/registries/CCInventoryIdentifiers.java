package com.hlysine.create_connected.registries;

import com.hlysine.create_connected.content.inventoryaccessport.InventoryAccessPortBlockEntity;
import com.hlysine.create_connected.content.inventorybridge.InventoryBridgeBlockEntity;
import com.simibubi.create.api.packager.InventoryIdentifier;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CCInventoryIdentifiers {
    public static void register() {
        InventoryIdentifier.REGISTRY.register(CCBlocks.INVENTORY_ACCESS_PORT.get(), (Level level, BlockState state, BlockFace face) -> {
            BlockEntity be = level.getBlockEntity(face.getPos());
            if (be instanceof InventoryAccessPortBlockEntity inventoryAccessPort) {
                return inventoryAccessPort.getInventoryId();
            }
            return null;
        });
        InventoryIdentifier.REGISTRY.register(CCBlocks.INVENTORY_BRIDGE.get(), (Level level, BlockState state, BlockFace face) -> {
            BlockEntity be = level.getBlockEntity(face.getPos());
            if (be instanceof InventoryBridgeBlockEntity inventoryBridge) {
                return inventoryBridge.getInventoryId();
            }
            return null;
        });
    }
}
