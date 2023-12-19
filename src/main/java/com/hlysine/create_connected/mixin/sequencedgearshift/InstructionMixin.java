package com.hlysine.create_connected.mixin.sequencedgearshift;

import com.hlysine.create_connected.CCSequencerInstructions;
import com.simibubi.create.content.kinetics.transmission.sequencer.Instruction;
import com.simibubi.create.content.kinetics.transmission.sequencer.InstructionSpeedModifiers;
import com.simibubi.create.content.kinetics.transmission.sequencer.OnIsPoweredResult;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencerInstructions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Instruction.class, remap = false)
public class InstructionMixin {

    @Shadow
    SequencerInstructions instruction;
    @Shadow
    InstructionSpeedModifiers speedModifier;

    @Inject(method = "getDuration(FF)I", at = @At("HEAD"), cancellable = true)
    public void getDurationForTurnAwait(float currentProgress, float speed, CallbackInfoReturnable<Integer> cir) {
        if (instruction == CCSequencerInstructions.TURN_AWAIT) {
            cir.setReturnValue(-1);
        }
    }

    @Inject(method = "getSpeedModifier()I", at = @At("HEAD"), cancellable = true)
    public void getSpeedModifierForTurnAwait(CallbackInfoReturnable<Integer> cir) {
        if (instruction == CCSequencerInstructions.TURN_AWAIT) {
            cir.setReturnValue(((InstructionSpeedModifiersAccessor) (Object) speedModifier).getValue());
        }
    }

    @Inject(method = "onRedstonePulse()Lcom/simibubi/create/content/kinetics/transmission/sequencer/OnIsPoweredResult;", at = @At("HEAD"), cancellable = true)
    public void onRedstonePulse(CallbackInfoReturnable<OnIsPoweredResult> cir) {
        if (instruction == CCSequencerInstructions.TURN_AWAIT) {
            cir.setReturnValue(OnIsPoweredResult.CONTINUE);
        }
    }
}
