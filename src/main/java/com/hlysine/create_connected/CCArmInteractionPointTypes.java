package com.hlysine.create_connected;

import com.hlysine.create_connected.content.kineticbattery.KineticBatteryInteractionPoint;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CCArmInteractionPointTypes {
    private static final DeferredRegister<ArmInteractionPointType> ARM_INTERACTION_POINT_TYPES = DeferredRegister.create(CreateRegistries.ARM_INTERACTION_POINT_TYPE, CreateConnected.MODID);

    public static RegistryObject<ArmInteractionPointType> KINETIC_BATTERY =
            register("kinetic_battery", new KineticBatteryInteractionPoint.Type());

    private static <T extends ArmInteractionPointType> RegistryObject<T> register(String key, T type) {
        return ARM_INTERACTION_POINT_TYPES.register(key, () -> type);
    }

    public static void register(IEventBus modBus) {
        ARM_INTERACTION_POINT_TYPES.register(modBus);
    }
}
