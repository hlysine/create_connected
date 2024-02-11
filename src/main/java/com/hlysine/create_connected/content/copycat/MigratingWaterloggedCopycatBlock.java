package com.hlysine.create_connected.content.copycat;

import com.hlysine.create_connected.compat.CopycatsManager;
import com.hlysine.create_connected.compat.Mods;
import com.simibubi.create.content.decoration.copycat.WaterloggedCopycatBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
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
        return Mods.COPYCATS.runIfInstalled(() -> () -> CopycatsManager.convertIfEnabled(state)).orElse(state);
    }

    protected boolean isSelfState(BlockState state) {
        if (state.is(this)) return true;
        return Mods.COPYCATS.runIfInstalled(() -> () -> state.is(CopycatsManager.convertIfEnabled(this))).orElse(false);
    }
}
