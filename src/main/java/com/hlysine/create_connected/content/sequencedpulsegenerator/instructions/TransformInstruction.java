package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.CCGuiTextures;
import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.nbt.CompoundTag;

public class TransformInstruction extends Instruction {
    private int input = -1;

    public TransformInstruction(int transform, int constant) {
        super(
                "transform",
                CCGuiTextures.SEQUENCER_INSTRUCTION,
                new ParameterConfig(
                        0,
                        4,
                        null,
                        1,
                        0,
                        ParameterConfig.transformFormat
                ),
                true,
                false
        );
        setParam(transform);
        setSignal(constant);
    }

    @Override
    public InstructionResult tick(SequencedPulseGeneratorBlockEntity be) {
        if (input == -1) {
            input = be.getCurrentInput();
        } else if (be.getCurrentInput() != input) {
            input = -1;
            return InstructionResult.next(true);
        }
        return InstructionResult.incomplete();
    }

    @Override
    public int transformOutput(SequencedPulseGeneratorBlockEntity be, int signal) {
        return Math.clamp(switch (getParam()) {
            case 0 -> be.getCurrentInput() + getSignal();
            case 1 -> be.getCurrentInput() - getSignal();
            case 2 -> getSignal() - be.getCurrentInput();
            case 3 -> (long) be.getCurrentInput() * getSignal();
            case 4 -> getSignal() == 0 ? 0 : be.getCurrentInput() / getSignal();
            default -> signal;
        }, 0, 15);
    }

    @Override
    public void writeState(CompoundTag nbt) {
        nbt.putInt("Input", input);
    }

    @Override
    public void readState(CompoundTag nbt) {
        input = nbt.getInt("Input");
    }

    @Override
    public Instruction copy() {
        return new TransformInstruction(getParam(), getSignal());
    }
}
