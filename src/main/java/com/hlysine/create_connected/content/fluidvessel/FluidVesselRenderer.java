package com.hlysine.create_connected.content.fluidvessel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.platform.ForgeCatnipServices;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class FluidVesselRenderer extends SafeBlockEntityRenderer<FluidVesselBlockEntity> {

    public FluidVesselRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(FluidVesselBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        if (!be.isController())
            return;
        if (!be.hasWindow()) {
            if (be.boiler.isActive())
                renderAsBoiler(be, partialTicks, ms, buffer, light, overlay);
            return;
        }

        LerpedFloat fluidLevel = be.getFluidLevel();
        if (fluidLevel == null)
            return;

        float capSize = 1 / 4f;
        float tankHullSize = 1 / 16f + 1 / 128f;
        float minPuddleHeight = 1 / 16f;
        float totalHeight = be.getWidth() - 2 * tankHullSize - minPuddleHeight;

        float level = fluidLevel.getValue(partialTicks);
        if (level < 1 / (512f * totalHeight))
            return;
        float clampedLevel = Mth.clamp(level * totalHeight, 0, totalHeight);

        FluidTank tank = be.getTankInventory();
        FluidStack fluidStack = tank.getFluid();

        if (fluidStack.isEmpty())
            return;

        boolean top = fluidStack.getFluid()
                .getFluidType()
                .isLighterThanAir();

        Axis axis = be.getAxis();
        float xMin = axis == Axis.X ? capSize : tankHullSize;
        float xMax = axis == Axis.X ? xMin + be.getHeight() - 2 * capSize : xMin + be.getWidth() - 2 * tankHullSize;
        float yMin = totalHeight + tankHullSize + minPuddleHeight - clampedLevel;
        float yMax = yMin + clampedLevel;

        if (top) {
            yMin += totalHeight - clampedLevel;
            yMax += totalHeight - clampedLevel;
        }

        float zMin = axis == Axis.Z ? capSize : tankHullSize;
        float zMax = axis == Axis.Z ? zMin + be.getHeight() - 2 * capSize : zMin + be.getWidth() - 2 * tankHullSize;

        ms.pushPose();
        ms.translate(0, clampedLevel - totalHeight, 0);
        ForgeCatnipServices.FLUID_RENDERER.renderFluidBox(
                fluidStack,
                xMin, yMin, zMin,
                xMax, yMax, zMax,
                buffer, ms, light,
                false,
                true
        );
        ms.popPose();
    }

    protected void renderAsBoiler(FluidVesselBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                                  int light, int overlay) {
        BlockState blockState = be.getBlockState();
        VertexConsumer vb = buffer.getBuffer(RenderType.cutout());
        ms.pushPose();
        var msr = TransformStack.of(ms);
        Axis axis = be.getAxis();
        msr.translate(axis == Axis.X ? be.getHeight() / 2f : be.getWidth() / 2f, 0.5, axis == Axis.Z ? be.getHeight() / 2f : be.getWidth() / 2f);

        float dialPivotY = 6f / 16;
        float dialPivotZ = 8f / 16;
        float progress = be.boiler.gauge.getValue(partialTicks);

        for (Direction d : Iterate.horizontalDirections) {
            if (be.boiler.occludedDirections[d.get2DDataValue()])
                continue;
            if (d.getAxis() != axis)
                continue;
            ms.pushPose();
            float yRot = -d.toYRot() - 90;
            CachedBuffers.partial(AllPartialModels.BOILER_GAUGE, blockState)
                    .rotateYDegrees(yRot)
                    .uncenter()
                    .translate(be.getWidth() / 2f - 6 / 16f, 0, 0)
                    .light(light)
                    .renderInto(ms, vb);
            CachedBuffers.partial(AllPartialModels.BOILER_GAUGE_DIAL, blockState)
                    .rotateYDegrees(yRot)
                    .uncenter()
                    .translate(be.getWidth() / 2f - 6 / 16f, 0, 0)
                    .translate(0, dialPivotY, dialPivotZ)
                    .rotateXDegrees(-145 * progress + 90)
                    .translate(0, -dialPivotY, -dialPivotZ)
                    .light(light)
                    .renderInto(ms, vb);
            ms.popPose();
        }

        ms.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(FluidVesselBlockEntity be) {
        return be.isController();
    }

}
