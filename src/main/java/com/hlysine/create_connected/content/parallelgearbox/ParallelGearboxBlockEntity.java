package com.hlysine.create_connected.content.parallelgearbox;


import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@MethodsReturnNonnullByDefault
public class ParallelGearboxBlockEntity extends SplitShaftBlockEntity {

    public ParallelGearboxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (!hasSource()) return 1;
        return getRotationSpeedModifier(face, getSourceFacing());
    }

    public static float getRotationSpeedModifier(Direction face, Direction source) {
        return face.getAxisDirection() == source.getAxisDirection() ? 1 : -1;
    }
}
