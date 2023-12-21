package com.hlysine.create_connected;

import com.hlysine.create_connected.config.CCConfigs;
import com.hlysine.create_connected.config.FeatureToggle;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CCCreativeTabs {
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "create_connected" namespace
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateConnected.MODID);
    public static final RegistryObject<CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.create_connected.main"))
            .withTabsBefore(AllCreativeModeTabs.PALETTES_CREATIVE_TAB.getKey())
            .icon(CCBlocks.PARALLEL_GEARBOX::asStack)
            .displayItems(new DisplayItemsGenerator(List.of(
                    CCBlocks.ENCASED_CHAIN_COGWHEEL.asStack(),
                    CCBlocks.INVERTED_CLUTCH.asStack(),
                    CCBlocks.INVERTED_GEARSHIFT.asStack(),
                    CCBlocks.PARALLEL_GEARBOX.asStack(),
                    CCItems.VERTICAL_PARALLEL_GEARBOX.asStack(),
                    CCBlocks.SIX_WAY_GEARBOX.asStack(),
                    CCItems.VERTICAL_SIX_WAY_GEARBOX.asStack(),
                    CCBlocks.BRASS_GEARBOX.asStack(),
                    CCItems.VERTICAL_BRASS_GEARBOX.asStack(),
                    CCBlocks.SHEAR_PIN.asStack(),
                    CCBlocks.OVERSTRESS_CLUTCH.asStack(),
                    CCBlocks.CENTRIFUGAL_CLUTCH.asStack(),
                    CCBlocks.FREEWHEEL_CLUTCH.asStack(),
                    CCBlocks.BRAKE.asStack(),
                    CCBlocks.EMPTY_FAN_CATALYST.asStack(),
                    CCBlocks.FAN_BLASTING_CATALYST.asStack(),
                    CCBlocks.FAN_SMOKING_CATALYST.asStack(),
                    CCBlocks.FAN_SPLASHING_CATALYST.asStack(),
                    CCBlocks.FAN_HAUNTING_CATALYST.asStack(),
                    CCBlocks.COPYCAT_BLOCK.asStack(),
                    CCBlocks.COPYCAT_SLAB.asStack(),
                    CCBlocks.COPYCAT_BEAM.asStack(),
                    CCBlocks.COPYCAT_VERTICAL_STEP.asStack()
            )))
            .build());

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }

    private record DisplayItemsGenerator(List<ItemStack> items) implements CreativeModeTab.DisplayItemsGenerator {
        @Override
        public void accept(@NotNull CreativeModeTab.ItemDisplayParameters params, @NotNull CreativeModeTab.Output output) {
            for (ItemStack item : items) {
                if (FeatureToggle.isEnabled(RegisteredObjects.getKeyOrThrow(item.getItem()))) {
                    output.accept(item);
                }
            }
        }
    }
}
