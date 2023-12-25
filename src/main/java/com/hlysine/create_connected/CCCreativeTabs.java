package com.hlysine.create_connected;

import com.hlysine.create_connected.config.FeatureToggle;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CCCreativeTabs {
    public static final CreativeModeTab MAIN = new MainCreativeModeTab();

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
            CCBlocks.EMPTY_FAN_CATALYST,
            CCBlocks.FAN_BLASTING_CATALYST,
            CCBlocks.FAN_SMOKING_CATALYST,
            CCBlocks.FAN_SPLASHING_CATALYST,
            CCBlocks.FAN_HAUNTING_CATALYST,
            CCBlocks.COPYCAT_BLOCK,
            CCBlocks.COPYCAT_SLAB,
            CCBlocks.COPYCAT_BEAM,
            CCBlocks.COPYCAT_VERTICAL_STEP
    );

    public static void register() {
    }

    public static class MainCreativeModeTab extends CreativeModeTab {

        public MainCreativeModeTab() {
            super(CreateConnected.MODID + ".main");
        }

        @Override
        public ItemStack makeIcon() {
            return CCBlocks.PARALLEL_GEARBOX.asStack();
        }

        @Override
        public void fillItemList(@NotNull NonNullList<ItemStack> pItems) {
            for (ItemProviderEntry<?> item : ITEMS) {
                if (FeatureToggle.isEnabled(item.getId()))
                    item.get().asItem().fillItemCategory(this, pItems);
            }
        }
    }
}
