package com.hlysine.create_connected;

import com.hlysine.create_connected.content.unidirectionalgearbox.VerticalUnidirectionalGearboxItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;

public class CCItems {

    private static final CreateRegistrate REGISTRATE = CreateConnected.getRegistrate();

    static {
        REGISTRATE.setCreativeTab(CCCreativeTabs.MAIN);
    }

    public static final ItemEntry<VerticalUnidirectionalGearboxItem> VERTICAL_UNIDIRECTIONAL_GEARBOX =
            REGISTRATE.item("vertical_unidirectional_gearbox", VerticalUnidirectionalGearboxItem::new)
                    .model(AssetLookup.customBlockItemModel("unidirectional_gearbox", "item_vertical"))
                    .register();

    public static void register() {
    }
}
