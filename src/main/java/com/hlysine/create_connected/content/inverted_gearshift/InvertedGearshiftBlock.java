package com.hlysine.create_connected.content.inverted_gearshift;

import com.hlysine.create_connected.CCBlockEntityTypes;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.kinetics.transmission.GearshiftBlock;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class InvertedGearshiftBlock extends GearshiftBlock {

    public InvertedGearshiftBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends SplitShaftBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.INVERTED_GEARSHIFT.get();
    }
}

