package com.hlysine.create_connected.content.linkedtransmitter;

import com.hlysine.create_connected.mixin.linkedtransmitter.AnalogLeverBlockEntityAccessor;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlockEntity;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class LinkedAnalogLeverBlockEntity extends AnalogLeverBlockEntity {
    /**
     * set to false if the module item is already returned to player via wrenching
     */
    public boolean containsBase = true;
    private LinkBehaviour link;

    public LinkedAnalogLeverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehavioursDeferred(List<BlockEntityBehaviour> behaviours) {
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

    private int lastChange() {
        return ((AnalogLeverBlockEntityAccessor) this).getLastChange();
    }

    private LerpedFloat getClientState() {
        return ((AnalogLeverBlockEntityAccessor) this).getClientState();
    }

    @Override
    public void tick() {
        int prevTick = lastChange();
        super.tick();
        if (prevTick > 0 && lastChange() == 0) {
            if (!level.isClientSide)
                transmit();
            level.setBlock(worldPosition, getBlockState().setValue(BlockStateProperties.POWERED, getClientState().getValue() > 0.1), Block.UPDATE_ALL);
        }
    }
}
