package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.registries.CCGuiTextures;
import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.nbt.CompoundTag;

public class LoopIfExactInstruction extends Instruction {

    public LoopIfExactInstruction(int target) {
        super(
                "loop_if_exact",
                CCGuiTextures.SEQUENCER_DELAY,
                new ParameterConfig(
                        0,
                        15,
                        null,
                        5,
                        1,
                        null
                ),
                false,
                false
        );
        setParam(target);
    }

    @Override
    public InstructionResult tick(SequencedPulseGeneratorBlockEntity be) {
        if (be.getCurrentInput() == getParam()) {
            return InstructionResult.backToTop(true);
        }
        return InstructionResult.next(true);
    }

    @Override
    public void writeState(CompoundTag nbt) {
    }

    @Override
    public void readState(CompoundTag nbt) {
    }

    @Override
    public Instruction copy() {
        return new LoopIfExactInstruction(getParam());
    }
}
