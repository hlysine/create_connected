package com.hlysine.create_connected.content.fancatalyst;

import com.hlysine.create_connected.CCBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class FanEndingCatalystDragonHeadBlock extends Block implements IBE<FanEndingCatalystDragonHeadBlockEntity>, IWrenchable {

    public FanEndingCatalystDragonHeadBlock(Properties properties) {
        super(properties);
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
