package com.hlysine.create_connected.mixin.sequencedgearshift;

import com.hlysine.create_connected.CCSequencerInstructions;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.transmission.sequencer.Instruction;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftScreen;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencerInstructions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Vector;
import java.util.function.Function;

@Mixin(value = SequencedGearshiftScreen.class, remap = false)
public class SequencedGearshiftScreenMixin {
    @Shadow
    private Vector<Instruction> instructions;
    @Shadow
    private Vector<Vector<ScrollInput>> inputs;

    @WrapOperation(
            method = "updateParamsOfRow(I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/foundation/gui/widget/ScrollInput;withStepFunction(Ljava/util/function/Function;)Lcom/simibubi/create/foundation/gui/widget/ScrollInput;",
                    ordinal = 1
            )
    )
    public ScrollInput updateParamsOfRow(ScrollInput instance, Function<ScrollValueBehaviour.StepContext, Integer> step, Operation<ScrollInput> original, int row) {
        ScrollInput toReturn = original.call(instance, step);
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
        return toReturn;
    }

    @Inject(
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/transmission/sequencer/SequencedGearshiftScreen;updateParamsOfRow(I)V", shift = At.Shift.AFTER),
            method = "instructionUpdated(II)V",
            cancellable = true
    )
    private void handleLoop(int index, int state, CallbackInfo ci) {
        SequencerInstructions newValue = SequencerInstructions.values()[state];
        if (newValue == CCSequencerInstructions.LOOP) {
            for (int i = instructions.size() - 1; i > index; i--) {
                instructions.remove(i);
                Vector<ScrollInput> rowInputs = inputs.get(i);
                ((AbstractSimiScreenAccessor) this).callRemoveWidgets(rowInputs);
                rowInputs.clear();
            }
            ci.cancel();
        }
    }
}
