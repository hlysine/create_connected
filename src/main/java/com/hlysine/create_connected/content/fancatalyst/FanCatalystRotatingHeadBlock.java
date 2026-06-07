package com.hlysine.create_connected.content.fancatalyst;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FanCatalystRotatingHeadBlock extends Block implements IBE<FanCatalystRotatingHeadBlockEntity>, IWrenchable {
    private final BlockEntityEntry<? extends FanCatalystRotatingHeadBlockEntity> blockEntityType;

    public FanCatalystRotatingHeadBlock(Properties properties, BlockEntityEntry<? extends FanCatalystRotatingHeadBlockEntity> blockEntityType) {
        super(properties);
        this.blockEntityType = blockEntityType;
    }

    @Override
    public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level level, BlockState state, BlockEntityType<S> type) {
        return null;
    }

    @Override
    public Class<FanCatalystRotatingHeadBlockEntity> getBlockEntityClass() {
        return FanCatalystRotatingHeadBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FanCatalystRotatingHeadBlockEntity> getBlockEntityType() {
        return blockEntityType.get();
    }
}
