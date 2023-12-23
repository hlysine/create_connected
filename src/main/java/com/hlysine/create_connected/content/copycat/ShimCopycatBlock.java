package com.hlysine.create_connected.content.copycat;

import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class ShimCopycatBlock extends CopycatBlock {
    public ShimCopycatBlock(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockState getConnectiveMaterial(BlockAndTintGetter reader, BlockState otherState, Direction face, BlockPos fromPos, BlockPos toPos) {
        BlockState selfState = reader.getBlockState(toPos);
        if (!canConnectTexturesToward(reader, fromPos, toPos, selfState))
            return null;

        if (isIgnoredConnectivitySide(reader, selfState, face, fromPos, toPos))
            return null;
        return getMaterial(reader, toPos);
    }

    protected abstract boolean canConnectTexturesToward(BlockAndTintGetter reader, BlockPos fromPos, BlockPos toPos, BlockState selfState);
}
