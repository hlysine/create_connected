package com.hlysine.create_connected.mixin.sequencedgearshift;

import com.hlysine.create_connected.CCSequencerInstructions;
import com.simibubi.create.content.kinetics.transmission.sequencer.Instruction;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftScreen;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Vector;

@Mixin(value = SequencedGearshiftScreen.class, remap = false)
public class SequencedGearshiftScreenMixin {
    @Shadow
    private Vector<Instruction> instructions;
    @Shadow
    private Vector<Vector<ScrollInput>> inputs;

    @Inject(
            method = "updateParamsOfRow(I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/foundation/gui/widget/ScrollInput;standardStep()Ljava/util/function/Function;",
                    shift = At.Shift.BY,
                    by = 2
            )
    )
    public void updateParamsOfRow(int row, CallbackInfo ci) {
        if (((InstructionAccessor) instructions.get(row)).getInstruction() == CCSequencerInstructions.TURN_TIME) {
            Vector<ScrollInput> rowInputs = inputs.get(row);
            ScrollInput value = rowInputs.get(1);
            value.withStepFunction(context -> {
                int v = context.currentValue;
                if (!context.forward)
                    v--;
                if (v < 20)
                    return context.shift ? 20 : 1;
                return context.shift ? 100 : 20;
            });
        }
    }
}
