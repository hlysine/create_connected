package com.hlysine.create_connected.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class CServer extends ConfigBase {
    public final CStress stressValues = nested(0, CStress::new, Comments.stress);
    public final ConfigFloat brakeActiveStress = f(16384, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, "brakeActiveStress", Comments.brakeActiveStress);

    @Override
    public String getName() {
        return "server";
    }

    private static class Comments {
        static String stress = "Fine tune the kinetic stats of individual components";
        static String brakeActiveStress = "Stress impact of a powered brake [in Stress Units]";
    }
}
