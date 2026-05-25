package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.registries.CCGuiTextures;
import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.nbt.CompoundTag;

public class LoopIfInstruction extends Instruction {

    public LoopIfInstruction(int target) {
        super(
                "loop_if",
                CCGuiTextures.SEQUENCER_DELAY,
                new ParameterConfig(
                        0,
                        1,
                        null,
                        1,
                        1,
                        ParameterConfig.booleanFormat
                ),
                false,
                false
        );
        setParam(target);
    }

    @Override
    public InstructionResult tick(SequencedPulseGeneratorBlockEntity be) {
        if ((be.getCurrentInput() > 0) == (getParam() == 1)) {
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
        return new LoopIfInstruction(getParam());
    }
}
