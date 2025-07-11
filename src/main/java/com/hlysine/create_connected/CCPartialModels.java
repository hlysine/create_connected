package com.hlysine.create_connected;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class CCPartialModels {
    public static final PartialModel CRANK_WHEEL_HANDLE = block("crank_wheel/handle");
    public static final PartialModel CRANK_WHEEL_BASE = block("crank_wheel/block");
    public static final PartialModel LARGE_CRANK_WHEEL_HANDLE = block("large_crank_wheel/handle");
    public static final PartialModel LARGE_CRANK_WHEEL_BASE = block("large_crank_wheel/block");
    public static final PartialModel SHEAR_PIN = block("shear_pin");
    public static final PartialModel KINETIC_BRIDGE_SOURCE = block("kinetic_bridge/source_coupler");
    public static final PartialModel KINETIC_BRIDGE_DESTINATION = block("kinetic_bridge/destination_coupler");

    private static PartialModel block(String path) {
        return PartialModel.of(CreateConnected.asResource("block/" + path));
    }

    public static void register() {

    }
}
