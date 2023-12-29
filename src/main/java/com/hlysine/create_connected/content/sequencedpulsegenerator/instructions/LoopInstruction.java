package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.CCGuiTextures;
import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.nbt.CompoundTag;

public class LoopInstruction extends Instruction {
    public LoopInstruction() {
        super("loop", CCGuiTextures.SEQUENCER_END, null, false, true);
    }

    @Override
    public InstructionResult tick(SequencedPulseGeneratorBlockEntity be) {
        return InstructionResult.backToTop(true);
    }

    @Override
    public void writeState(CompoundTag nbt) {

    }

    @Override
    public void readState(CompoundTag nbt) {

    }

    @Override
    public Instruction copy() {
        return new LoopInstruction();
    }
}
