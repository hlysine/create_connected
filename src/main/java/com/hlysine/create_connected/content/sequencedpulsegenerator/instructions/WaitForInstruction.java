package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.registries.CCGuiTextures;
import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.nbt.CompoundTag;

public class WaitForInstruction extends Instruction {

    public WaitForInstruction(int target, int signal) {
        super(
                "wait_for",
                CCGuiTextures.SEQUENCER_INSTRUCTION,
                new ParameterConfig(
                        0,
                        1,
                        null,
                        1,
                        1,
                        ParameterConfig.booleanFormat
                ),
                true,
                false
        );
        setParam(target);
        setSignal(signal);
    }

    @Override
    public InstructionResult tick(SequencedPulseGeneratorBlockEntity be) {
        if (be.getPreviousInput() == 0 && be.getCurrentInput() > 0 && getParam() == 1)
            return InstructionResult.next(true);
        if (be.getPreviousInput() > 0 && be.getCurrentInput() == 0 && getParam() == 0)
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
        return new WaitForInstruction(getParam(), getSignal());
    }
}
