package com.hlysine.create_connected.content.itemsilo;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ItemSiloMountedStorageType extends MountedItemStorageType<ItemSiloMountedStorage> {
    public ItemSiloMountedStorageType() {
        super(ItemSiloMountedStorage.CODEC);
    }

    @Override
    @Nullable
    public ItemSiloMountedStorage mount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        return be instanceof ItemSiloBlockEntity vault ? ItemSiloMountedStorage.fromVault(vault) : null;
    }
}