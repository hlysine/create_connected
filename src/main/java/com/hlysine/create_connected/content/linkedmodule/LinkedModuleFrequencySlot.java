package com.hlysine.create_connected.content.linkedmodule;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.Vec3;

public class LinkedModuleFrequencySlot extends ValueBoxTransform.Dual {

    public LinkedModuleFrequencySlot(boolean first) {
        super(first);
    }

    @Override
    public Vec3 getLocalOffset(BlockState state) {
        Direction facing = state.getValue(LinkedButtonBlock.FACING);
        AttachFace face = state.getValue(LinkedButtonBlock.FACE);

        Vec3 location = switch (face) {
            case FLOOR -> VecHelper.voxelSpace(10.5f, 1.1f, 2f);
            case WALL -> VecHelper.voxelSpace(5.5f, 2f, 1.1f);
            case CEILING -> VecHelper.voxelSpace(5.5f, 14.9f, 2f);
        };
        if (!isFirst()) {
            location = location.add(5 / 16f * (face != AttachFace.FLOOR ? 1 : -1), 0, 0);
        }
        location = VecHelper.rotateCentered(location, AngleHelper.horizontalAngle(facing), Axis.Y);
        return location;
    }

    @Override
    public void rotate(BlockState state, PoseStack ms) {
        Direction facing = state.getValue(LinkedButtonBlock.FACING);
        AttachFace face = state.getValue(LinkedButtonBlock.FACE);
        float yRot = AngleHelper.horizontalAngle(facing) + 180;
        float xRot = face == AttachFace.FLOOR ? 90 : face == AttachFace.CEILING ? 270 : 0;
        TransformStack.cast(ms)
                .rotateY(yRot)
                .rotateX(xRot);
    }

    @Override
    public float getScale() {
        return .4975f;
    }
}

