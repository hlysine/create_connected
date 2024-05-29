package com.hlysine.create_connected.content.copycat;

import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class ShimCopycatBlock extends MigratingCopycatBlock implements IShimCopycatBlock {
    public ShimCopycatBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean isUnblockableConnectivitySide(BlockAndTintGetter reader, BlockState state, Direction face, BlockPos fromPos, BlockPos toPos) {
        return IShimCopycatBlock.super.isUnblockableConnectivitySide(reader, state, face, fromPos, toPos);
    }

    @Nullable
    @Override
    public BlockState getConnectiveMaterial(BlockAndTintGetter reader, BlockState fromState, Direction face, BlockPos fromPos, BlockPos toPos) {
        return IShimCopycatBlock.super.getConnectiveMaterial(reader, fromState, face, fromPos, toPos);
    }
}