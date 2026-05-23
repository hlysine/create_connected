package com.hlysine.create_connected;

import com.hlysine.create_connected.content.dashboard.DashboardDisplayTarget;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;

import java.util.function.Supplier;

public class CCDisplayTargets {
    private static final CreateRegistrate REGISTRATE = CreateConnected.getRegistrate();

    public static final RegistryEntry<DisplayTarget, DashboardDisplayTarget> DASHBOARD = simple("dashboard", DashboardDisplayTarget::new);

    private static <T extends DisplayTarget> RegistryEntry<DisplayTarget, T> simple(String name, Supplier<T> supplier) {
        return REGISTRATE.displayTarget(name, supplier).register();
    }

    public static void register() {
    }
}
