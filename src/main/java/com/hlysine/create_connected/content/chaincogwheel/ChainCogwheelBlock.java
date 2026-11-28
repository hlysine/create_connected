package com.hlysine.create_connected.content.chaincogwheel;

import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.chainDrive.ChainDriveBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ChainCogwheelBlock extends ChainDriveBlock implements ICogWheel {
    public ChainCogwheelBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.ENCASED_CHAIN_COGWHEEL.get();
    }

    @Override
    protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
        if (!super.areStatesKineticallyEquivalent(oldState, newState))
            return false;
        return !chainShapeChanged(oldState, newState);
    }

    private static boolean chainShapeChanged(BlockState previous, BlockState current) {
        if (!(current.getBlock() instanceof ChainCogwheelBlock) || !previous.is(current.getBlock())) {
            return false;
        }
        ChainDriveBlock.Part previousPart = previous.getValue(ChainDriveBlock.PART);
        ChainDriveBlock.Part currentPart = current.getValue(ChainDriveBlock.PART);
        return previousPart != currentPart || (currentPart != ChainDriveBlock.Part.NONE
                && previous.getValue(ChainDriveBlock.CONNECTED_ALONG_FIRST_COORDINATE)
                != current.getValue(ChainDriveBlock.CONNECTED_ALONG_FIRST_COORDINATE));
    }
}
