package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.nbt.CompoundTag;

public class EndInstruction extends Instruction {
    public EndInstruction() {
        super("end", null, false);
    }

    @Override
    public InstructionResult tick(SequencedPulseGeneratorBlockEntity be) {
        return InstructionResult.terminate();
    }

    @Override
    public InstructionResult onRisingEdge(SequencedPulseGeneratorBlockEntity be) {
        return InstructionResult.terminate();
    }

    @Override
    public InstructionResult onFallingEdge(SequencedPulseGeneratorBlockEntity be) {
        return InstructionResult.terminate();
    }

    @Override
    public void writeState(CompoundTag nbt) {

    }

    @Override
    public void readState(CompoundTag nbt) {

    }

    @Override
    public Instruction copy() {
        return new EndInstruction();
    }
}
