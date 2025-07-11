package com.hlysine.create_connected.config;

import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CServer extends ConfigBase {
    private final ConfigInt schematicsNestingDepth = i(5, 0, 20, "schematicsNestingDepth", Comments.schematicsNestingDepth);
    public static final Supplier<Integer> SchematicsNestingDepth = CCConfigs.safeGetter(() -> CCConfigs.server().schematicsNestingDepth.get(), 0);

    private final ConfigBool applicationRemainingItemFix = b(true, "applicationRemainingItemFix", Comments.applicationRemainingItemFix);
    public static final Supplier<Boolean> ApplicationRemainingItemFix = CCConfigs.safeGetter(() -> CCConfigs.server().applicationRemainingItemFix.get(), true);

    private final ConfigFloat vesselBoilerStressMultiplier = f(0.8125f, 0.0f, "vesselBoilerStressMultiplier", Comments.vesselBoilerStressMultiplier);
    public static final Supplier<Double> VesselBoilerStressMultiplier = CCConfigs.safeGetter(() -> CCConfigs.server().vesselBoilerStressMultiplier.get(), 0.8125);

    private final ConfigFloat vesselHeatMultiplier = f(0.5f, 0.0f, "vesselHeatMultiplier", Comments.vesselHeatMultiplier);
    public static final Supplier<Double> VesselHeatMultiplier = CCConfigs.safeGetter(() -> CCConfigs.server().vesselHeatMultiplier.get(), 0.5);

    private final ConfigInt vesselMaxLevel = i(18, 0, 18, "vesselMaxLevel", Comments.vesselMaxLevel);
    public static final Supplier<Integer> VesselMaxLevel = CCConfigs.safeGetter(() -> CCConfigs.server().vesselMaxLevel.get(), 0);

    private final ConfigInt batteryDischargeRPM = i(64, 0, 256, "batteryDischargeRPM", Comments.batteryDischargeRPM);
    public static final Supplier<Integer> BatteryDischargeRPM = CCConfigs.safeGetter(() -> CCConfigs.server().batteryDischargeRPM.get(), 64);

    private final ConfigFloat batteryCapacity = f(512, 0, 8192, "batteryCapacity", Comments.batteryCapacity);
    public static final Supplier<Double> BatteryCapacity = CCConfigs.safeGetter(() -> CCConfigs.server().batteryCapacity.get(), 512.0);

    private final ConfigBool allowDualWildcardLink = b(false, "allowDualWildcardLink", Comments.allowDualWildcardLink);
    public static final Supplier<Boolean> AllowDualWildcardLink = CCConfigs.safeGetter(() -> CCConfigs.server().allowDualWildcardLink.get(), false);

    public final CStress stressValues = nested(0, CStress::new, Comments.stress);
    public final ConfigFloat brakeActiveStress = f(16384, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, "brakeActiveStress", Comments.brakeActiveStress);


    @Override
    public @NotNull String getName() {
        return "server";
    }

    private static class Comments {
        static String stress = "Fine tune the kinetic stats of individual components";
        static String brakeActiveStress = "Stress impact of a powered brake [in Stress Units]";
        static String schematicsNestingDepth = "Number of sub-folders allowed for schematics";
        static String applicationRemainingItemFix = "Enable the fix that gives remaining items after manual application";
        static String vesselBoilerStressMultiplier = "Modify the stress output when a Fluid Vessel is used as a boiler";
        static String vesselHeatMultiplier = "Modify the heat level when a Fluid Vessel is used as a boiler";
        static String vesselMaxLevel = "Limit the max boiler level achievable with a Fluid Vessel";
        static String batteryDischargeRPM = "RPM of a Kinetic Battery when discharging";
        static String batteryCapacity = "Max capacity of a Kinetic Battery in su-hours";
        static String allowDualWildcardLink = "Allow Redstone Links to have wildcards in both slots [restart required]";
    }
}
