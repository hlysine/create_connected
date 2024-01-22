package com.hlysine.create_connected;

import com.hlysine.create_connected.config.FeatureToggle;
import com.simibubi.create.AllCreativeModeTabs;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class CCCreativeTabs {
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "create_connected" namespace
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateConnected.MODID);

    public static final List<ItemProviderEntry<?>> ITEMS = List.of(
            CCBlocks.ENCASED_CHAIN_COGWHEEL,
            CCBlocks.INVERTED_CLUTCH,
            CCBlocks.INVERTED_GEARSHIFT,
            CCBlocks.PARALLEL_GEARBOX,
            CCItems.VERTICAL_PARALLEL_GEARBOX,
            CCBlocks.SIX_WAY_GEARBOX,
            CCItems.VERTICAL_SIX_WAY_GEARBOX,
            CCBlocks.BRASS_GEARBOX,
            CCItems.VERTICAL_BRASS_GEARBOX,
            CCBlocks.SHEAR_PIN,
            CCBlocks.OVERSTRESS_CLUTCH,
            CCBlocks.CENTRIFUGAL_CLUTCH,
            CCBlocks.FREEWHEEL_CLUTCH,
            CCBlocks.BRAKE,
            CCBlocks.ITEM_SILO,
            CCBlocks.SEQUENCED_PULSE_GENERATOR,
            CCItems.LINKED_TRANSMITTER,
            CCBlocks.EMPTY_FAN_CATALYST,
            CCBlocks.FAN_BLASTING_CATALYST,
            CCBlocks.FAN_SMOKING_CATALYST,
            CCBlocks.FAN_SPLASHING_CATALYST,
            CCBlocks.FAN_HAUNTING_CATALYST,
            CCBlocks.COPYCAT_BLOCK,
            CCBlocks.COPYCAT_SLAB,
            CCBlocks.COPYCAT_BEAM,
            CCBlocks.COPYCAT_VERTICAL_STEP,
            CCBlocks.COPYCAT_STAIRS,
            CCBlocks.COPYCAT_FENCE,
            CCBlocks.COPYCAT_WALL,
            CCBlocks.COPYCAT_BOARD,
            CCItems.CONTROL_CHIP,
            CCItems.MUSIC_DISC_ELEVATOR,
            CCItems.MUSIC_DISC_INTERLUDE,
            CCBlocks.CHERRY_WINDOW,
            CCBlocks.BAMBOO_WINDOW,
            CCBlocks.CHERRY_WINDOW_PANE,
            CCBlocks.BAMBOO_WINDOW_PANE
    );

    public static final RegistryObject<CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.create_connected.main"))
            .withTabsBefore(AllCreativeModeTabs.PALETTES_CREATIVE_TAB.getKey())
            .icon(CCBlocks.PARALLEL_GEARBOX::asStack)
            .displayItems(new DisplayItemsGenerator(ITEMS))
            .build());

    public static void hideItems(BuildCreativeModeTabContentsEvent event) {
        if (Objects.equals(event.getTabKey(), MAIN.getKey()) || Objects.equals(event.getTabKey(), CreativeModeTabs.SEARCH)) {
            Set<Item> hiddenItems = ITEMS.stream()
                    .filter(x -> !FeatureToggle.isEnabled(x.getId()))
                    .map(ItemProviderEntry::asItem)
                    .collect(Collectors.toSet());
            for (Iterator<Map.Entry<ItemStack, CreativeModeTab.TabVisibility>> iterator = event.getEntries().iterator(); iterator.hasNext(); ) {
                Map.Entry<ItemStack, CreativeModeTab.TabVisibility> entry = iterator.next();
                if (hiddenItems.contains(entry.getKey().getItem()))
                    iterator.remove();
            }
        }
    }

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
        modEventBus.addListener(CCCreativeTabs::hideItems);
    }

    private record DisplayItemsGenerator(
            List<ItemProviderEntry<?>> items) implements CreativeModeTab.DisplayItemsGenerator {
        @Override
        public void accept(@NotNull CreativeModeTab.ItemDisplayParameters params, @NotNull CreativeModeTab.Output output) {
            for (ItemProviderEntry<?> item : items) {
                if (FeatureToggle.isEnabled(item.getId())) {
                    output.accept(item);
                }
            }
        }
    }
}
