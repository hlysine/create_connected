package com.hlysine.create_connected.content.kineticbattery;

import com.hlysine.create_connected.ConnectedLang;
import com.hlysine.create_connected.config.CServer;
import com.hlysine.create_connected.content.ISplitShaftBlockEntity;
import com.hlysine.create_connected.datagen.advancements.AdvancementBehaviour;
import com.hlysine.create_connected.datagen.advancements.CCAdvancements;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchObservable;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import joptsimple.internal.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Math;

import java.util.List;

import static com.hlysine.create_connected.content.kineticbattery.KineticBatteryBlock.*;

public class KineticBatteryBlockEntity extends GeneratingKineticBlockEntity implements ISplitShaftBlockEntity, ThresholdSwitchObservable {

    private static final int SYNC_RATE = 20;
    public static final double CHARGE_THRESHOlD = 3600 * 20;

    private double batteryLevel;

    private int syncCooldown;
    protected boolean queuedSync;

    protected ScrollOptionBehaviour<WindmillBearingBlockEntity.RotationDirection> movementDirection;

    public KineticBatteryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        movementDirection = new ScrollOptionBehaviour<>(WindmillBearingBlockEntity.RotationDirection.class,
                ConnectedLang.translateDirect("battery.rotation_direction"),
                this,
                new KineticBatteryValueBox());
        movementDirection.withCallback(i -> {
            updateGeneratedRotation();
            sendDataImmediately();
        });
        behaviours.add(movementDirection);
        AdvancementBehaviour.registerAwardables(this, behaviours, CCAdvancements.KINETIC_BATTERY);
    }

    @Override
    public void initialize() {
        super.initialize();
        updateGeneratedRotation();
    }

    public static double getMaxBatteryLevel() {
        return CServer.BatteryCapacity.get() * 3600 * 20;
    }

    public static int getDischargeRPM() {
        return CServer.BatteryDischargeRPM.get();
    }

    public int getCrudeBatteryLevel(int totalLevels) {
        if (batteryLevel >= getMaxBatteryLevel())
            return totalLevels;
        if (batteryLevel <= 0)
            return 0;
        return (int) Math.floor((batteryLevel / getMaxBatteryLevel()) * (totalLevels - 1)) + 1;
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
            if (batteryLevel < getMaxBatteryLevel() && capacity > 0) {
                if (lastStressApplied == 0) {
                    calculateStressApplied();
                }
                batteryLevel = Math.min(batteryLevel + lastStressApplied * Math.abs(getTheoreticalSpeed()), getMaxBatteryLevel());
                changed = true;
            }
        }
        if (changed)
            updateLevel();
    }

    private void updateLevel() {
        int crudeLevel = getCrudeBatteryLevel(5);
        int oldLevel = getBlockState().getValue(LEVEL);
        if (oldLevel != crudeLevel) {
            if (crudeLevel == 5) {
                AdvancementBehaviour.tryAward(this, CCAdvancements.KINETIC_BATTERY);
            }
            switchToBlockState(getLevel(), getBlockPos(), getBlockState().setValue(LEVEL, crudeLevel));
        }
        sendData();
    }

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(double batteryLevel) {
        this.batteryLevel = batteryLevel;
        updateLevel();
        sendDataImmediately();
    }

    public void sendDataImmediately() {
        syncCooldown = 0;
        queuedSync = false;
        sendData();
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
        return convertToDirection(getDischargeRPM(), getBlockState().getValue(FACING)) *
                (movementDirection.get() == WindmillBearingBlockEntity.RotationDirection.CLOCKWISE ? -1 : 1);
    }

    @Override
    public float calculateAddedStressCapacity() {
        if (!isDischarging(getBlockState()) || isCurrentStageComplete(getBlockState()))
            return 0;
        return super.calculateAddedStressCapacity();
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
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        batteryLevel = compound.getFloat("batteryLevel");
        queuedSync = compound.getBoolean("queuedSync");
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putDouble("batteryLevel", batteryLevel);
        compound.putBoolean("queuedSync", queuedSync);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        ConnectedLang.translate("battery.status", getBatteryStatusTextComponent().withStyle(ChatFormatting.GREEN))
                .forGoggles(tooltip);
        ConnectedLang.builder().add(ConnectedLang.translateDirect("battery.charge")
                        .withStyle(ChatFormatting.GRAY)
                        .append(" ")
                        .append(barComponent(0, getCrudeBatteryLevel(20), 20)))
                .forGoggles(tooltip);
        ConnectedLang.number(batteryLevel / 3600 / 20)
                .style(ChatFormatting.BLUE)
                .add(ConnectedLang.text(" / ")
                        .style(ChatFormatting.GRAY))
                .add(ConnectedLang.number(getMaxBatteryLevel() / 3600 / 20)
                        .add(Component.literal(" "))
                        .add(ConnectedLang.translate("generic.unit.su_hours"))
                        .style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);


        super.addToGoggleTooltip(tooltip, isPlayerSneaking);

        return true;
    }

    public MutableComponent getBatteryStatusTextComponent() {
        boolean complete = isCurrentStageComplete(getBlockState());
        boolean discharging = isDischarging(getBlockState());

        if (discharging && !complete) {
            return ConnectedLang.translateDirect("battery.status.discharging");
        } else if (!discharging && !complete) {
            return ConnectedLang.translateDirect("battery.status.charging");
        } else if (!discharging && complete) {
            return ConnectedLang.translateDirect("battery.status.full");
        } else {
            return ConnectedLang.translateDirect("battery.status.empty");
        }
    }

    private MutableComponent barComponent(int minValue, int level, int maxValue) {
        return Component.empty()
                .append(bars(java.lang.Math.max(0, minValue - 1), ChatFormatting.DARK_GREEN))
                .append(bars(minValue > 0 ? 1 : 0, ChatFormatting.GREEN))
                .append(bars(java.lang.Math.max(0, level - minValue), ChatFormatting.DARK_GREEN))
                .append(bars(java.lang.Math.max(0, maxValue - level), ChatFormatting.DARK_RED))
                .append(bars(java.lang.Math.max(0, java.lang.Math.min(18 - maxValue, ((maxValue / 5 + 1) * 5) - maxValue)),
                        ChatFormatting.DARK_GRAY));

    }

    private MutableComponent bars(int level, ChatFormatting format) {
        return Component.literal(Strings.repeat('|', level))
                .withStyle(format);
    }

    @Override
    public int getMaxValue() {
        return (int) (getMaxBatteryLevel() / 3600.0 / 20.0);
    }

    @Override
    public int getMinValue() {
        return 0;
    }

    @Override
    public int getCurrentValue() {
        return (int) (batteryLevel / 3600.0 / 20.0);
    }

    @Override
    public MutableComponent format(int value) {
        return ConnectedLang.number(value)
                .add(Component.literal(" "))
                .add(ConnectedLang.translate("generic.unit.su_hours"))
                .component();
    }
}

