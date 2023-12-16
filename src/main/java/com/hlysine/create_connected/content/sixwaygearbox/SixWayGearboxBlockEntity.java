package com.hlysine.create_connected.content.sixwaygearbox;


import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.core.Direction.*;

@MethodsReturnNonnullByDefault
public class SixWayGearboxBlockEntity extends SplitShaftBlockEntity {

    public SixWayGearboxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (!hasSource()) return 1;
        return getRotationSpeedModifier(getBlockState(), face, getSourceFacing());
    }

    public static float getRotationSpeedModifier(BlockState state, Direction face, Direction source) {
        float modifier = face.getAxisDirection() == source.getAxisDirection() ? 1 : -1;
        Axis axis = state.getValue(SixWayGearboxBlock.AXIS);
        if ((face.getAxis() == axis) != (source.getAxis() == axis)) {
            modifier *= face.getAxis() == axis ? -0.5 : -2;
        }
        return modifier;
    }
}
