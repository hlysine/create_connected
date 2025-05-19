package com.hlysine.create_connected.content.copycat;

import com.hlysine.create_connected.CCBlockEntityTypes;
import com.hlysine.create_connected.compat.CopycatsManager;
import com.hlysine.create_connected.compat.Mods;
import com.hlysine.create_connected.config.CCConfigs;
import com.simibubi.create.content.decoration.copycat.CopycatBlockEntity;
import com.simibubi.create.content.decoration.copycat.WaterloggedCopycatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        return migrateOnUpdate(pLevel.isClientSide(), super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos));
    }

    protected static BlockState migrateOnUpdate(boolean isClient, BlockState state) {
        if (!isClient && CCConfigs.common().migrateCopycatsOnBlockUpdate.get())
            return migrate(state);
        return state;
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

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState pState, LootParams.@NotNull Builder pParams) {
        List<ItemStack> drops = super.getDrops(pState, pParams);
        return Mods.COPYCATS.runIfInstalled(() -> () -> {
            for (int i = 0; i < drops.size(); i++) {
                ItemStack drop = drops.get(i);
                Item converted = CopycatsManager.convert(drop.getItem());
                if (!converted.equals(drop.getItem())) {
                    drops.set(i, new ItemStack(converted, drop.getCount()));
                }
            }
            return drops;
        }).orElse(drops);
    }

    @Override
    public BlockEntityType<? extends CopycatBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.COPYCAT.get();
    }
}
