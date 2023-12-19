package com.hlysine.create_connected.content;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class ClutchValueBox extends CenteredSideValueBoxTransform {
    public ClutchValueBox() {
        super((state, d) -> {
            Direction.Axis axis = d.getAxis();
            Direction.Axis bearingAxis = state.getValue(FACING).getAxis();
            return bearingAxis != axis;
        });
    }

    @Override
    public void rotate(BlockState state, PoseStack ms) {
        Direction facing = getSide();
        float xRot = facing == Direction.UP ? 90 : facing == Direction.DOWN ? 270 : 0;
        float yRot = AngleHelper.horizontalAngle(facing) + 180;

        if (facing.getAxis() == Direction.Axis.Y)
            TransformStack.cast(ms)
                    .rotateY(180 + AngleHelper.horizontalAngle(state.getValue(FACING)));

        TransformStack.cast(ms)
                .rotateY(yRot)
                .rotateX(xRot);
    }
}
