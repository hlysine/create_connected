package com.hlysine.create_connected.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class CServer extends ConfigBase {
    public final CStress stressValues = nested(0, CStress::new, Comments.stress);
    @Override
    public String getName() {
        return "server";
    }

    private static class Comments {
        static String stress = "Fine tune the kinetic stats of individual components";
    }
}
