package com.hlysine.create_connected.mixin.itemsilo;

import com.hlysine.create_connected.content.itemsilo.ItemSiloBlockEntity;
import com.simibubi.create.content.contraptions.MountedStorage;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MountedStorage.class, remap = false)
public class MountedStorageMixin {
    @Shadow
    boolean noFuel;

    @Shadow
    private BlockEntity blockEntity;

    @Shadow
    ItemStackHandler handler;

    @Shadow
    boolean valid;

    @Inject(
            at = @At("HEAD"),
            method = "canUseAsStorage(Lnet/minecraft/world/level/block/entity/BlockEntity;)Z",
            cancellable = true
    )
    private static void itemSiloAsStorage(BlockEntity be, CallbackInfoReturnable<Boolean> cir) {
        if (be instanceof ItemSiloBlockEntity) cir.setReturnValue(true);
    }

    @Inject(
            at = @At("TAIL"),
            method = "<init>(Lnet/minecraft/world/level/block/entity/BlockEntity;)V"
    )
    private void ctor(BlockEntity be, CallbackInfo ci) {
        noFuel = noFuel || be instanceof ItemSiloBlockEntity;
    }

    @Inject(
            at = @At("HEAD"),
            method = "removeStorageFromWorld()V",
            cancellable = true
    )
    private void removeItemSilo(CallbackInfo ci) {
        if (blockEntity instanceof ItemSiloBlockEntity) {
            handler = ((ItemSiloBlockEntity) blockEntity).getInventoryOfBlock();
            valid = true;
            ci.cancel();
        }
    }

    @Inject(
            at = @At("HEAD"),
            method = "addStorageToWorld(Lnet/minecraft/world/level/block/entity/BlockEntity;)V",
            cancellable = true
    )
    private void addItemSilo(BlockEntity be, CallbackInfo ci) {
        if (be instanceof ItemSiloBlockEntity) {
            ((ItemSiloBlockEntity) be).applyInventoryToBlock(handler);
            ci.cancel();
        }
    }
}
