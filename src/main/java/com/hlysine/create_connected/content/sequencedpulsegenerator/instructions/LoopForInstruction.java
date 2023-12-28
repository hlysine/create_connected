package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.CCGuiTextures;
import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.nbt.CompoundTag;

public class LoopForInstruction extends Instruction {
    private int progress = 0;

    public LoopForInstruction(int target) {
        super(
                "loop_for",
                CCGuiTextures.SEQUENCER_DELAY,
                new ParameterConfig("count",
                        1,
                        100,
                        null,
                        10,
                        3,
                        null),
                false,
                false
        );
        setValue(target);
    }

    @Override
    public InstructionResult tick(SequencedPulseGeneratorBlockEntity be) {
        progress++;
        if (progress >= getValue()) {
            progress = 0;
            return InstructionResult.next(true);
        }
        return InstructionResult.backToTop(true);
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
        return new LoopForInstruction(getValue());
    }
}
