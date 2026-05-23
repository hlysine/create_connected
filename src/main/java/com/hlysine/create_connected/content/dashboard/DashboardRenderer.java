package com.hlysine.create_connected.content.dashboard;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class DashboardRenderer extends SafeBlockEntityRenderer<DashboardBlockEntity> {

    //taken from sign renderer
    private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);

    private final BlockEntityRendererProvider.Context context;

    public DashboardRenderer(final BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void renderSafe(final DashboardBlockEntity be, final float pPartialTick, final PoseStack ps, final MultiBufferSource buffer, int packedLight, final int packedOverlay) {
        final Font font = this.context.getFont();

        final int lineHeight = be.getTextLineHeight();
        final int maxWidth = be.getMaxTextLineWidth();
        final int midpoint = 4 * lineHeight / 2;

        final BlockState state = be.getBlockState();
        final Direction facing = state.getValue(DashboardBlock.FACING);

        ps.pushPose();

        ps.translate(0.5, 0.5, 0.5);
        ps.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        ps.translate(-0.5, -0.5, -0.5);

        ps.translate(0.5, 12/16f, 9/16f);
        ps.mulPose(Axis.XP.rotationDegrees(-66.80141f));
        ps.translate(0, 3.5/16f, 0.15/16f);

        float scale = 0.015625f * 0.6666667f;
        ps.scale(scale, -scale, scale);

        FormattedCharSequence[] sequences = be.text.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (line) -> {
            List<FormattedCharSequence> list = font.split(line, maxWidth);
            return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
        });

        final int textColor;
        final boolean glowing;
        if (be.text.hasGlowingText()) {
            textColor = be.text.getColor().getTextColor();
            glowing = isOutlineVisible(be.getBlockPos(), textColor);
            packedLight = 15728880;
        } else {
            textColor = SignRenderer.getDarkColor(be.text);
            glowing = false;
        }

        for (int i = 0; i < 4; ++i) {
            FormattedCharSequence sequence = sequences[i];
            float f = (float) (-font.width(sequence) / 2);
            if (glowing) {
                font.drawInBatch8xOutline(sequence, f, (float) (i * lineHeight - midpoint), textColor, i, ps.last().pose(), buffer, packedLight);
            } else {
                font.drawInBatch(sequence, f, (float) (i * lineHeight - midpoint), textColor, false, ps.last().pose(), buffer, Font.DisplayMode.POLYGON_OFFSET, 0, packedLight);
            }
        }

        ps.popPose();
    }

    //taken from sign renderer
    private static boolean isOutlineVisible(final BlockPos blockPos, final int i) {
        if (i == DyeColor.BLACK.getTextColor()) {
            return true;
        } else {
            final Minecraft minecraft = Minecraft.getInstance();
            final LocalPlayer localPlayer = minecraft.player;
            if (localPlayer != null && minecraft.options.getCameraType().isFirstPerson() && localPlayer.isScoping()) {
                return true;
            } else {
                final Entity entity = minecraft.getCameraEntity();
                return entity != null && entity.distanceToSqr(Vec3.atCenterOf(blockPos)) < (double) OUTLINE_RENDER_DISTANCE;
            }
        }
    }
}

