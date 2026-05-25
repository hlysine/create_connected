package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.registries.CCGuiTextures;
import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.nbt.CompoundTag;

public class WaitForExactInstruction extends Instruction {

    public WaitForExactInstruction(int target, int signal) {
        super(
                "wait_for_exact",
                CCGuiTextures.SEQUENCER_INSTRUCTION,
                new ParameterConfig(
                        0,
                        15,
                        null,
                        5,
                        1,
                        null
                ),
                true,
                false
        );
        setParam(target);
        setSignal(signal);
    }

    @Override
    public InstructionResult tick(SequencedPulseGeneratorBlockEntity be) {
        if (be.getPreviousInput() != getParam() && be.getCurrentInput() == getParam())
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
        return new WaitForExactInstruction(getParam(), getSignal());
    }
}
