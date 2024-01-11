package com.hlysine.create_connected.content.contraption.menu;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiFunction;

public class TrackingContainerLevelAccess implements ContainerLevelAccess {
    private final Level level;
    private final AbstractContraptionEntity entity;
    private final BlockPos localPos;

    public TrackingContainerLevelAccess(Level level, AbstractContraptionEntity entity, BlockPos localPos) {
        this.level = level;
        this.entity = entity;
        this.localPos = localPos;
    }

    @Override
    public <T> @NotNull Optional<T> evaluate(BiFunction<Level, BlockPos, T> provideLevelPos) {
        BlockPos realPos = BlockPos.containing(entity.toGlobalVector(Vec3.atCenterOf(localPos), 1));
        return Optional.of(provideLevelPos.apply(level, realPos));
    }
}
