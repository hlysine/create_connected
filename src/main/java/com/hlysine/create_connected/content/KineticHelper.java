package com.hlysine.create_connected.content;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class KineticHelper {
    public static void updateKineticBlock(KineticBlockEntity kineticTE) {
        if (kineticTE.hasNetwork())
            kineticTE.getOrCreateNetwork().remove(kineticTE);
        kineticTE.detachKinetics();
        kineticTE.removeSource();
        BlockState state = kineticTE.getBlockState();
        BlockPos pos = kineticTE.getBlockPos();
        Level level = Objects.requireNonNull(kineticTE.getLevel());
        level.markAndNotifyBlock(pos, level.getChunkAt(pos), state, state, 3, 512);
        if (kineticTE instanceof GeneratingKineticBlockEntity generatingBlockEntity) {
            generatingBlockEntity.reActivateSource = true;
        }
    }
}
