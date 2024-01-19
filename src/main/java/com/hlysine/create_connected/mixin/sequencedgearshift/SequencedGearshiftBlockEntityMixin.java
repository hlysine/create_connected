package com.hlysine.create_connected.mixin.sequencedgearshift;

import com.hlysine.create_connected.CCSequencerInstructions;
import com.simibubi.create.content.kinetics.transmission.sequencer.Instruction;
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
        Instruction instruction = ((SequencedGearshiftBlockEntity) (Object) this).getInstruction(instructionIndex);
        if (instruction == null) return;
        if (((InstructionAccessor) instruction).getInstruction() == CCSequencerInstructions.LOOP) {
            ((SequencedGearshiftBlockEntity) (Object) this).run(instructionIndex == 0 ? -1 : 0); // prevent stack overflow
            ci.cancel();
        }
    }
}
