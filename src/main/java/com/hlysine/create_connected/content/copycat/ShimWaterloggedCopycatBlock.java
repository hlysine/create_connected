package com.hlysine.create_connected.content.copycat;

import com.simibubi.create.content.decoration.copycat.WaterloggedCopycatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class ShimWaterloggedCopycatBlock extends WaterloggedCopycatBlock {
    public ShimWaterloggedCopycatBlock(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockState getConnectiveMaterial(BlockAndTintGetter reader, BlockState otherState, Direction face, BlockPos fromPos, BlockPos toPos) {
        BlockState selfState = reader.getBlockState(toPos);
        if (!canConnectTexturesToward(reader, fromPos, toPos, selfState))
            return null;

        if (isIgnoredConnectivitySide(reader, selfState, face, toPos, fromPos))
            return null;

        return getMaterial(reader, toPos);
    }

    protected abstract boolean canConnectTexturesToward(BlockAndTintGetter reader, BlockPos fromPos, BlockPos toPos, BlockState selfState);
}
