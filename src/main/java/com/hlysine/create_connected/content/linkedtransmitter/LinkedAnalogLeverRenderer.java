package com.hlysine.create_connected.content.linkedtransmitter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlockEntity;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverRenderer;
import com.simibubi.create.content.redstone.link.LinkRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class LinkedAnalogLeverRenderer extends AnalogLeverRenderer {
    public LinkedAnalogLeverRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(AnalogLeverBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        FilteringRenderer.renderOnBlockEntity(be, partialTicks, ms, buffer, light, overlay);
        LinkRenderer.renderOnBlockEntity(be, partialTicks, ms, buffer, light, overlay);
    }
}
