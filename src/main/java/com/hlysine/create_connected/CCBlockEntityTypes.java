package com.hlysine.create_connected;

import com.hlysine.create_connected.content.brake.BrakeBlockEntity;
import com.hlysine.create_connected.content.brassgearbox.BrassGearboxBlockEntity;
import com.hlysine.create_connected.content.brassgearbox.BrassGearboxInstance;
import com.hlysine.create_connected.content.brassgearbox.BrassGearboxRenderer;
import com.hlysine.create_connected.content.centrifugalclutch.CentrifugalClutchBlockEntity;
import com.hlysine.create_connected.content.freewheelclutch.FreewheelClutchBlockEntity;
import com.hlysine.create_connected.content.invertedclutch.InvertedClutchBlockEntity;
import com.hlysine.create_connected.content.invertedgearshift.InvertedGearshiftBlockEntity;
import com.hlysine.create_connected.content.itemsilo.ItemSiloBlockEntity;
import com.hlysine.create_connected.content.overstressclutch.OverstressClutchBlockEntity;
import com.hlysine.create_connected.content.parallelgearbox.ParallelGearboxBlockEntity;
import com.hlysine.create_connected.content.parallelgearbox.ParallelGearboxInstance;
import com.hlysine.create_connected.content.parallelgearbox.ParallelGearboxRenderer;
import com.hlysine.create_connected.content.shearpin.ShearPinBlockEntity;
import com.hlysine.create_connected.content.sixwaygearbox.SixWayGearboxBlockEntity;
import com.hlysine.create_connected.content.sixwaygearbox.SixWayGearboxInstance;
import com.hlysine.create_connected.content.sixwaygearbox.SixWayGearboxRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogInstance;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogRenderer;
import com.simibubi.create.content.kinetics.transmission.SplitShaftInstance;
import com.simibubi.create.content.kinetics.transmission.SplitShaftRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class CCBlockEntityTypes {
    private static final CreateRegistrate REGISTRATE = CreateConnected.getRegistrate();

    public static final BlockEntityEntry<SimpleKineticBlockEntity> ENCASED_CHAIN_COGWHEEL = REGISTRATE
            .blockEntity("encased_chain_cogwheel", SimpleKineticBlockEntity::new)
            .instance(() -> EncasedCogInstance::small, false)
            .validBlocks(CCBlocks.ENCASED_CHAIN_COGWHEEL)
            .renderer(() -> EncasedCogRenderer::small)
            .register();

    public static final BlockEntityEntry<ParallelGearboxBlockEntity> PARALLEL_GEARBOX = REGISTRATE
            .blockEntity("parallel_gearbox", ParallelGearboxBlockEntity::new)
            .instance(() -> ParallelGearboxInstance::new, false)
            .validBlocks(CCBlocks.PARALLEL_GEARBOX)
            .renderer(() -> ParallelGearboxRenderer::new)
            .register();

    public static final BlockEntityEntry<SixWayGearboxBlockEntity> SIX_WAY_GEARBOX = REGISTRATE
            .blockEntity("six_way_gearbox", SixWayGearboxBlockEntity::new)
            .instance(() -> SixWayGearboxInstance::new, false)
            .validBlocks(CCBlocks.SIX_WAY_GEARBOX)
            .renderer(() -> SixWayGearboxRenderer::new)
            .register();


    public static final BlockEntityEntry<OverstressClutchBlockEntity> OVERSTRESS_CLUTCH = REGISTRATE
            .blockEntity("overstress_clutch", OverstressClutchBlockEntity::new)
            .instance(() -> SplitShaftInstance::new, false)
            .validBlocks(CCBlocks.OVERSTRESS_CLUTCH)
            .renderer(() -> SplitShaftRenderer::new)
            .register();


    public static final BlockEntityEntry<ShearPinBlockEntity> SHEAR_PIN = REGISTRATE
            .blockEntity("shear_pin", ShearPinBlockEntity::new)
            .instance(() -> BracketedKineticBlockEntityInstance::new, false)
            .validBlocks(CCBlocks.SHEAR_PIN)
            .renderer(() -> BracketedKineticBlockEntityRenderer::new)
            .register();

    public static final BlockEntityEntry<InvertedClutchBlockEntity> INVERTED_CLUTCH = REGISTRATE
            .blockEntity("inverted_clutch", InvertedClutchBlockEntity::new)
            .instance(() -> SplitShaftInstance::new, false)
            .validBlocks(CCBlocks.INVERTED_CLUTCH)
            .renderer(() -> SplitShaftRenderer::new)
            .register();

    public static final BlockEntityEntry<InvertedGearshiftBlockEntity> INVERTED_GEARSHIFT = REGISTRATE
            .blockEntity("inverted_gearshift", InvertedGearshiftBlockEntity::new)
            .instance(() -> SplitShaftInstance::new, false)
            .validBlocks(CCBlocks.INVERTED_GEARSHIFT)
            .renderer(() -> SplitShaftRenderer::new)
            .register();

    public static final BlockEntityEntry<CentrifugalClutchBlockEntity> CENTRIFUGAL_CLUTCH = REGISTRATE
            .blockEntity("centrifugal_clutch", CentrifugalClutchBlockEntity::new)
            .instance(() -> SplitShaftInstance::new, false)
            .validBlocks(CCBlocks.CENTRIFUGAL_CLUTCH)
            .renderer(() -> SplitShaftRenderer::new)
            .register();

    public static final BlockEntityEntry<FreewheelClutchBlockEntity> FREEWHEEL_CLUTCH = REGISTRATE
            .blockEntity("freewheel_clutch", FreewheelClutchBlockEntity::new)
            .instance(() -> SplitShaftInstance::new, false)
            .validBlocks(CCBlocks.FREEWHEEL_CLUTCH)
            .renderer(() -> SplitShaftRenderer::new)
            .register();

    public static final BlockEntityEntry<BrassGearboxBlockEntity> BRASS_GEARBOX = REGISTRATE
            .blockEntity("brass_gearbox", BrassGearboxBlockEntity::new)
            .instance(() -> BrassGearboxInstance::new, false)
            .validBlocks(CCBlocks.BRASS_GEARBOX)
            .renderer(() -> BrassGearboxRenderer::new)
            .register();

    public static final BlockEntityEntry<BrakeBlockEntity> BRAKE = REGISTRATE
            .blockEntity("brake", BrakeBlockEntity::new)
            .instance(() -> SplitShaftInstance::new, false)
            .validBlocks(CCBlocks.BRAKE)
            .renderer(() -> SplitShaftRenderer::new)
            .register();

    public static final BlockEntityEntry<ItemSiloBlockEntity> ITEM_SILO = REGISTRATE
            .blockEntity("item_silo", ItemSiloBlockEntity::new)
            .validBlocks(CCBlocks.ITEM_SILO)
            .register();


    public static void register() {
    }
}
