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
    @Shadow
    int value;

    @Inject(method = "getDuration(FF)I", at = @At("HEAD"), cancellable = true)
    private void getCustomDuration(float currentProgress, float speed, CallbackInfoReturnable<Integer> cir) {
        if (instruction == CCSequencerInstructions.TURN_AWAIT) {
            cir.setReturnValue(-1);
        } else if (instruction == CCSequencerInstructions.TURN_TIME) {
            double target = value - currentProgress;
            cir.setReturnValue((int) target);
        } else if (instruction == CCSequencerInstructions.LOOP) {
            cir.setReturnValue(0);
        }
    }

    @Inject(method = "getTickProgress(F)F", at = @At("HEAD"), cancellable = true)
    private void getCustomTickProgress(float speed, CallbackInfoReturnable<Float> cir) {
        if (instruction == CCSequencerInstructions.TURN_AWAIT) {
            cir.setReturnValue(0f);
        } else if (instruction == CCSequencerInstructions.TURN_TIME) {
            cir.setReturnValue(1f);
        } else if (instruction == CCSequencerInstructions.LOOP) {
            cir.setReturnValue(0f);
        }
    }

    @Inject(method = "getSpeedModifier()I", at = @At("HEAD"), cancellable = true)
    private void getCustomSpeedModifier(CallbackInfoReturnable<Integer> cir) {
        if (instruction == CCSequencerInstructions.TURN_AWAIT) {
            cir.setReturnValue(((InstructionSpeedModifiersAccessor) (Object) speedModifier).getValue());
        } else if (instruction == CCSequencerInstructions.TURN_TIME) {
            cir.setReturnValue(((InstructionSpeedModifiersAccessor) (Object) speedModifier).getValue());
        } else if (instruction == CCSequencerInstructions.LOOP) {
            cir.setReturnValue(0);
        }
    }

    @Inject(method = "onRedstonePulse()Lcom/simibubi/create/content/kinetics/transmission/sequencer/OnIsPoweredResult;", at = @At("HEAD"), cancellable = true)
    private void onCustomRedstonePulse(CallbackInfoReturnable<OnIsPoweredResult> cir) {
        if (instruction == CCSequencerInstructions.TURN_AWAIT) {
            cir.setReturnValue(OnIsPoweredResult.CONTINUE);
        } else if (instruction == CCSequencerInstructions.TURN_TIME) {
            cir.setReturnValue(OnIsPoweredResult.NOTHING);
        } else if (instruction == CCSequencerInstructions.LOOP) {
            cir.setReturnValue(OnIsPoweredResult.NOTHING);
        }
    }
}
