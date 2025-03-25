package com.hlysine.create_connected.content.linkedtransmitter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class LinkedTransmitterFrequencySlot extends ValueBoxTransform.Dual {

    public LinkedTransmitterFrequencySlot(boolean first) {
        super(first);
    }

    @Override
    public boolean shouldRender(LevelAccessor level, BlockPos pos, BlockState state) {
        return !state.getValue(BlockStateProperties.LOCKED) && super.shouldRender(level, pos, state);
    }

    @Override
    public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
        return !state.getValue(BlockStateProperties.LOCKED) && super.testHit(level, pos, state, localHit);
    }

    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(LinkedButtonBlock.FACING);
        AttachFace face = state.getValue(LinkedButtonBlock.FACE);

        Vec3 location = switch (face) {
            case FLOOR ->
                    VecHelper.voxelSpace(2.5f, 1.1f, 10.5f).add(isFirst() ? Vec3.ZERO : VecHelper.voxelSpace(0, 0, -5));
            case WALL ->
                    VecHelper.voxelSpace(13.5f, 10.5f, 1.1f).add(isFirst() ? Vec3.ZERO : VecHelper.voxelSpace(0, -5, 0));
            case CEILING ->
                    VecHelper.voxelSpace(2.5f, 14.9f, 5.5f).add(isFirst() ? Vec3.ZERO : VecHelper.voxelSpace(0, 0, 5));
        };
        location = VecHelper.rotateCentered(location, AngleHelper.horizontalAngle(facing), Axis.Y);
        return location;
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        Direction facing = state.getValue(LinkedButtonBlock.FACING);
        AttachFace face = state.getValue(LinkedButtonBlock.FACE);
        float yRot = AngleHelper.horizontalAngle(facing) + (face != AttachFace.WALL ? 0 : 180);
        float xRot = face == AttachFace.FLOOR ? 90 : face == AttachFace.CEILING ? 270 : 0;
        TransformStack.of(ms)
                .rotateY(yRot)
                .rotateX(xRot);
    }

    @Override
    public float getScale() {
        return .4975f;
    }
}

