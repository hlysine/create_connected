package com.hlysine.create_connected.content.chaincogwheel;

import com.hlysine.create_connected.CCBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.chainDrive.ChainDriveBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ChainCogwheelBlock extends ChainDriveBlock implements ICogWheel {
    public ChainCogwheelBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.ENCASED_CHAIN_COGWHEEL.get();
    }
}
