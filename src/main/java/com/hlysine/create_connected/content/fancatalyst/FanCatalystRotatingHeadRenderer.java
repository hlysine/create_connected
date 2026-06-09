package com.hlysine.create_connected.content.fancatalyst;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class FanCatalystRotatingHeadRenderer extends SafeBlockEntityRenderer<FanCatalystRotatingHeadBlockEntity> {
    private final SkullTypes skullType;
    private float animationTick;

    public FanCatalystRotatingHeadRenderer(SkullTypes skullType) {
        this.skullType = skullType;
    }

    public static FanCatalystRotatingHeadRenderer creeper(BlockEntityRendererProvider.Context context) {
        return new FanCatalystRotatingHeadRenderer(SkullTypes.CREEPER.withModelFromContext(context));
    }

    public static FanCatalystRotatingHeadRenderer dragon(BlockEntityRendererProvider.Context context) {
        return new FanCatalystRotatingHeadRenderer(SkullTypes.DRAGON.withModelFromContext(context));
    }

    public RenderType getRenderType() {
        ResourceLocation resourcelocation = SkullBlockRenderer.SKIN_BY_TYPE.get(skullType.getTexture());
        return RenderType.entityCutoutNoCullZOffset(resourcelocation);
    }

    @Override
    protected void renderSafe(FanCatalystRotatingHeadBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        animationTick += partialTicks;
        animationTick %= 360;
        ms.pushPose();
        skullType.translate(ms);
        skullType.scale(ms);
        skullType.getModel().setupAnim(0, animationTick, 0.0F);
        skullType.getModel().renderToBuffer(ms, bufferSource.getBuffer(getRenderType()), light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        ms.popPose();
    }
}
