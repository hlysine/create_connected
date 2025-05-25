package com.hlysine.create_connected.content.kineticbridge;

import com.hlysine.create_connected.content.KineticHelper;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.lang.ref.WeakReference;

public class KineticBridgeDestinationBlockEntity extends GeneratingKineticBlockEntity {

    WeakReference<KineticBridgeBlockEntity> sourceBE = new WeakReference<>(null);
    boolean updateKineticsNextTick = false;

    public KineticBridgeDestinationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    private KineticBridgeBlockEntity getSource() {
        KineticBridgeBlockEntity source = sourceBE.get();
        BlockPos sourcePos = KineticBridgeDestinationBlock.getSource(getBlockPos(), getBlockState());
        if (source != null && !source.isRemoved() && source.getBlockPos().equals(sourcePos)) {
            return source;
        }
        if (getLevel() == null) {
            return null;
        }
        BlockEntity be = getLevel().getBlockEntity(sourcePos);
        if (!(be instanceof KineticBridgeBlockEntity bridgeBE)) {
            return null;
        }
        sourceBE = new WeakReference<>(bridgeBE);
        return bridgeBE;
    }

    @Override
    public void tick() {
        super.tick();
        if (!getLevel().isClientSide()) {
            if (updateKineticsNextTick) {
                KineticHelper.updateKineticBlock(this);
                updateKineticsNextTick = false;
            }
        }
    }

    @Override
    public float getGeneratedSpeed() {
        KineticBridgeBlockEntity source = getSource();
        if (source == null) {
            return 0;
        }
        return source.getSpeed();
    }

    @Override
    public float calculateAddedStressCapacity() {
        float capacity = 0;
        KineticBridgeBlockEntity source = getSource();
        if (source != null) {
            capacity = source.calculateStressApplied();
        }
        this.lastCapacityProvided = capacity;
        return capacity;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate(1);
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putBoolean("UpdateKineticNextTick", updateKineticsNextTick);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        updateKineticsNextTick = compound.getBoolean("UpdateKineticNextTick");
    }
}
