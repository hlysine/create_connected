package com.hlysine.create_connected.content.kineticbattery;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.registries.CCDataComponents;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;

public class KineticBatteryOverrides {

    public static final ResourceLocation ID = CreateConnected.asResource("kinetic_battery_level");

    @OnlyIn(Dist.CLIENT)
    public static void registerModelOverridesClient(KineticBatteryBlockItem item) {
        ItemProperties.register(item, ID, (pStack, pLevel, pEntity, pSeed) -> {
            double level = pStack.getOrDefault(CCDataComponents.KINETIC_BATTERY_CHARGE, 0.0);
            return KineticBatteryBlockEntity.getCrudeBatteryLevel(level, 5);
        });
    }

    public static ItemModelBuilder addOverrideModels(DataGenContext<Item, KineticBatteryBlockItem> c,
                                                     RegistrateItemModelProvider p) {
        ItemModelBuilder builder = p.getBuilder(c.getName());
        for (int i = 0; i <= 5; i++) {
            builder.override()
                    .predicate(ID, i)
                    .model(p.withExistingParent("kinetic_battery_level_" + i, CreateConnected.asResource("block/kinetic_battery/item"))
                            .texture("level", CreateConnected.asResource("block/kinetic_battery/level_" + i + "_discharge")))
                    .end();
        }
        return builder;
    }
}

