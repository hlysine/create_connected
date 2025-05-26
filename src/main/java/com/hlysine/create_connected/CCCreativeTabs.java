package com.hlysine.create_connected;

import com.hlysine.create_connected.config.FeatureToggle;
import com.simibubi.create.AllCreativeModeTabs;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class CCCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateConnected.MODID);

    public static final List<ItemProviderEntry<?, ?>> ITEMS = List.of(
            CCBlocks.ENCASED_CHAIN_COGWHEEL,
            CCBlocks.CRANK_WHEEL,
            CCBlocks.LARGE_CRANK_WHEEL,
            CCBlocks.INVERTED_CLUTCH,
            CCBlocks.INVERTED_GEARSHIFT,
            CCBlocks.PARALLEL_GEARBOX,
            CCItems.VERTICAL_PARALLEL_GEARBOX,
            CCBlocks.SIX_WAY_GEARBOX,
            CCItems.VERTICAL_SIX_WAY_GEARBOX,
            CCBlocks.BRASS_GEARBOX,
            CCItems.VERTICAL_BRASS_GEARBOX,
            CCBlocks.CROSS_CONNECTOR,
            CCBlocks.SHEAR_PIN,
            CCBlocks.OVERSTRESS_CLUTCH,
            CCBlocks.CENTRIFUGAL_CLUTCH,
            CCBlocks.FREEWHEEL_CLUTCH,
            CCBlocks.BRAKE,
            CCBlocks.KINETIC_BRIDGE,
            CCBlocks.KINETIC_BATTERY,
            CCItems.CHARGED_KINETIC_BATTERY,
            CCBlocks.ITEM_SILO,
            CCBlocks.FLUID_VESSEL,
            CCBlocks.CREATIVE_FLUID_VESSEL,
            CCBlocks.INVENTORY_ACCESS_PORT,
            CCBlocks.INVENTORY_BRIDGE,
            CCBlocks.SEQUENCED_PULSE_GENERATOR,
            CCItems.LINKED_TRANSMITTER,
            CCItems.REDSTONE_LINK_WILDCARD,
            CCBlocks.EMPTY_FAN_CATALYST,
            CCBlocks.FAN_BLASTING_CATALYST,
            CCBlocks.FAN_SMOKING_CATALYST,
            CCBlocks.FAN_SPLASHING_CATALYST,
            CCBlocks.FAN_HAUNTING_CATALYST,
            CCBlocks.FAN_SEETHING_CATALYST,
            CCBlocks.FAN_FREEZING_CATALYST,
            CCBlocks.FAN_SANDING_CATALYST,
            CCBlocks.FAN_ENRICHED_CATALYST,
            CCBlocks.FAN_ENDING_CATALYST,
            CCBlocks.COPYCAT_BLOCK,
            CCBlocks.COPYCAT_SLAB,
            CCBlocks.COPYCAT_BEAM,
            CCBlocks.COPYCAT_VERTICAL_STEP,
            CCBlocks.COPYCAT_STAIRS,
            CCBlocks.COPYCAT_FENCE,
            CCBlocks.COPYCAT_FENCE_GATE,
            CCBlocks.COPYCAT_WALL,
            CCBlocks.COPYCAT_BOARD,
            CCItems.COPYCAT_BOX,
            CCItems.COPYCAT_CATWALK,
            CCItems.CONTROL_CHIP,
            CCItems.MUSIC_DISC_ELEVATOR,
            CCItems.MUSIC_DISC_INTERLUDE
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.create_connected.main"))
            .withTabsBefore(AllCreativeModeTabs.PALETTES_CREATIVE_TAB.getKey())
            .icon(CCBlocks.PARALLEL_GEARBOX::asStack)
            .displayItems(new DisplayItemsGenerator(ITEMS))
            .build());

    public static void hideItems(BuildCreativeModeTabContentsEvent event) {
        if (Objects.equals(event.getTabKey(), MAIN.getKey()) || Objects.equals(event.getTabKey(), CreativeModeTabs.SEARCH)) {
            Set<ItemStack> hiddenItems = ITEMS.stream()
                    .filter(x -> !FeatureToggle.isEnabled(x.getId()))
                    .map(entry -> event.getSearchEntries().stream().filter(stack -> stack.getItem() == entry.asItem()).findFirst()
                            .orElse(event.getParentEntries().stream().filter(stack -> stack.getItem() == entry.asItem()).findFirst()
                                    .orElse(null)))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            for (ItemStack hiddenItem : hiddenItems) {
                event.remove(hiddenItem, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }
    }

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
        modEventBus.addListener(CCCreativeTabs::hideItems);
    }

    private record DisplayItemsGenerator(
            List<ItemProviderEntry<?, ?>> items) implements CreativeModeTab.DisplayItemsGenerator {
        @Override
        public void accept(@NotNull CreativeModeTab.ItemDisplayParameters params, @NotNull CreativeModeTab.Output output) {
            for (ItemProviderEntry<?, ?> item : items) {
                if (FeatureToggle.isEnabled(item.getId())) {
                    output.accept(item);
                }
            }
        }
    }
}
