package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity.INSTRUCTION_CAPACITY;

public abstract class Instruction {
    private static final Map<String, Instruction> INSTRUCTION_MAP = new HashMap<>();

    public static void register(Instruction instruction) {
        INSTRUCTION_MAP.put(instruction.instructionId, instruction);
    }

    private final String instructionId;
    public final @Nullable ParameterConfig parameter;
    public final boolean hasSignal;

    private int value = 0;
    private int signal = 0;

    public Instruction(String instructionId,
                       @Nullable ParameterConfig parameter,
                       boolean hasSignal) {
        this.instructionId = instructionId;
        this.parameter = parameter;
        this.hasSignal = hasSignal;
    }

    public String getId() {
        return instructionId;
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
        instructions.add(new TimeInstruction(10, 15));
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

    public static Vector<Instruction> deserializeAll(ListTag list) {
        if (list.isEmpty()) {
            return Instruction.createDefault();
        } else {
            Vector<Instruction> instructions = new Vector<>(INSTRUCTION_CAPACITY);
            list.forEach(tag -> instructions.add(Instruction.deserializeParams((CompoundTag) tag)));
            return instructions;
        }
    }

    public record ParameterConfig(String parameterId,
                                  int minValue,
                                  int maxValue,
                                  int stepValue,
                                  int defaultValue) {
    }
}
