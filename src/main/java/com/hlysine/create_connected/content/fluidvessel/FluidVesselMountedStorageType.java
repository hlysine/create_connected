package com.hlysine.create_connected.content.fluidvessel;


import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FluidVesselMountedStorageType extends MountedFluidStorageType<FluidVesselMountedStorage> {
    public FluidVesselMountedStorageType() {
        super(FluidVesselMountedStorage.CODEC);
    }

    @Override
    @Nullable
    public FluidVesselMountedStorage mount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        if (be instanceof FluidTankBlockEntity tank && tank.isController()) {
            return FluidVesselMountedStorage.fromTank(tank);
        }

        return null;
    }
}
