package com.hlysine.create_connected.mixin.contraption;

import com.hlysine.create_connected.compat.ModMixin;
import com.hlysine.create_connected.content.contraption.menu.TrackingContainerLevelAccess;
import com.hlysine.create_connected.content.contraption.menu.TrackingContraptionWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;
import java.util.function.BiFunction;

@ModMixin(mods = {"railways"}, applyIfPresent = false)
@Mixin(ContainerLevelAccess.class)
public interface ContainerLevelAccessMixin {
    /**
     * @author hlysine
     * @reason Special handling has to be added if the level is a contraption world, but @Inject is not supported for interface static methods.
     */
    @Overwrite
    static ContainerLevelAccess create(final Level pLevel, final BlockPos pPos) {
        if (pLevel instanceof TrackingContraptionWorld world) {
            return new TrackingContainerLevelAccess(world.getLevel(), world.contraption.entity, world.localPos);
        }
        return new ContainerLevelAccess() {
            public <T> @NotNull Optional<T> evaluate(@NotNull BiFunction<Level, BlockPos, T> pLevelPosConsumer) {
                return Optional.of(pLevelPosConsumer.apply(pLevel, pPos));
            }
        };
    }
}
