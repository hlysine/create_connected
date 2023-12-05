package com.hlysine.create_connected.content.overstressclutch;

import com.hlysine.create_connected.content.overstressclutch.OverstressClutchBlock.ClutchState;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import com.simibubi.create.content.redstone.diodes.BrassDiodeBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.*;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

import static com.hlysine.create_connected.content.overstressclutch.OverstressClutchBlock.STATE;

public class OverstressClutchBlockEntity extends SplitShaftBlockEntity {

    protected int delay;
    ScrollValueBehaviour maxDelay;

    public OverstressClutchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        maxDelay = new TimeDelayScrollValueBehaviour(Components.translatable("create_connected.overstress_clutch.uncouple_delay"), this,
                new CenteredSideValueBoxTransform((state, d) -> {
                    Direction.Axis axis = d.getAxis();
                    Direction.Axis bearingAxis = state.getValue(OverstressClutchBlock.AXIS);
                    return bearingAxis != axis;
                })).between(1, 60 * 20 * 60);
        maxDelay.withFormatter(this::format);
        maxDelay.withCallback(this::onMaxDelayChanged);
        maxDelay.setValue(1);
        behaviours.add(maxDelay);
    }

    private void onMaxDelayChanged(int newMax) {
        delay = Mth.clamp(delay, 0, newMax);
        sendData();
    }

    private String format(int value) {
        if (value < 60)
            return value + "t";
        if (value < 20 * 60)
            return (value / 20) + "s";
        return (value / 20 / 60) + "m";
    }

    public boolean isIdle() {
        return delay == 0;
    }

    @Override
    public void updateFromNetwork(float maxStress, float currentStress, int networkSize) {
        super.updateFromNetwork(maxStress, currentStress, networkSize);

        if (IRotate.StressImpact.isEnabled()) {
            if (isOverStressed() && getBlockState().getValue(STATE) == ClutchState.COUPLED) {
                if (level != null) {
                    level.setBlock(getBlockPos(), getBlockState().setValue(STATE, ClutchState.UNCOUPLING), 2 | 16);
                }
            }
        }
        if (!isOverStressed() && getBlockState().getValue(STATE) == ClutchState.UNCOUPLING) {
            if (level != null) {
                level.setBlock(getBlockPos(), getBlockState().setValue(STATE, ClutchState.COUPLED), 2 | 16);
            }
        }
    }

    @Override
    public void onSpeedChanged(float prevSpeed) {
        super.onSpeedChanged(prevSpeed);
        KineticNetwork network = getOrCreateNetwork();
        updateFromNetwork(capacity, stress, network == null ? 0 : network.getSize());
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (hasSource()) {
            if (face != getSourceFacing() && getBlockState().getValue(STATE) == ClutchState.UNCOUPLED)
                return 0;
        }
        return 1;
    }

    public void unpowerClutch() {
        if (getBlockState().getValue(STATE) == ClutchState.UNCOUPLED) {
            level.setBlock(getBlockPos(), getBlockState().setValue(STATE, ClutchState.COUPLED), 2);
            RotationPropagator.handleRemoved(level, getBlockPos(), this);
            RotationPropagator.handleAdded(level, getBlockPos(), this);
        }
    }

    @Override
    public void tick() {
        ClutchState state = getBlockState().getValue(STATE);
        boolean atMax = delay >= maxDelay.getValue();
        boolean atMin = delay <= 0;
        updateState(state, atMax, atMin);

        super.tick();
    }

    protected void updateState(ClutchState state, boolean atMax, boolean atMin) {
        if (state == ClutchState.COUPLED || state == ClutchState.UNCOUPLED) {
            if (delay != 0) {
                delay = 0;
            }
            return;
        }
        if (atMin) {
            delay = maxDelay.getValue();
            return;
        }
        if (delay == 1) {
            if (!level.isClientSide) {
                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(STATE, ClutchState.UNCOUPLED));
                RotationPropagator.handleRemoved(level, getBlockPos(), this);
                return;
            }
        }
        delay--;
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        delay = compound.getInt("Delay");
        super.read(compound, clientPacket);
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.putInt("Delay", delay);
        super.write(compound, clientPacket);
    }

    public static class TimeDelayScrollValueBehaviour extends ScrollValueBehaviour {

        public TimeDelayScrollValueBehaviour(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
            super(label, be, slot);
        }

        @Override
        public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
            return new ValueSettingsBoard(label, 60, 10,
                    Lang.translatedOptions("generic.unit", "ticks", "seconds", "minutes"),
                    new ValueSettingsFormatter(this::formatSettings));
        }

        @Override
        public void onShortInteract(Player player, InteractionHand hand, Direction side) {
            BlockState blockState = blockEntity.getBlockState();
            if (blockState.getBlock() instanceof BrassDiodeBlock bdb)
                bdb.toggle(getWorld(), getPos(), blockState, player, hand);
        }

        @Override
        public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlHeld) {
            int value = valueSetting.value();
            int multiplier = switch (valueSetting.row()) {
                case 0 -> 1;
                case 1 -> 20;
                default -> 60 * 20;
            };
            if (!valueSetting.equals(getValueSettings()))
                playFeedbackSound(this);
            setValue(Math.max(1, Math.max(1, value) * multiplier));
        }

        @Override
        public ValueSettings getValueSettings() {
            int row = 0;
            int value = this.value;

            if (value > 60 * 20) {
                value = value / (60 * 20);
                row = 2;
            } else if (value > 60) {
                value = value / 20;
                row = 1;
            }

            return new ValueSettings(row, value);
        }

        public MutableComponent formatSettings(ValueSettings settings) {
            int value = Math.max(1, settings.value());
            return Components.literal(switch (settings.row()) {
                case 0 -> value + "t";
                case 1 -> "0:" + (value < 10 ? "0" : "") + value;
                default -> value + ":00";
            });
        }

        @Override
        public String getClipboardKey() {
            return "Timings";
        }

    }
}

