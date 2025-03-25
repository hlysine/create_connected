package com.hlysine.create_connected.content.itemsilo;

import com.hlysine.create_connected.CCMountedStorageTypes;
import com.mojang.serialization.Codec;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.contraption.storage.item.WrapperMountedItemStorage;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.foundation.utility.CreateCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class ItemSiloMountedStorage extends WrapperMountedItemStorage<ItemStackHandler> {
    public static final Codec<ItemSiloMountedStorage> CODEC = CreateCodecs.ITEM_STACK_HANDLER.xmap(
            ItemSiloMountedStorage::new, storage -> storage.wrapped
    );

    protected ItemSiloMountedStorage(MountedItemStorageType<?> type, ItemStackHandler handler) {
        super(type, handler);
    }

    protected ItemSiloMountedStorage(ItemStackHandler handler) {
        this(CCMountedStorageTypes.SILO.get(), handler);
    }

    @Override
    public void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        if (be instanceof ItemSiloBlockEntity vault) {
            vault.applyInventoryToBlock(this.wrapped);
        }
    }

    @Override
    public boolean handleInteraction(ServerPlayer player, Contraption contraption, StructureTemplate.StructureBlockInfo info) {
        // vaults should never be opened.
        return false;
    }

    public static ItemSiloMountedStorage fromVault(ItemSiloBlockEntity vault) {
        // Vault inventories have a world-affecting onContentsChanged, copy to a safe one
        return new ItemSiloMountedStorage(copyToItemStackHandler(vault.getInventoryOfBlock()));
    }

    public static ItemSiloMountedStorage fromLegacy(CompoundTag nbt) {
        ItemStackHandler handler = new ItemStackHandler();
        handler.deserializeNBT(nbt);
        return new ItemSiloMountedStorage(handler);
    }
}
