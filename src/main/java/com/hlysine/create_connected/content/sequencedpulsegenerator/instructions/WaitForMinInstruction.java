package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.CCGuiTextures;
import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.nbt.CompoundTag;

public class WaitForMinInstruction extends Instruction {

    public WaitForMinInstruction(int target, int signal) {
        super(
                "wait_for_min",
                CCGuiTextures.SEQUENCER_INSTRUCTION,
                new ParameterConfig("target",
                        0,
                        15,
                        null,
                        5,
                        1,
                        null),
                true,
                false
        );
        setValue(target);
        setSignal(signal);
    }

    @Override
    public InstructionResult tick(SequencedPulseGeneratorBlockEntity be) {
        if (be.getPreviousInput() < getValue() && be.getCurrentInput() >= getValue())
            return InstructionResult.next(true);
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
        return new WaitForMinInstruction(getValue(), getSignal());
    }
}
