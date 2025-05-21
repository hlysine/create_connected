package com.hlysine.create_connected.content.kineticbattery;

import com.hlysine.create_connected.content.ISplitShaftBlockEntity;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Math;

import static com.hlysine.create_connected.content.kineticbattery.KineticBatteryBlock.*;

public class KineticBatteryBlockEntity extends GeneratingKineticBlockEntity implements ISplitShaftBlockEntity {

    public static final int DEFAULT_SPEED = 64;
    public static final float MAX_BATTERY_LEVEL = 64 * 8 * 20 * 5; // 5 seconds for debugging
    //    public static final float MAX_BATTERY_LEVEL = 128 * 3600 * 20; // 128 su-hours, expressed in su-ticks
    private static final int SYNC_RATE = 0; // continuous sync for debugging

    private float batteryLevel;

    private int syncCooldown;
    protected boolean queuedSync;

    public KineticBatteryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void initialize() {
        super.initialize();
        updateGeneratedRotation();
    }

    public int getCrudeBatteryLevel() {
        if (batteryLevel >= MAX_BATTERY_LEVEL)
            return 5;
        if (batteryLevel <= 0)
            return 0;
        return (int) Math.floor((batteryLevel / MAX_BATTERY_LEVEL) * 4) + 1;
    }

    @Override
    public void tick() {
        super.tick();

        if (syncCooldown > 0) {
            syncCooldown--;
            if (syncCooldown == 0 && queuedSync)
                sendData();
        }

        if (getSpeed() == 0 || !hasNetwork())
            return;
        boolean changed = false;
        if (isDischarging(getBlockState())) {
            if (batteryLevel > 0 && stress > 0) {
                if (lastCapacityProvided == 0) {
                    calculateAddedStressCapacity();
                }
                batteryLevel = Math.max(batteryLevel - lastCapacityProvided * Math.abs(getGeneratedSpeed()), 0);
                changed = true;
            }
        } else {
            if (batteryLevel < MAX_BATTERY_LEVEL && capacity > 0) {
                if (lastStressApplied == 0) {
                    calculateStressApplied();
                }
                batteryLevel = Math.min(batteryLevel + lastStressApplied * Math.abs(getTheoreticalSpeed()), MAX_BATTERY_LEVEL);
                changed = true;
            }
        }
        if (changed) {
            int crudeLevel = getCrudeBatteryLevel();
            int oldLevel = getBlockState().getValue(LEVEL);
            if (oldLevel != crudeLevel) {
                switchToBlockState(getLevel(), getBlockPos(), getBlockState().setValue(LEVEL, crudeLevel));
            }
            sendData();
        }
    }

    @Override
    public void sendData() {
        if (syncCooldown > 0) {
            queuedSync = true;
            return;
        }
        super.sendData();
        queuedSync = false;
        syncCooldown = SYNC_RATE;
    }

    @Override
    public float getGeneratedSpeed() {
        if (!isDischarging(getBlockState()) || isCurrentStageComplete(getBlockState()))
            return 0;
        return convertToDirection(DEFAULT_SPEED, getBlockState().getValue(FACING));
    }

    @Override
    public float calculateStressApplied() {
        if (!isDischarging(getBlockState()) && !isCurrentStageComplete(getBlockState())) {
            return super.calculateStressApplied();
        } else {
            this.lastStressApplied = 0;
            return 0;
        }
    }

    public float getRotationSpeedModifier(Direction face) {
        if (face.getAxis() != getBlockState().getValue(FACING).getAxis())
            return 0;
        if (face != getBlockState().getValue(FACING)) {
            if (!isCurrentStageComplete(getBlockState()))
                return 0;
        }
        return 1;
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        batteryLevel = compound.getFloat("batteryLevel");
        queuedSync = compound.getBoolean("queuedSync");
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putFloat("batteryLevel", batteryLevel);
        compound.putBoolean("queuedSync", queuedSync);
    }
}

