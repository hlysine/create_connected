package com.hlysine.create_connected.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class CServer extends ConfigBase {

    public final ConfigInt exampleConfig = i(10, 0, 200, "exampleConfig", Comments.exampleConfig);

    @Override
    public String getName() {
        return "server";
    }

    private static class Comments {
        static String exampleConfig = "Placeholder config.";
    }
}
