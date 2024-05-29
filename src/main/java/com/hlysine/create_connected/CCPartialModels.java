package com.hlysine.create_connected;

import com.jozufozu.flywheel.core.PartialModel;

public class CCPartialModels {
    public static final PartialModel CRANK_WHEEL_HANDLE = block("crank_wheel/handle");
    public static final PartialModel LARGE_CRANK_WHEEL_HANDLE = block("large_crank_wheel/handle");

    private static PartialModel block(String path) {
        return new PartialModel(CreateConnected.asResource("block/" + path));
    }

    public static void register() {

    }
}
