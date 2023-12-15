package com.hlysine.create_connected;

import com.simibubi.create.AllCreativeModeTabs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CCCreativeTabs {
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "create_connected" namespace
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateConnected.MODID);
    public static final RegistryObject<CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.create_connected.main"))
            .withTabsBefore(AllCreativeModeTabs.PALETTES_CREATIVE_TAB.getKey())
            .icon(CCBlocks.PARALLEL_GEARBOX::asStack)
            .displayItems((params, output) -> {
                output.accept(CCBlocks.INVERTED_CLUTCH.asStack());
                output.accept(CCBlocks.INVERTED_GEARSHIFT.asStack());
                output.accept(CCBlocks.PARALLEL_GEARBOX.asStack());
                output.accept(CCItems.VERTICAL_PARALLEL_GEARBOX.asStack());
                output.accept(CCBlocks.BRASS_GEARBOX.asStack());
                output.accept(CCItems.VERTICAL_BRASS_GEARBOX.asStack());
                output.accept(CCBlocks.SHEAR_PIN.asStack());
                output.accept(CCBlocks.OVERSTRESS_CLUTCH.asStack());
                output.accept(CCBlocks.CENTRIFUGAL_CLUTCH.asStack());
                output.accept(CCBlocks.BRAKE.asStack());
                output.accept(CCBlocks.EMPTY_FAN_CATALYST.asStack());
                output.accept(CCBlocks.FAN_BLASTING_CATALYST.asStack());
                output.accept(CCBlocks.FAN_SMOKING_CATALYST.asStack());
                output.accept(CCBlocks.FAN_SPLASHING_CATALYST.asStack());
                output.accept(CCBlocks.FAN_HAUNTING_CATALYST.asStack());
                output.accept(CCBlocks.COPYCAT_BLOCK.asStack());
                output.accept(CCBlocks.COPYCAT_SLAB.asStack());
                output.accept(CCBlocks.COPYCAT_BEAM.asStack());
            })
            .build());

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
