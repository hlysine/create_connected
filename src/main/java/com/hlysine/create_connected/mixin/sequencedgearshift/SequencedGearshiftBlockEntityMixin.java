package com.hlysine.create_connected.mixin.sequencedgearshift;

import com.hlysine.create_connected.CCSequencerInstructions;
import com.simibubi.create.content.kinetics.transmission.sequencer.Instruction;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlock;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SequencedGearshiftBlockEntity.class, remap = false)
public class SequencedGearshiftBlockEntityMixin {

    @Inject(
            at = @At("HEAD"),
            method = "run(I)V",
            cancellable = true
    )
    private void runLoop(int instructionIndex, CallbackInfo ci) {
        SequencedGearshiftBlockEntity self = (SequencedGearshiftBlockEntity) (Object) this;
        Instruction instruction = self.getInstruction(instructionIndex);
        if (instruction == null) return;
        if (((InstructionAccessor) instruction).getInstruction() == CCSequencerInstructions.LOOP) {
            // force a block state change
            if (instructionIndex == 1)
                self.getLevel().setBlock(self.getBlockPos(), self.getBlockState().setValue(SequencedGearshiftBlock.STATE, instructionIndex + 1), 3);
            // prevent stack overflow
            self.run(instructionIndex == 0 ? -1 : 0);
            ci.cancel();
        }
    }
}
