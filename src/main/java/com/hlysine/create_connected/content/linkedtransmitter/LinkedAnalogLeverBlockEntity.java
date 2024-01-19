package com.hlysine.create_connected.content.linkedtransmitter;

import com.hlysine.create_connected.mixin.linkedtransmitter.AnalogLeverBlockEntityAccessor;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlockEntity;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class LinkedAnalogLeverBlockEntity extends AnalogLeverBlockEntity {

    private int transmittedSignal;
    /**
     * set to false if the module item is already returned to player via wrenching
     */
    public boolean containsBase = true;
    private LinkBehaviour link;

    public LinkedAnalogLeverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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
        transmit(getState());
    }

    protected void createLink() {
        Pair<ValueBoxTransform, ValueBoxTransform> slots =
                ValueBoxTransform.Dual.makeSlots(LinkedTransmitterFrequencySlot::new);
        link = LinkBehaviour.transmitter(this, slots, this::getSignal);
    }

    public int getSignal() {
        return transmittedSignal;
    }

    public void transmit(int strength) {
        transmittedSignal = strength;
        if (link != null)
            link.notifySignalChange();
    }

    private int lastChange() {
        return ((AnalogLeverBlockEntityAccessor) this).getLastChange();
    }

    @Override
    public void tick() {
        int prevTick = lastChange();
        super.tick();
        if (prevTick > 0 && lastChange() == 0) {
            transmit(getState());
            level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(BlockStateProperties.POWERED, getState() > 0));
        }
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.putInt("Transmit", transmittedSignal);
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        if (level == null || level.isClientSide || !link.newPosition)
            transmittedSignal = compound.getInt("Transmit");
    }

}
