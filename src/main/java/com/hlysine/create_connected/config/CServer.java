package com.hlysine.create_connected.config;

import com.simibubi.create.foundation.config.ConfigBase;

import java.util.function.Supplier;

public class CServer extends ConfigBase {
    private final ConfigInt schematicsNestingDepth = i(5, 0, 20, "schematicsNestingDepth", Comments.schematicsNestingDepth);
    public static final Supplier<Integer> SchematicsNestingDepth = CCConfigs.safeGetter(() -> CCConfigs.server().schematicsNestingDepth.get(), 0);

    private final ConfigBool applicationRemainingItemFix = b(true, "applicationRemainingItemFix", Comments.applicationRemainingItemFix);
    public static final Supplier<Boolean> ApplicationRemainingItemFix = CCConfigs.safeGetter(() -> CCConfigs.server().applicationRemainingItemFix.get(), true);

    public final CStress stressValues = nested(0, CStress::new, Comments.stress);
    public final ConfigFloat brakeActiveStress = f(16384, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, "brakeActiveStress", Comments.brakeActiveStress);


    @Override
    public String getName() {
        return "server";
    }

    private static class Comments {
        static String stress = "Fine tune the kinetic stats of individual components";
        static String brakeActiveStress = "Stress impact of a powered brake [in Stress Units]";
        static String schematicsNestingDepth = "Number of sub-folders allowed for schematics";
        static String applicationRemainingItemFix = "Enable the fix that gives remaining items after manual application";
    }
}
