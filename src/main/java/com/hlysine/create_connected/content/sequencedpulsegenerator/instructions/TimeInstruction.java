package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.nbt.CompoundTag;

public class TimeInstruction extends Instruction {
    private int progress = 0;

    public TimeInstruction(int duration, int signal) {
        super(
                "time",
                new ParameterConfig("duration", 1, 600, 20, 10),
                true
        );
        setValue(duration);
        setSignal(signal);
    }

    @Override
    public InstructionResult tick(SequencedPulseGeneratorBlockEntity be) {
        if (progress >= getValue()) {
            return InstructionResult.next();
        }
        progress++;
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
        return new TimeInstruction(getValue(), getSignal());
    }
}
