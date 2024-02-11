package com.hlysine.create_connected.content.copycat;

import com.hlysine.create_connected.compat.CopycatsManager;
import com.hlysine.create_connected.compat.Mods;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MigratingCopycatBlock extends CopycatBlock {

    public MigratingCopycatBlock(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext pContext) {
        BlockState state = super.getStateForPlacement(pContext);
        assert state != null;
        return Mods.COPYCATS.runIfInstalled(() -> () -> CopycatsManager.convertIfEnabled(state)).orElse(state);
    }
}
