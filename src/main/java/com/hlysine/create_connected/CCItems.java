package com.hlysine.create_connected;

import com.hlysine.create_connected.content.brassgearbox.VerticalBrassGearboxItem;
import com.hlysine.create_connected.content.parallelgearbox.VerticalParallelGearboxItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;

public class CCItems {

    private static final CreateRegistrate REGISTRATE = CreateConnected.getRegistrate();

    static {
        REGISTRATE.setCreativeTab(CCCreativeTabs.MAIN);
    }

    public static final ItemEntry<VerticalParallelGearboxItem> VERTICAL_PARALLEL_GEARBOX =
            REGISTRATE.item("vertical_parallel_gearbox", VerticalParallelGearboxItem::new)
                    .model(AssetLookup.customBlockItemModel("parallel_gearbox", "item_vertical"))
                    .register();

    public static final ItemEntry<VerticalBrassGearboxItem> VERTICAL_BRASS_GEARBOX =
            REGISTRATE.item("vertical_brass_gearbox", VerticalBrassGearboxItem::new)
                    .model(AssetLookup.customBlockItemModel("brass_gearbox", "item_vertical"))
                    .register();

    public static void register() {
    }
}
