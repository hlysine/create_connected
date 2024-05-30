package com.hlysine.create_connected;

import com.hlysine.create_connected.content.brake.BrakeBlockEntity;
import com.hlysine.create_connected.content.brassgearbox.BrassGearboxBlockEntity;
import com.hlysine.create_connected.content.brassgearbox.BrassGearboxInstance;
import com.hlysine.create_connected.content.brassgearbox.BrassGearboxRenderer;
import com.hlysine.create_connected.content.centrifugalclutch.CentrifugalClutchBlockEntity;
import com.hlysine.create_connected.content.crankwheel.CrankWheelBlockEntity;
import com.hlysine.create_connected.content.fluidvessel.CreativeFluidVesselBlockEntity;
import com.hlysine.create_connected.content.fluidvessel.FluidVesselBlockEntity;
import com.hlysine.create_connected.content.fluidvessel.FluidVesselRenderer;
import com.hlysine.create_connected.content.freewheelclutch.FreewheelClutchBlockEntity;
import com.hlysine.create_connected.content.inventoryaccessport.InventoryAccessPortBlockEntity;
import com.hlysine.create_connected.content.invertedclutch.InvertedClutchBlockEntity;
import com.hlysine.create_connected.content.invertedgearshift.InvertedGearshiftBlockEntity;
import com.hlysine.create_connected.content.itemsilo.ItemSiloBlockEntity;
import com.hlysine.create_connected.content.linkedtransmitter.LinkedAnalogLeverBlockEntity;
import com.hlysine.create_connected.content.linkedtransmitter.LinkedAnalogLeverRenderer;
import com.hlysine.create_connected.content.linkedtransmitter.LinkedTransmitterBlockEntity;
import com.hlysine.create_connected.content.overstressclutch.OverstressClutchBlockEntity;
import com.hlysine.create_connected.content.parallelgearbox.ParallelGearboxBlockEntity;
import com.hlysine.create_connected.content.parallelgearbox.ParallelGearboxInstance;
import com.hlysine.create_connected.content.parallelgearbox.ParallelGearboxRenderer;
import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import com.hlysine.create_connected.content.shearpin.ShearPinBlockEntity;
import com.hlysine.create_connected.content.sixwaygearbox.SixWayGearboxBlockEntity;
import com.hlysine.create_connected.content.sixwaygearbox.SixWayGearboxInstance;
import com.hlysine.create_connected.content.sixwaygearbox.SixWayGearboxRenderer;
import com.simibubi.create.content.decoration.copycat.CopycatBlockEntity;
import com.simibubi.create.content.kinetics.crank.HandCrankInstance;
import com.simibubi.create.content.kinetics.crank.HandCrankRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogInstance;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogRenderer;
import com.simibubi.create.content.kinetics.transmission.SplitShaftInstance;
import com.simibubi.create.content.kinetics.transmission.SplitShaftRenderer;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverInstance;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
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

    public static final BlockEntityEntry<CrankWheelBlockEntity> CRANK_WHEEL = REGISTRATE
            .blockEntity("crank_wheel", CrankWheelBlockEntity::new)
            .instance(() -> HandCrankInstance::new)
            .validBlocks(CCBlocks.CRANK_WHEEL, CCBlocks.LARGE_CRANK_WHEEL)
            .renderer(() -> HandCrankRenderer::new)
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

    public static final BlockEntityEntry<FluidVesselBlockEntity> FLUID_VESSEL = REGISTRATE
            .blockEntity("fluid_vessel", FluidVesselBlockEntity::new)
            .validBlocks(CCBlocks.FLUID_VESSEL)
            .renderer(() -> FluidVesselRenderer::new)
            .register();

    public static final BlockEntityEntry<CreativeFluidVesselBlockEntity> CREATIVE_FLUID_VESSEL = REGISTRATE
            .blockEntity("creative_fluid_vessel", CreativeFluidVesselBlockEntity::new)
            .validBlocks(CCBlocks.CREATIVE_FLUID_VESSEL)
            .renderer(() -> FluidVesselRenderer::new)
            .register();

    public static final BlockEntityEntry<InventoryAccessPortBlockEntity> INVENTORY_ACCESS_PORT = REGISTRATE
            .blockEntity("inventory_access_port", InventoryAccessPortBlockEntity::new)
            .validBlocks(CCBlocks.INVENTORY_ACCESS_PORT)
            .register();

    public static final BlockEntityEntry<SequencedPulseGeneratorBlockEntity> SEQUENCED_PULSE_GENERATOR = REGISTRATE
            .blockEntity("sequenced_pulse_generator", SequencedPulseGeneratorBlockEntity::new)
            .validBlocks(CCBlocks.SEQUENCED_PULSE_GENERATOR)
            .register();

    public static final BlockEntityEntry<LinkedTransmitterBlockEntity> LINKED_TRANSMITTER = REGISTRATE
            .blockEntity("linked_transmitter", LinkedTransmitterBlockEntity::new)
            .transform(b -> {
                CCBlocks.LINKED_BUTTONS.values().forEach(b::validBlock);
                return b;
            })
            .validBlocks(CCBlocks.LINKED_LEVER)
            .renderer(() -> SmartBlockEntityRenderer::new)
            .register();

    public static final BlockEntityEntry<LinkedAnalogLeverBlockEntity> LINKED_ANALOG_LEVER = REGISTRATE
            .blockEntity("linked_analog_lever", LinkedAnalogLeverBlockEntity::new)
            .instance(() -> AnalogLeverInstance::new)
            .validBlocks(CCBlocks.LINKED_ANALOG_LEVER)
            .renderer(() -> LinkedAnalogLeverRenderer::new)
            .register();

    public static final BlockEntityEntry<CopycatBlockEntity> COPYCAT =
            REGISTRATE.blockEntity("copycat", CopycatBlockEntity::new)
                    .validBlocks(
                            CCBlocks.COPYCAT_BLOCK,
                            CCBlocks.COPYCAT_SLAB,
                            CCBlocks.COPYCAT_BEAM,
                            CCBlocks.COPYCAT_VERTICAL_STEP,
                            CCBlocks.COPYCAT_STAIRS,
                            CCBlocks.COPYCAT_FENCE,
                            CCBlocks.COPYCAT_FENCE_GATE,
                            CCBlocks.COPYCAT_WALL,
                            CCBlocks.COPYCAT_BOARD
                    )
                    .register();


    public static void register() {
    }
}
