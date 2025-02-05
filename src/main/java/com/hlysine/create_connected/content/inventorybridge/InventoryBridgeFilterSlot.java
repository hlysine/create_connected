package com.hlysine.create_connected.content.inventorybridge;


import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class InventoryBridgeFilterSlot extends CenteredSideValueBoxTransform {

    public InventoryBridgeFilterSlot() {
        super((state, d) -> state.getValue(InventoryBridgeBlock.AXIS) == d.getAxis());
    }

    @Override
    public Vec3 getLocalOffset(BlockState state) {
        Vec3 location = getSouthLocation();
        if (getSide() == Direction.UP) {
            location = new Vec3(location.x, 1 - location.y, location.z);
        }
        location = VecHelper.rotateCentered(location, AngleHelper.horizontalAngle(getSide()), Direction.Axis.Y);
        location = VecHelper.rotateCentered(location, AngleHelper.verticalAngle(getSide()), Direction.Axis.X);
        return location;
    }


    @Override
    public void rotate(BlockState state, PoseStack ms) {
        super.rotate(state, ms);
        if (getSide() == Direction.UP)
            TransformStack.cast(ms)
                    .rotateZ(180);
    }

    @Override
    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace(8, 15.5, 15.5);
    }
}