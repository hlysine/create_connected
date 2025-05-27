package com.hlysine.create_connected.content.fancatalyst;

import com.hlysine.create_connected.CCBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FanEndingCatalystDragonHeadBlock extends Block implements IBE<FanEndingCatalystDragonHeadBlockEntity>, IWrenchable {

    public FanEndingCatalystDragonHeadBlock(Properties properties) {
        super(properties);
    }

    @Override
    public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level level, BlockState state, BlockEntityType<S> type) {
        return null;
    }

    @Override
    public Class<FanEndingCatalystDragonHeadBlockEntity> getBlockEntityClass() {
        return FanEndingCatalystDragonHeadBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FanEndingCatalystDragonHeadBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.FAN_ENDING_CATALYST_DRAGON_HEAD.get();
    }
}
