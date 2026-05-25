package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.CCGuiTextures;
import com.hlysine.create_connected.ConnectedLang;
import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import net.minecraft.client.resources.language.I18n;
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
    public final @Nullable ParameterConfig paramConfig;
    public final boolean hasSignal;
    public final boolean terminal;

    private int param = 0;
    private int signal = 0;

    public Instruction(String instructionId,
                       CCGuiTextures background,
                       @Nullable ParameterConfig paramConfig,
                       boolean hasSignal,
                       boolean terminal) {
        this.instructionId = instructionId;
        this.background = background;
        this.paramConfig = paramConfig;
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

    public int transformOutput(SequencedPulseGeneratorBlockEntity be, int signal) {
        return signal;
    }

    public int getParam() {
        return param;
    }

    public int getSignal() {
        return signal;
    }

    public void setParam(int param) {
        this.param = param;
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
        if (paramConfig != null) {
            nbt.putInt("Value", param);
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
        if (instance.paramConfig != null) {
            instance.param = nbt.getInt("Value");
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
        return "gui.sequenced_pulse_generator.instruction." + ConnectedLang.asId(instructionId);
    }

    public String getDescriptiveLangKey() {
        return getLangKey() + ".descriptive";
    }

    public String getParamLangKey() {
        String key = getLangKey() + ".param";
        if (I18n.exists(CreateConnected.MODID + "." + key))
            return key;
        return "gui.sequenced_pulse_generator.param";
    }

    public String getSignalLangKey() {
        String key = getLangKey() + ".signal";
        if (I18n.exists(CreateConnected.MODID + "." + key))
            return key;
        return "gui.sequenced_pulse_generator.signal";
    }

    public static List<Component> getOptions() {
        List<Component> options = new ArrayList<>();
        for (Instruction value : INSTRUCTION_MAP.values())
            options.add(ConnectedLang.translateDirect(value.getDescriptiveLangKey()));
        return options;
    }

    public record ParameterConfig(int minValue,
                                  int maxValue,
                                  @Nullable Function<ScrollValueBehaviour.StepContext, Integer> stepFunction,
                                  int shiftStepValue,
                                  int defaultValue,
                                  @Nullable Function<Integer, Component> formatter) {
        public static final Function<ScrollValueBehaviour.StepContext, Integer> timeStep = context -> {
            int v = context.currentValue;
            if (!context.forward)
                v--;
            if (v < 20)
                return context.shift ? 20 : 1;
            return context.shift ? 100 : 20;
        };
        public static final Function<Integer, Component> timeFormat = value -> {
            if (value >= 20) return Component.literal((value / 20) + "s");
            return Component.literal(value + "t");
        };
        public static final Function<Integer, Component> booleanFormat = value -> value == 1
                ? ConnectedLang.translate("gui.sequenced_pulse_generator.on").component()
                : ConnectedLang.translate("gui.sequenced_pulse_generator.off").component();
        public static final Function<Integer, Component> transformFormat = value -> Component.literal(switch (value) {
            case 0 -> "I+C";
            case 1 -> "I-C";
            case 2 -> "C-I";
            case 3 -> "I×C";
            case 4 -> "I÷C";
            case 5 -> "I&C";
            case 6 -> "I|C";
            case 7 -> "I^C";
            case 8 -> "I<<C";
            case 9 -> "I>>C";
            default -> Integer.toString(value);
        });
    }
}
