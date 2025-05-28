package com.hlysine.create_connected.content.kineticbridge;


import com.google.common.collect.ImmutableList;
import com.hlysine.create_connected.ConnectedLang;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import net.createmod.catnip.lang.LangNumberFormat;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class StressImpactScrollValueBehaviour extends ScrollValueBehaviour {

    public StressImpactScrollValueBehaviour(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
        super(label, be, slot);
        withFormatter(v -> String.format("%1sx", LangNumberFormat.format(convertValue(v))));
    }

    public static float convertValue(int value) {
        if (value < 40) {
            return (float) Math.pow(2, Math.abs(value / 10.0));
        } else {
            return (int) Math.pow(2, Math.abs(value / 10.0));
        }
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        ImmutableList<Component> rows = ImmutableList.of(ConnectedLang.translateDirect("kinetic_bridge.stress_impact_short"));
        ValueSettingsFormatter formatter = new ValueSettingsFormatter(this::formatSettings);
        return new ValueSettingsBoard(label, 160, 10, rows, formatter);
    }

    @Override
    public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlHeld) {
        int value = Math.max(0, valueSetting.value());
        if (!valueSetting.equals(getValueSettings()))
            playFeedbackSound(this);
        setValue(Mth.abs(value));
    }

    @Override
    public ValueSettings getValueSettings() {
        return new ValueSettings(0, Math.abs(value));
    }

    public MutableComponent formatSettings(ValueSettings settings) {
        return ConnectedLang.number(Math.max(0, convertValue(settings.value()))).add(Component.literal("x"))
                .component();
    }

    @Override
    public String getClipboardKey() {
        return "Stress Impact";
    }

}

