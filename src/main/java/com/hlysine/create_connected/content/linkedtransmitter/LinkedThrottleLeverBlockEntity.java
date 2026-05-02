package com.hlysine.create_connected.content.linkedtransmitter;

import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class LinkedThrottleLeverBlockEntity extends ThrottleLeverBlockEntity {

    private int transmittedSignal;
    /**
     * set to false if the module item is already returned to player via wrenching
     */
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
        sendData();
    }

    private int lastChange() {
        return this.lastChange;
    }

    @Override
    public void tick() {
        int prevTick = lastChange();
        super.tick();
        if (!level.isClientSide && prevTick > 0 && lastChange() == 0) {
            transmit(getState());
        }
        if (level.isClientSide && prevTick > 0 && lastChange() == 0) {
            // todo: desync between server and client, but setblock on server resets BE
            level.setBlock(worldPosition, getBlockState().setValue(BlockStateProperties.POWERED, getState() > 0), 0);
        }
    }

    @Override
    public void remove() {
        super.remove();
        transmit(0);
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("Transmit", transmittedSignal);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (level == null || level.isClientSide || !link.newPosition)
            transmittedSignal = compound.getInt("Transmit");
    }
}
