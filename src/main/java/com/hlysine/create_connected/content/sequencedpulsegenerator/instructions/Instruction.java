package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.CCGuiTextures;
import com.hlysine.create_connected.Lang;
import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

import static com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity.INSTRUCTION_CAPACITY;

public abstract class Instruction {
    private static final Map<String, Instruction> INSTRUCTION_MAP = new LinkedHashMap<>();

    public static void register(Instruction instruction) {
        INSTRUCTION_MAP.put(instruction.instructionId, instruction);
    }

    private final String instructionId;
    private final CCGuiTextures background;
    public final @Nullable ParameterConfig parameter;
    public final boolean hasSignal;
    public final boolean terminal;

    private int value = 0;
    private int signal = 0;

    public Instruction(String instructionId,
                       CCGuiTextures background,
                       @Nullable ParameterConfig parameter,
                       boolean hasSignal,
                       boolean terminal) {
        this.instructionId = instructionId;
        this.background = background;
        this.parameter = parameter;
        this.hasSignal = hasSignal;
        this.terminal = terminal;
    }

    public String getId() {
        return instructionId;
    }

    public int getOrdinal() {
        return INSTRUCTION_MAP.keySet().stream().toList().indexOf(getId());
    }

    public static Instruction getByOrdinal(int ordinal) {
        return INSTRUCTION_MAP.values().stream().toList().get(ordinal).copy();
    }

    public CCGuiTextures getBackground() {
        return background;
    }

    public InstructionResult tick(SequencedPulseGeneratorBlockEntity be) {
        return InstructionResult.incomplete();
    }

    public InstructionResult onRisingEdge(SequencedPulseGeneratorBlockEntity be) {
        return InstructionResult.incomplete();
    }

    public InstructionResult onFallingEdge(SequencedPulseGeneratorBlockEntity be) {
        return InstructionResult.incomplete();
    }

    public int getValue() {
        return value;
    }

    public int getSignal() {
        return signal;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    /**
     * Only serialize instruction state here. Parameter and signal are done elsewhere automatically.
     */
    public abstract void writeState(CompoundTag nbt);

    public abstract void readState(CompoundTag nbt);

    public abstract Instruction copy();

    public static Vector<Instruction> createDefault() {
        Vector<Instruction> instructions = new Vector<>(INSTRUCTION_CAPACITY);
        instructions.add(new OutputInstruction(10, 15));
        instructions.add(new EndInstruction());
        return instructions;
    }

    public static Instruction create(String instructionId) {
        Instruction template = INSTRUCTION_MAP.get(instructionId);
        if (template == null) return null;
        return template.copy();
    }

    public CompoundTag serializeParams() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("ID", instructionId);
        if (hasSignal) {
            nbt.putInt("Signal", signal);
        }
        if (parameter != null) {
            nbt.putInt("Value", value);
        }
        return nbt;
    }

    public static Instruction deserializeParams(CompoundTag nbt) {
        String id = nbt.getString("ID");
        Instruction instance = create(id);
        if (instance == null) return null;
        if (instance.hasSignal) {
            instance.signal = nbt.getInt("Signal");
        }
        if (instance.parameter != null) {
            instance.value = nbt.getInt("Value");
        }
        return instance;
    }

    public static ListTag serializeAll(Vector<Instruction> instructions) {
        ListTag list = new ListTag();
        instructions.forEach(i -> {
            CompoundTag tag = i.serializeParams();
            i.writeState(tag);
            list.add(tag);
        });
        return list;
    }

    public static Vector<Instruction> deserializeAll(ListTag list) {
        if (list.isEmpty()) {
            return Instruction.createDefault();
        } else {
            Vector<Instruction> instructions = new Vector<>(INSTRUCTION_CAPACITY);
            list.forEach(tag -> {
                Instruction instruction = Instruction.deserializeParams((CompoundTag) tag);
                if (instruction == null) return;
                instruction.readState((CompoundTag) tag);
                instructions.add(instruction);
            });
            return instructions;
        }
    }

    public String getLangKey() {
        return "gui.sequenced_pulse_generator.instruction." + Lang.asId(instructionId);
    }

    public String getDescriptiveLangKey() {
        return getLangKey() + ".descriptive";
    }

    public String getParameterLangKey() {
        if (parameter != null)
            return getLangKey() + "." + Lang.asId(parameter.parameterId);
        return getLangKey() + ".missing";
    }

    public static List<Component> getOptions() {
        List<Component> options = new ArrayList<>();
        for (Instruction value : INSTRUCTION_MAP.values())
            options.add(Lang.translateDirect(value.getDescriptiveLangKey()));
        return options;
    }

    public record ParameterConfig(String parameterId,
                                  int minValue,
                                  int maxValue,
                                  @Nullable Function<ScrollValueBehaviour.StepContext, Integer> stepFunction,
                                  int shiftStepValue,
                                  int defaultValue,
                                  @Nullable Function<Integer, String> formatter) {
        public static final Function<ScrollValueBehaviour.StepContext, Integer> timeStep = context -> {
            int v = context.currentValue;
            if (!context.forward)
                v--;
            if (v < 20)
                return context.shift ? 20 : 1;
            return context.shift ? 100 : 20;
        };
        public static final Function<Integer, String> timeFormat = value -> {
            if (value >= 20) return (value / 20) + "s";
            return value + "t";
        };
        public static final Function<Integer, String> booleanFormat = value -> value == 1 ? "On" : "Off";
    }
}
