package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.CCGuiTextures;
import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.nbt.CompoundTag;

public class WaitForInstruction extends Instruction {

    public WaitForInstruction(int target, int signal) {
        super(
                "wait_for",
                CCGuiTextures.SEQUENCER_INSTRUCTION,
                new ParameterConfig("target",
                        0,
                        1,
                        null,
                        1,
                        1,
                        ParameterConfig.booleanFormat),
                true,
                false
        );
        setValue(target);
        setSignal(signal);
    }

    @Override
    public InstructionResult onRisingEdge(SequencedPulseGeneratorBlockEntity be) {
        if (getValue() == 1) return InstructionResult.next(true);
        return InstructionResult.incomplete();
    }

    @Override
    public InstructionResult onFallingEdge(SequencedPulseGeneratorBlockEntity be) {
        if (getValue() == 0) return InstructionResult.next(true);
        return InstructionResult.incomplete();
    }

    @Override
    public void writeState(CompoundTag nbt) {
    }

    @Override
    public void readState(CompoundTag nbt) {
    }

    @Override
    public Instruction copy() {
        return new WaitForInstruction(getValue(), getSignal());
    }
}
