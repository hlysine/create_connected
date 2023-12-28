package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.CCGuiTextures;
import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.nbt.CompoundTag;

public class JumpIfInstruction extends Instruction {

    public JumpIfInstruction(int target) {
        super(
                "jump_if",
                CCGuiTextures.SEQUENCER_DELAY,
                new ParameterConfig("target",
                        0,
                        1,
                        null,
                        1,
                        1,
                        ParameterConfig.booleanFormat),
                false,
                false
        );
        setValue(target);
    }

    @Override
    public InstructionResult tick(SequencedPulseGeneratorBlockEntity be) {
        if (be.isPowered() == (getValue() == 1)) {
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
        return new JumpIfInstruction(getValue());
    }
}
