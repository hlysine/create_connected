package com.hlysine.create_connected;

import com.hlysine.create_connected.content.overstressclutch.OverstressClutchBlockEntity;
import com.hlysine.create_connected.content.parallelgearbox.ParallelGearboxBlockEntity;
import com.hlysine.create_connected.content.parallelgearbox.ParallelGearboxInstance;
import com.hlysine.create_connected.content.parallelgearbox.ParallelGearboxRenderer;
import com.hlysine.create_connected.content.shearpin.ShearPinBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.transmission.SplitShaftInstance;
import com.simibubi.create.content.kinetics.transmission.SplitShaftRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class CCBlockEntityTypes {
    private static final CreateRegistrate REGISTRATE = CreateConnected.getRegistrate();

    public static final BlockEntityEntry<ParallelGearboxBlockEntity> PARALLEL_GEARBOX = REGISTRATE
            .blockEntity("parallel_gearbox", ParallelGearboxBlockEntity::new)
            .instance(() -> ParallelGearboxInstance::new, false)
            .validBlocks(CCBlocks.PARALLEL_GEARBOX)
            .renderer(() -> ParallelGearboxRenderer::new)
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

    public static void register() {
    }
}
