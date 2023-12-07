package com.hlysine.create_connected.content.brassgearbox;


import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import static com.hlysine.create_connected.content.brassgearbox.BrassGearboxBlock.getFaceId;
import static com.hlysine.create_connected.content.brassgearbox.BrassGearboxBlock.isFaceFlipped;

@MethodsReturnNonnullByDefault
public class BrassGearboxBlockEntity extends SplitShaftBlockEntity {

    public BrassGearboxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (!hasSource()) return 1;
        return getRotationSpeedModifier(face, getSourceFacing(), getBlockState());
    }

    public static float getRotationSpeedModifier(Direction face, Direction source, BlockState state) {
        int axisAdjust = face.getAxisDirection() == source.getAxisDirection() ? 1 : -1;
        int currentFace = getFaceId(face, state.getValue(BrassGearboxBlock.AXIS));
        int sourceFace = getFaceId(source, state.getValue(BrassGearboxBlock.AXIS));
        int currentFaceFlipped = isFaceFlipped(currentFace, state) ? -1 : 1;
        int sourceFaceFlipped = isFaceFlipped(sourceFace, state) ? -1 : 1;
        return axisAdjust * currentFaceFlipped * sourceFaceFlipped;
    }
}
