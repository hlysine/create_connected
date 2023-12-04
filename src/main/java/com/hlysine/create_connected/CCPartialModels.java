package com.hlysine.create_connected;

import com.jozufozu.flywheel.core.PartialModel;

public class CCPartialModels {
//    public static final PartialModel ANDESITE_CHUNK_LOADER_CORE_ACTIVE = block("andesite_chunk_loader/core_active");

    private static PartialModel block(String path) {
        return new PartialModel(CreateConnected.asResource("block/" + path));
    }

    public static void register() {

    }
}
