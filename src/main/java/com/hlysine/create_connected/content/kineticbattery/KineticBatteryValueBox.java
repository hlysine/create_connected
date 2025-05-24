package com.hlysine.create_connected.content.kineticbattery;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.Pointing;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class KineticBatteryValueBox extends ValueBoxTransform.Sided {

    private final double offset;

    public KineticBatteryValueBox(double offset) {
        this.offset = offset;
    }

    @Override
    protected boolean isSideActive(BlockState state, Direction side) {
        Direction batteryFacing = state.getValue(KineticBatteryBlock.FACING);
        return batteryFacing.getAxis() != side.getAxis();
    }

    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction side = getSide();
        Direction batteryFacing = state.getValue(KineticBatteryBlock.FACING);

        float roll = 0;
        for (Pointing p : Pointing.values())
            if (p.getCombinedDirection(batteryFacing) == side)
                roll = p.getXRotation();
        if (batteryFacing == Direction.UP)
            roll += 180;

        float horizontalAngle = AngleHelper.horizontalAngle(batteryFacing);
        float verticalAngle = AngleHelper.verticalAngle(batteryFacing);
        Vec3 local = VecHelper.voxelSpace(8, 15.5, offset);

        local = VecHelper.rotateCentered(local, roll, Axis.Z);
        local = VecHelper.rotateCentered(local, horizontalAngle, Axis.Y);
        local = VecHelper.rotateCentered(local, verticalAngle, Axis.X);

        return local;
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        Direction facing = state.getValue(KineticBatteryBlock.FACING);

        if (facing.getAxis() == Axis.Y) {
            super.rotate(level, pos, state, ms);
            return;
        }

        float roll = 0;
        for (Pointing p : Pointing.values())
            if (p.getCombinedDirection(facing) == getSide())
                roll = p.getXRotation();

        float yRot = AngleHelper.horizontalAngle(facing) + (facing == Direction.DOWN ? 180 : 0);
        TransformStack.of(ms)
                .rotateYDegrees(yRot)
                .rotateXDegrees(facing == Direction.DOWN ? -90 : 90)
                .rotateYDegrees(roll);
    }

    @Override
    protected Vec3 getSouthLocation() {
        return Vec3.ZERO;
    }

}

