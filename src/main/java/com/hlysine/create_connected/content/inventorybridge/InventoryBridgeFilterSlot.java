package com.hlysine.create_connected.content.inventorybridge;


import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class InventoryBridgeFilterSlot extends CenteredSideValueBoxTransform {

    public InventoryBridgeFilterSlot() {
        super((state, d) -> state.getValue(InventoryBridgeBlock.AXIS) == d.getAxis());
    }

    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        Vec3 location = getSouthLocation();
        if (getSide() == Direction.UP) {
            location = new Vec3(location.x, 1 - location.y, location.z);
        }
        location = VecHelper.rotateCentered(location, AngleHelper.horizontalAngle(getSide()), Direction.Axis.Y);
        location = VecHelper.rotateCentered(location, AngleHelper.verticalAngle(getSide()), Direction.Axis.X);
        return location;
    }


    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        super.rotate(level, pos,state, ms);
        if (getSide() == Direction.UP)
            TransformStack.of(ms)
                    .rotateZ(180);
    }

    @Override
    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace(8, 15.5, 15.5);
    }
}