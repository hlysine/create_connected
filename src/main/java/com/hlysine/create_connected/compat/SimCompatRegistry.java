/*
package com.hlysine.create_connected.compat;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.registries.PreciseItemUseOverrides;
import com.hlysine.create_connected.content.linkedtransmitter.*;
import com.hlysine.create_connected.datagen.CCBlockStateGen;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverVisual;
import dev.simulated_team.simulated.index.SimBlocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Blocks;

public class SimCompatRegistry {
    private static final CreateRegistrate REGISTRATE = CreateConnected.getRegistrate();

    public static final BlockEntry<LinkedThrottleLeverBlock> LINKED_THROTTLE_LEVER = REGISTRATE
            .block("linked_throttle_lever", properties -> new LinkedThrottleLeverBlock(properties, SimBlocks.THROTTLE_LEVER))
            .initialProperties(() -> Blocks.LEVER)
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(LinkedTransmitterItem.register())
            .onRegister(PreciseItemUseOverrides::addBlock)
            .blockstate(CCBlockStateGen.linkedLeverNoPower(
                    Simulated.path("block/throttle_lever/block")
            ))
            .register();

    public static final BlockEntityEntry<LinkedThrottleLeverBlockEntity> LINKED_THROTTLE_LEVER_ENTITY = REGISTRATE
            .blockEntity("linked_throttle_lever", LinkedThrottleLeverBlockEntity::new)
            .visual(() -> ThrottleLeverVisual::new)
            .validBlocks(SimCompatRegistry.LINKED_THROTTLE_LEVER)
            .renderer(() -> LinkedThrottleLeverRenderer::new)
            .register();

    public static void register() {
    }
}
*/