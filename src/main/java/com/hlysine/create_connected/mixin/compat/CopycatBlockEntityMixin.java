package com.hlysine.create_connected.mixin.compat;

import com.hlysine.create_connected.compat.CopycatsManager;
import com.hlysine.create_connected.compat.Mods;
import com.hlysine.create_connected.config.CCConfigs;
import com.simibubi.create.content.decoration.copycat.CopycatBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CopycatBlockEntity.class)
public abstract class CopycatBlockEntityMixin extends SmartBlockEntity {
    public CopycatBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void setLevel(@NotNull Level pLevel) {
        super.setLevel(pLevel);

        if (!pLevel.isClientSide() && CCConfigs.common().migrateCopycatsOnInitialize.get())
            Mods.COPYCATS.executeIfInstalled(() -> () -> {
                BlockState state = CopycatsManager.convert(getBlockState());
                if (!state.getBlock().equals(getBlockState().getBlock()))
                    CopycatsManager.enqueueMigration(pLevel, getBlockPos());
            });
    }
}
