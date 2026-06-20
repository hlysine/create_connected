package com.hlysine.create_connected.content.kineticbattery;

import com.hlysine.create_connected.ConnectedLang;
import com.hlysine.create_connected.config.CServer;
import com.hlysine.create_connected.content.ISplitShaftBlockEntity;
import com.hlysine.create_connected.datagen.advancements.AdvancementBehaviour;
import com.hlysine.create_connected.datagen.advancements.CCAdvancements;
import com.hlysine.create_connected.mixin.kineticbattery.KineticNetworkAccessor;
import com.hlysine.create_connected.registries.CCDataComponents;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchObservable;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import joptsimple.internal.Strings;
import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Math;

import java.util.Iterator;
import java.util.List;

import static com.hlysine.create_connected.content.kineticbattery.KineticBatteryBlock.*;

public class KineticBatteryBlockEntity extends GeneratingKineticBlockEntity implements ISplitShaftBlockEntity, ThresholdSwitchObservable {

    private static final int SYNC_RATE = 20;

    private double batteryLevel;
    private DataComponentPatch componentPatch = DataComponentPatch.EMPTY;

    private int syncCooldown;
    protected boolean queuedSync;
    private float consumedStress = -1;
    private boolean applyMinStress = false;

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
                new KineticBatteryValueBox(3));
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
        updateLevel();
        updateGeneratedRotation();
    }

    public static double getMaxBatteryLevel() {
        return CServer.BatteryCapacity.get() * 3600 * 20;
    }

    public static int getDischargeRPM() {
        return CServer.BatteryDischargeRPM.get();
    }

    public static int getCrudeBatteryLevel(double level, int totalLevels) {
        if (level >= getMaxBatteryLevel())
            return totalLevels;
        if (level <= 0)
            return 0;
        return (int) Math.floor((level / getMaxBatteryLevel()) * (totalLevels - 1)) + 1;
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
            if (batteryLevel > 0) {
                if (lastCapacityProvided == 0) {
                    calculateAddedStressCapacity();
                }
                if (consumedStress < 0) {
                    updateConsumedStress();
                }
                batteryLevel = Math.max(batteryLevel - getConsumedStress(), 0);
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

    @Override
    public void updateFromNetwork(float maxStress, float currentStress, int networkSize) {
        super.updateFromNetwork(maxStress, currentStress, networkSize);
        updateConsumedStress();
    }

    private void updateConsumedStress() {
        KineticNetwork network = getOrCreateNetwork();

        float presentCapacity = 0;
        int batteryCount = 0;
        for (Iterator<KineticBlockEntity> iterator = network.sources.keySet().iterator(); iterator.hasNext(); ) {
            KineticBlockEntity be = iterator.next();
            if (be.getLevel().getBlockEntity(be.getBlockPos()) != be) {
                iterator.remove();
                continue;
            }
            if (be instanceof KineticBatteryBlockEntity) {
                batteryCount += 1;
                continue;
            }
            presentCapacity += network.getActualCapacityOf(be);
        }
        float batteryCapacity = stress - presentCapacity - ((KineticNetworkAccessor) network).getUnloadedStress();

        applyMinStress = false;
        if (stress <= 0) {
            for (Iterator<KineticBlockEntity> iterator = network.members.keySet().iterator(); iterator.hasNext(); ) {
                KineticBlockEntity be = iterator.next();
                if (be.getLevel().getBlockEntity(be.getBlockPos()) != be) {
                    iterator.remove();
                    continue;
                }
                if (BeltBlock.canTransportObjects(be.getBlockState())) {
                    applyMinStress = true;
                    break;
                }
            }
        }

        if (batteryCapacity <= 0) {
            consumedStress = 0;
        } else {
            consumedStress = batteryCapacity / batteryCount;
        }
        sendDataImmediately();
    }

    public float getConsumedStress() {
        if (applyMinStress) {
            return Math.max(CServer.BatteryMinDischarge.get().floatValue(), consumedStress);
        }
        return Math.max(0, consumedStress);
    }

    private void updateLevel() {
        int crudeLevel = getCrudeBatteryLevel(getBatteryLevel(), 5);
        int oldLevel = getBlockState().getValue(LEVEL);
        if (oldLevel != crudeLevel) {
            if (crudeLevel == 5) {
                AdvancementBehaviour.tryAward(this, CCAdvancements.KINETIC_BATTERY);
            }
            switchToBlockState(getLevel(), getBlockPos(), getBlockState().setValue(LEVEL, crudeLevel));
        }
        sendData();
    }

    public WindmillBearingBlockEntity.RotationDirection getRotationDirection() {
        return movementDirection.get();
    }

    public void setRotationDirection(WindmillBearingBlockEntity.RotationDirection direction) {
        movementDirection.setValue(direction.ordinal());
    }

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(double batteryLevel) {
        this.batteryLevel = batteryLevel;
        updateLevel();
        sendDataImmediately();
    }

    public void setComponentPatch(DataComponentPatch componentPatch) {
        this.componentPatch = componentPatch;
    }

    public DataComponentPatch getComponentPatch() {
        return componentPatch;
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        setBatteryLevel(componentInput.getOrDefault(CCDataComponents.KINETIC_BATTERY_CHARGE, 0.0));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        components.set(CCDataComponents.KINETIC_BATTERY_CHARGE, getBatteryLevel());
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
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        batteryLevel = compound.getFloat("batteryLevel");
        queuedSync = compound.getBoolean("queuedSync");
        consumedStress = compound.getFloat("consumedStress");
        applyMinStress = compound.getBoolean("applyMinStress");
        componentPatch = CatnipCodecUtils.decode(DataComponentPatch.CODEC, registries, compound.getCompound("Components")).orElse(DataComponentPatch.EMPTY);
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putDouble("batteryLevel", batteryLevel);
        compound.putBoolean("queuedSync", queuedSync);
        compound.putFloat("consumedStress", consumedStress);
        compound.putBoolean("applyMinStress", applyMinStress);
        compound.put("Components", CatnipCodecUtils.encode(DataComponentPatch.CODEC, registries, componentPatch).orElse(new CompoundTag()));
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        ConnectedLang.translate("battery.status", getBatteryStatusTextComponent().withStyle(ChatFormatting.GREEN))
                .forGoggles(tooltip);
        ConnectedLang.builder().add(ConnectedLang.translateDirect("battery.charge")
                        .withStyle(ChatFormatting.GRAY)
                        .append(" ")
                        .append(barComponent(0, getCrudeBatteryLevel(getBatteryLevel(), 20), 20)))
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
        if (isDischarging(getBlockState()) && getBatteryLevel() > 0) {
            ConnectedLang.translate("battery.consumption")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
            if (consumedStress == 0 && getConsumedStress() > 0) {
                CreateLang.number(getConsumedStress())
                        .translate("generic.unit.stress")
                        .style(ChatFormatting.BLUE)
                        .space()
                        .add(ConnectedLang.translate("battery.powering_belts").style(ChatFormatting.DARK_GRAY))
                        .forGoggles(tooltip, 1);
            } else {
                CreateLang.number(getConsumedStress())
                        .translate("generic.unit.stress")
                        .style(ChatFormatting.BLUE)
                        .forGoggles(tooltip, 1);
            }
        }


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

    static MutableComponent barComponent(int minValue, int level, int maxValue) {
        return Component.empty()
                .append(bars(java.lang.Math.max(0, minValue - 1), ChatFormatting.DARK_GREEN))
                .append(bars(minValue > 0 ? 1 : 0, ChatFormatting.GREEN))
                .append(bars(java.lang.Math.max(0, level - minValue), ChatFormatting.DARK_GREEN))
                .append(bars(java.lang.Math.max(0, maxValue - level), ChatFormatting.DARK_RED))
                .append(bars(java.lang.Math.max(0, java.lang.Math.min(18 - maxValue, ((maxValue / 5 + 1) * 5) - maxValue)),
                        ChatFormatting.DARK_GRAY));

    }

    static MutableComponent bars(int level, ChatFormatting format) {
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

