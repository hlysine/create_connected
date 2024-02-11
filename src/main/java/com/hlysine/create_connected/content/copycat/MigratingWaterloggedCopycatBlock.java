package com.hlysine.create_connected.content.copycat;

import com.hlysine.create_connected.compat.CopycatsManager;
import com.hlysine.create_connected.compat.Mods;
import com.simibubi.create.content.decoration.copycat.WaterloggedCopycatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MigratingWaterloggedCopycatBlock extends WaterloggedCopycatBlock {

    public MigratingWaterloggedCopycatBlock(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext pContext) {
        BlockState state = super.getStateForPlacement(pContext);
        assert state != null;
        return migrate(state);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState pState, @NotNull Direction pDirection, @NotNull BlockState pNeighborState, @NotNull LevelAccessor pLevel, @NotNull BlockPos pCurrentPos, @NotNull BlockPos pNeighborPos) {
        return migrate(super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos));
    }

    protected static BlockState migrate(BlockState state) {
        return Mods.COPYCATS.runIfInstalled(() -> () -> CopycatsManager.convertIfEnabled(state)).orElse(state);
    }

    protected boolean isSelfState(BlockState state) {
        if (state.is(this)) return true;
        return Mods.COPYCATS.runIfInstalled(() -> () -> state.is(CopycatsManager.convertIfEnabled(this))).orElse(false);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (Mods.COPYCATS.runIfInstalled(() -> () -> {
            Block oldBlock = CopycatsManager.convertIfEnabled(pState.getBlock());
            Block newBlock = CopycatsManager.convertIfEnabled(pNewState.getBlock());
            return oldBlock.equals(newBlock);
        }).orElse(false)) return;
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }
}
