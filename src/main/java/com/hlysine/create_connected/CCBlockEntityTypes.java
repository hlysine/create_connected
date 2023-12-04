package com.hlysine.create_connected;

import com.hlysine.create_connected.content.overstressclutch.OverstressClutchBlockEntity;
import com.hlysine.create_connected.content.unidirectionalgearbox.UnidirectionalGearboxBlockEntity;
import com.hlysine.create_connected.content.unidirectionalgearbox.UnidirectionalGearboxInstance;
import com.hlysine.create_connected.content.unidirectionalgearbox.UnidirectionalGearboxRenderer;
import com.simibubi.create.content.kinetics.transmission.SplitShaftInstance;
import com.simibubi.create.content.kinetics.transmission.SplitShaftRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class CCBlockEntityTypes {
    private static final CreateRegistrate REGISTRATE = CreateConnected.getRegistrate();

    public static final BlockEntityEntry<UnidirectionalGearboxBlockEntity> UNIDIRECTIONAL_GEARBOX = REGISTRATE
            .blockEntity("unidirectional_gearbox", UnidirectionalGearboxBlockEntity::new)
            .instance(() -> UnidirectionalGearboxInstance::new, false)
            .validBlocks(CCBlocks.UNIDIRECTIONAL_GEARBOX)
            .renderer(() -> UnidirectionalGearboxRenderer::new)
            .register();


    public static final BlockEntityEntry<OverstressClutchBlockEntity> OVERSTRESS_CLUTCH = REGISTRATE
            .blockEntity("overstress_clutch", OverstressClutchBlockEntity::new)
            .instance(() -> SplitShaftInstance::new, false)
            .validBlocks(CCBlocks.OVERSTRESS_CLUTCH)
            .renderer(() -> SplitShaftRenderer::new)
            .register();

    public static void register() {
    }
}
