package com.hlysine.create_connected.mixin.itemsilo;

import com.hlysine.create_connected.content.itemsilo.ItemSiloMountedStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MountedStorageManager.class, remap = false)
public abstract class MountedStorageManagerMixin {
    @Shadow
    protected abstract void addStorage(MountedItemStorage storage, BlockPos pos);

    @Inject(
            method = "readLegacy(Lnet/minecraft/nbt/CompoundTag;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/createmod/catnip/nbt/NBTHelper;iterateCompoundList(Lnet/minecraft/nbt/ListTag;Ljava/util/function/Consumer;)V",
                    ordinal = 0
            )
    )
    private void readLegacy(CompoundTag nbt, CallbackInfo ci) {
        NBTHelper.iterateCompoundList(nbt.getList("Storage", Tag.TAG_COMPOUND), tag -> {
            BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("Pos"));
            CompoundTag data = tag.getCompound("Data");

            if (data.contains("NoFuel")) {
                addStorage(ItemSiloMountedStorage.fromLegacy(data), pos);
            }
        });
    }
}