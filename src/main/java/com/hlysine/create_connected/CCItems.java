package com.hlysine.create_connected;

import com.hlysine.create_connected.config.FeatureToggle;
import com.hlysine.create_connected.content.brassgearbox.VerticalBrassGearboxItem;
import com.hlysine.create_connected.content.linkedtransmitter.LinkedTransmitterItem;
import com.hlysine.create_connected.content.parallelgearbox.VerticalParallelGearboxItem;
import com.hlysine.create_connected.content.sixwaygearbox.VerticalSixWayGearboxItem;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;

public class CCItems {

    private static final CreateRegistrate REGISTRATE = CreateConnected.getRegistrate();

    static {
        REGISTRATE.setCreativeTab(CCCreativeTabs.MAIN);
    }

    public static final ItemEntry<Item> CONTROL_CHIP =
            REGISTRATE.item("control_chip", Item::new)
                    .register();

    public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_CONTROL_CHIP =
            REGISTRATE.item("incomplete_control_chip", SequencedAssemblyItem::new)
                    .register();

    public static final ItemEntry<VerticalParallelGearboxItem> VERTICAL_PARALLEL_GEARBOX =
            REGISTRATE.item("vertical_parallel_gearbox", VerticalParallelGearboxItem::new)
                    .model(AssetLookup.customBlockItemModel("parallel_gearbox", "item_vertical"))
                    .transform(FeatureToggle.registerDependent(CCBlocks.PARALLEL_GEARBOX))
                    .register();

    public static final ItemEntry<VerticalSixWayGearboxItem> VERTICAL_SIX_WAY_GEARBOX =
            REGISTRATE.item("vertical_six_way_gearbox", VerticalSixWayGearboxItem::new)
                    .model(AssetLookup.customBlockItemModel("six_way_gearbox", "item_vertical"))
                    .transform(FeatureToggle.registerDependent(CCBlocks.SIX_WAY_GEARBOX))
                    .lang("Vertical 6-way Gearbox")
                    .register();

    public static final ItemEntry<VerticalBrassGearboxItem> VERTICAL_BRASS_GEARBOX =
            REGISTRATE.item("vertical_brass_gearbox", VerticalBrassGearboxItem::new)
                    .model(AssetLookup.customBlockItemModel("brass_gearbox", "item_vertical"))
                    .transform(FeatureToggle.registerDependent(CCBlocks.BRASS_GEARBOX))
                    .register();

    public static final ItemEntry<LinkedTransmitterItem> LINKED_TRANSMITTER =
            REGISTRATE.item("linked_transmitter", LinkedTransmitterItem::new)
                    .model(AssetLookup.customGenericItemModel("linked_transmitter", "item"))
                    .transform(FeatureToggle.register())
                    .register();

    public static final ItemEntry<RecordItem> MUSIC_DISC_ELEVATOR =
            REGISTRATE.item("music_disc_elevator", properties -> new RecordItem(15, CCSoundEvents.ELEVATOR_MUSIC::getMainEvent, properties, 4820))
                    .transform(FeatureToggle.register())
                    .properties(p -> p.stacksTo(1).rarity(Rarity.RARE))
                    .tag(ItemTags.MUSIC_DISCS)
                    .lang("Music Disc")
                    .register();

    public static void register() {
    }
}
