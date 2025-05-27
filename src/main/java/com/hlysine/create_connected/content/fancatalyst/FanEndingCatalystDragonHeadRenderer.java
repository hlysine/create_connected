package com.hlysine.create_connected.content.fancatalyst;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.dragon.DragonHeadModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SkullBlock;

public class FanEndingCatalystDragonHeadRenderer extends SafeBlockEntityRenderer<FanEndingCatalystDragonHeadBlockEntity> {
    private final SkullModelBase skullModel;
    private float animationTick;

    public FanEndingCatalystDragonHeadRenderer(BlockEntityRendererProvider.Context context) {
        this.skullModel = new DragonHeadModel(context.getModelSet().bakeLayer(ModelLayers.DRAGON_SKULL));
    }

    public static RenderType getRenderType() {
        ResourceLocation resourcelocation = SkullBlockRenderer.SKIN_BY_TYPE.get(SkullBlock.Types.DRAGON);
        return RenderType.entityCutoutNoCullZOffset(resourcelocation);
    }

    @Override
    protected void renderSafe(FanEndingCatalystDragonHeadBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        animationTick += partialTicks;
        animationTick %= 360;
        ms.pushPose();
        ms.translate(0.5F, 0.25F, 0.5F);
        ms.scale(-0.5F, -0.5F, 0.5F);
        skullModel.setupAnim(0, animationTick, 0.0F);
        skullModel.renderToBuffer(ms, bufferSource.getBuffer(getRenderType()), light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        ms.popPose();
    }
}
