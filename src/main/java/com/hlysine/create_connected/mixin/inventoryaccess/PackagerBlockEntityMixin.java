package com.hlysine.create_connected.mixin.inventoryaccess;

import com.hlysine.create_connected.content.inventoryaccessport.InventoryAccessPortBlockEntity;
import com.hlysine.create_connected.content.inventorybridge.InventoryBridgeBlockEntity;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PackagerBlockEntity.class, remap = false)
public class PackagerBlockEntityMixin {
    @Inject(
            method = "supportsBlockEntity",
            at = @At("HEAD"),
            cancellable = true
    )
    private void supportsInventoryAccess(BlockEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (target == null) return;
        if (target instanceof InventoryAccessPortBlockEntity accessPort) {
            BlockState attached = accessPort.getAttachedBlock();
            if (attached != null) {
                if (attached.is(AllBlocks.PORTABLE_STORAGE_INTERFACE.get())) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
        if (target instanceof InventoryBridgeBlockEntity accessPort) {
            BlockState attached = accessPort.getNegativeAttachedBlock();
            if (attached != null) {
                if (attached.is(AllBlocks.PORTABLE_STORAGE_INTERFACE.get())) {
                    cir.setReturnValue(false);
                    return;
                }
            }
            attached = accessPort.getPositiveAttachedBlock();
            if (attached != null) {
                if (attached.is(AllBlocks.PORTABLE_STORAGE_INTERFACE.get())) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }
}
