package com.hlysine.create_connected;

import com.hlysine.create_connected.content.itemsilo.ItemSiloMountedStorageType;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;

import java.util.function.Supplier;

public class CCMountedStorageTypes {
    private static final CreateRegistrate REGISTRATE = CreateConnected.getRegistrate();

    public static final RegistryEntry<ItemSiloMountedStorageType> SILO = simpleItem("silo", ItemSiloMountedStorageType::new);

    private static <T extends MountedItemStorageType<?>> RegistryEntry<T> simpleItem(String name, Supplier<T> supplier) {
        return REGISTRATE.mountedItemStorage(name, supplier).register();
    }

    public static void register() {
    }
}
