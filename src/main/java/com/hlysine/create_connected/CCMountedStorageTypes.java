package com.hlysine.create_connected;

import com.hlysine.create_connected.content.itemsilo.ItemSiloMountedStorageType;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.content.fluids.tank.storage.FluidTankMountedStorageType;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;

import java.util.function.Supplier;

public class CCMountedStorageTypes {
    private static final CreateRegistrate REGISTRATE = CreateConnected.getRegistrate();

    public static final RegistryEntry<MountedItemStorageType<?>, ItemSiloMountedStorageType> SILO = simpleItem("silo", ItemSiloMountedStorageType::new);
    public static final RegistryEntry<MountedFluidStorageType<?>, FluidTankMountedStorageType> FLUID_VESSEL = simpleFluid("fluid_vessel", FluidTankMountedStorageType::new);

    private static <T extends MountedItemStorageType<?>> RegistryEntry<MountedItemStorageType<?>, T> simpleItem(String name, Supplier<T> supplier) {
        return REGISTRATE.mountedItemStorage(name, supplier).register();
    }

    private static <T extends MountedFluidStorageType<?>> RegistryEntry<MountedFluidStorageType<?>, T> simpleFluid(String name, Supplier<T> supplier) {
        return REGISTRATE.mountedFluidStorage(name, supplier).register();
    }

    public static void register() {
    }
}