package com.hlysine.create_connected.content.linkedtransmitter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.redstone.link.LinkRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverBlockEntity;
import dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class LinkedThrottleLeverRenderer extends ThrottleLeverRenderer {
    public LinkedThrottleLeverRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(ThrottleLeverBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        FilteringRenderer.renderOnBlockEntity(be, partialTicks, ms, buffer, light, overlay);
        LinkRenderer.renderOnBlockEntity(be, partialTicks, ms, buffer, light, overlay);
    }
}

