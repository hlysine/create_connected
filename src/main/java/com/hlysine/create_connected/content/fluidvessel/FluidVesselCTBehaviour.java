package com.hlysine.create_connected.content.fluidvessel;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FluidVesselCTBehaviour extends ConnectedTextureBehaviour.Base {

    protected CTSpriteShiftEntry topShift;
    protected CTSpriteShiftEntry layerShift;
    private CTSpriteShiftEntry innerShift;

    public FluidVesselCTBehaviour(CTSpriteShiftEntry layerShift, CTSpriteShiftEntry topShift, CTSpriteShiftEntry innerShift) {
        this.layerShift = layerShift;
        this.topShift = topShift;
        this.innerShift = innerShift;
    }

    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        if (sprite != null && direction.getAxis() == state.getValue(FluidVesselBlock.AXIS) && innerShift.getOriginal() == sprite)
            return innerShift;
        return direction.getAxis() == state.getValue(FluidVesselBlock.AXIS) ? topShift : layerShift;
    }

    @Override
    protected Direction getUpDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
        Direction.Axis vesselAxis = state.getValue(FluidVesselBlock.AXIS);
        boolean alongX = vesselAxis == Direction.Axis.X;
        if (face == Direction.UP && alongX)
            return super.getUpDirection(reader, pos, state, face).getClockWise();
        if (face == Direction.DOWN && alongX)
            return super.getUpDirection(reader, pos, state, face).getCounterClockWise();
        if (face.getAxis() == vesselAxis || face.getAxis().isVertical())
            return super.getUpDirection(reader, pos, state, face);
        return Direction.fromAxisAndDirection(vesselAxis, Direction.AxisDirection.POSITIVE);
    }

    @Override
    protected Direction getRightDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
        Direction.Axis vesselAxis = state.getValue(FluidVesselBlock.AXIS);
        boolean alongX = vesselAxis == Direction.Axis.X;
        if (face == Direction.UP && alongX)
            return super.getRightDirection(reader, pos, state, face).getClockWise();
        if (face == Direction.DOWN && alongX)
            return super.getRightDirection(reader, pos, state, face).getCounterClockWise();
        if (face.getAxis() == vesselAxis || face.getAxis().isVertical())
            return super.getRightDirection(reader, pos, state, face);
        return Direction.fromAxisAndDirection(Direction.Axis.Y, face.getAxisDirection());
    }

    public boolean buildContextForOccludedDirections() {
        return true;
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos,
                              Direction face) {
        return state.getBlock() == other.getBlock() && ConnectivityHandler.isConnected(reader, pos, otherPos);
    }
}
