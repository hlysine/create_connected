package com.hlysine.create_connected.content.overstressclutch;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.Lang;
import com.hlysine.create_connected.content.overstressclutch.OverstressClutchBlock.ClutchState;
import com.hlysine.create_connected.datagen.advancements.AdvancementBehaviour;
import com.hlysine.create_connected.datagen.advancements.CCAdvancements;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import com.simibubi.create.content.redstone.diodes.BrassDiodeBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.*;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.Components;
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
import net.minecraft.world.ticks.TickPriority;

import java.util.List;

import static com.hlysine.create_connected.content.overstressclutch.OverstressClutchBlock.POWERED;
import static com.hlysine.create_connected.content.overstressclutch.OverstressClutchBlock.STATE;
import static net.minecraft.ChatFormatting.GOLD;

public class OverstressClutchBlockEntity extends SplitShaftBlockEntity {

    public int delay;
    public ScrollValueBehaviour maxDelay;

    public OverstressClutchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        AdvancementBehaviour.registerAwardables(this, behaviours, CCAdvancements.OVERSTRESS_CLUTCH);
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
    public void initialize() {
        onKineticUpdate();
        super.initialize();
    }

    public void onKineticUpdate() {
        if (getBlockState().getValue(STATE) == ClutchState.UNCOUPLED && getBlockState().getValue(POWERED)) {
            resetClutch();
            return;
        }
        if (IRotate.StressImpact.isEnabled() && !getBlockState().getValue(POWERED)) {
            if (isOverStressed() && getBlockState().getValue(STATE) == ClutchState.COUPLED) {
                if (level != null) {
                    level.setBlock(getBlockPos(), getBlockState().setValue(STATE, ClutchState.UNCOUPLING), 2 | 16);
                    delay = maxDelay.getValue() - 1;
                    sendData();
                    return;
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
    public void updateFromNetwork(float maxStress, float currentStress, int networkSize) {
        super.updateFromNetwork(maxStress, currentStress, networkSize);
        onKineticUpdate();
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (hasSource()) {
            if (face != getSourceFacing() && getBlockState().getValue(STATE) == ClutchState.UNCOUPLED)
                return 0;
        }
        return 1;
    }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean added = super.addToTooltip(tooltip, isPlayerSneaking);

        if (getBlockState().getValue(STATE) == ClutchState.UNCOUPLED) {
            Lang.translate("gui.overstress_clutch.uncoupled")
                    .style(GOLD)
                    .forGoggles(tooltip);
            Component hint = Lang.translateDirect("gui.overstress_clutch.uncoupled_explanation");
            List<Component> cutString = TooltipHelper.cutTextComponent(hint, TooltipHelper.Palette.GRAY_AND_WHITE);
            for (Component component : cutString)
                Lang.builder()
                        .add(component.copy())
                        .forGoggles(tooltip);
            added = true;
        }

        return added;
    }

    public void resetClutch() {
        if (getBlockState().getValue(STATE) == ClutchState.UNCOUPLED && !isOverStressed()) {
            assert level != null;
            level.setBlock(getBlockPos(), getBlockState().setValue(STATE, ClutchState.COUPLED), 3);
            RotationPropagator.handleRemoved(level, getBlockPos(), this);
            RotationPropagator.handleAdded(level, getBlockPos(), this);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (getBlockState().getValue(STATE) == ClutchState.UNCOUPLING && level != null && !level.isClientSide) {
            level.scheduleTick(getBlockPos(), CCBlocks.OVERSTRESS_CLUTCH.get(), 0, TickPriority.EXTREMELY_HIGH);
        }
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
                    com.simibubi.create.foundation.utility.Lang.translatedOptions("generic.unit", "ticks", "seconds", "minutes"),
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

