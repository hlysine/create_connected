package com.hlysine.create_connected.registries;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.content.dashboard.DashboardDisplayTarget;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;

import java.util.function.Supplier;

public class CCDisplayTargets {
    private static final CreateRegistrate REGISTRATE = CreateConnected.getRegistrate();

    public static final RegistryEntry<DashboardDisplayTarget> DASHBOARD = simple("dashboard", DashboardDisplayTarget::new);

    private static <T extends DisplayTarget> RegistryEntry<T> simple(String name, Supplier<T> supplier) {
        return REGISTRATE.displayTarget(name, supplier).register();
    }

    public static void register() {
    }
}
