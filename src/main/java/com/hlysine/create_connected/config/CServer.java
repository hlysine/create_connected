package com.hlysine.create_connected.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class CServer extends ConfigBase {
    public final CStress stressValues = nested(0, CStress::new, Comments.stress);
    public final ConfigFloat brakeActiveStress = f(16384, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, "brakeActiveStress", Comments.brakeActiveStress);

    private final ConfigInt schematicsNestingDepth = i(5, 0, 20, "schematicsNestingDepth", Comments.schematicsNestingDepth);

    public int getSchematicsNestingDepth() {
        try {
            return schematicsNestingDepth.get();
        } catch (IllegalStateException $) {
            return 0;
        }
    }

    @Override
    public String getName() {
        return "server";
    }

    private static class Comments {
        static String stress = "Fine tune the kinetic stats of individual components";
        static String brakeActiveStress = "Stress impact of a powered brake [in Stress Units]";
        static String schematicsNestingDepth = "Number of sub-folders allowed for schematics. Value on server overrides clients.";
    }
}
