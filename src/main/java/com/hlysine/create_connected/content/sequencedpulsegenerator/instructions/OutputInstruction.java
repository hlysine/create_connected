package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.CCGuiTextures;
import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.nbt.CompoundTag;

public class OutputInstruction extends Instruction {
    private int progress = 0;

    public OutputInstruction(int duration, int signal) {
        super(
                "output",
                CCGuiTextures.SEQUENCER_INSTRUCTION,
                new ParameterConfig(
                        1,
                        600,
                        ParameterConfig.timeStep,
                        20,
                        10,
                        ParameterConfig.timeFormat
                ),
                true,
                false
        );
        setParam(duration);
        setSignal(signal);
    }

    @Override
    public InstructionResult tick(SequencedPulseGeneratorBlockEntity be) {
        progress++;
        if (progress >= getParam()) {
            progress = 0;
            return InstructionResult.next(getParam() <= 0);
        }
        return InstructionResult.incomplete();
    }

    @Override
    public void writeState(CompoundTag nbt) {
        nbt.putInt("Progress", progress);
    }

    @Override
    public void readState(CompoundTag nbt) {
        progress = nbt.getInt("Progress");
    }

    @Override
    public Instruction copy() {
        return new OutputInstruction(getParam(), getSignal());
    }
}
