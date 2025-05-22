package com.hlysine.create_connected;

import com.hlysine.create_connected.content.kineticbattery.KineticBatteryInteractionPoint;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CCArmInteractionPointTypes {
    private static final DeferredRegister<ArmInteractionPointType> ARM_INTERACTION_POINT_TYPES = DeferredRegister.create(CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE, CreateConnected.MODID);

    public static DeferredHolder<ArmInteractionPointType, ? extends ArmInteractionPointType> KINETIC_BATTERY =
            register("kinetic_battery", new KineticBatteryInteractionPoint.Type());

    private static <T extends ArmInteractionPointType> DeferredHolder<ArmInteractionPointType, T> register(String key, T type) {
        return ARM_INTERACTION_POINT_TYPES.register(key, () -> type);
    }

    public static void register(IEventBus modBus) {
        ARM_INTERACTION_POINT_TYPES.register(modBus);
    }
}
