/*
package com.hlysine.create_connected.content.linkedtransmitter;

import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class LinkedThrottleLeverBlockEntity extends ThrottleLeverBlockEntity {
    public boolean containsBase = true;
    private LinkBehaviour link;

    public LinkedThrottleLeverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        createLink();
        behaviours.add(link);
    }

    @Override
    public void initialize() {
        super.initialize();
        transmit();
    }

    protected void createLink() {
        Pair<ValueBoxTransform, ValueBoxTransform> slots =
                ValueBoxTransform.Dual.makeSlots(LinkedTransmitterFrequencySlot::new);
        link = LinkBehaviour.transmitter(this, slots, this::getState);
    }

    public void transmit() {
        if (link != null)
            link.notifySignalChange();
    }

    @Override
    public void setSignal(int signal) {
        super.setSignal(signal);
        transmit();
        level.setBlock(worldPosition, getBlockState().setValue(BlockStateProperties.POWERED, getState() > 0), Block.UPDATE_ALL);
    }
}
*/