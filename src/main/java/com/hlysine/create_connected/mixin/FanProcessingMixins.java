package com.hlysine.create_connected.mixin;

import com.hlysine.create_connected.content.ConditionalFanProcessing;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.decoration.copycat.CopycatModel;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

public class FanProcessingMixins {
    @Mixin(AllFanProcessingTypes.BlastingType.class)
    public static class BlastingMixin {
        @Inject(
                at = @At("HEAD"),
                method = "isValidAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z",
                cancellable = true,
                remap = false
        )
        void conditionalProcessing(Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
            BlockState blockState = level.getBlockState(pos);
            if (AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_BLASTING.matches(blockState)) {
                if (blockState.getBlock() instanceof ConditionalFanProcessing processing) {
                    if (processing.canApplyFanType(AllFanProcessingTypes.BLASTING, level, pos, blockState)) {
                        cir.setReturnValue(true);
                    } else {
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }

    @Mixin(AllFanProcessingTypes.SmokingType.class)
    public static class SmokingMixin {
        @Inject(
                at = @At("HEAD"),
                method = "isValidAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z",
                cancellable = true,
                remap = false
        )
        void conditionalProcessing(Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
            BlockState blockState = level.getBlockState(pos);
            if (AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_SMOKING.matches(blockState)) {
                if (blockState.getBlock() instanceof ConditionalFanProcessing processing) {
                    if (processing.canApplyFanType(AllFanProcessingTypes.SMOKING, level, pos, blockState)) {
                        cir.setReturnValue(true);
                    } else {
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }

    @Mixin(AllFanProcessingTypes.SplashingType.class)
    public static class SplashingMixin {
        @Inject(
                at = @At("HEAD"),
                method = "isValidAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z",
                cancellable = true,
                remap = false
        )
        void conditionalProcessing(Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
            BlockState blockState = level.getBlockState(pos);
            if (AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_SPLASHING.matches(blockState)) {
                if (blockState.getBlock() instanceof ConditionalFanProcessing processing) {
                    if (processing.canApplyFanType(AllFanProcessingTypes.SPLASHING, level, pos, blockState)) {
                        cir.setReturnValue(true);
                    } else {
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }

    @Mixin(AllFanProcessingTypes.HauntingType.class)
    public static class HauntingMixin {
        @Inject(
                at = @At("HEAD"),
                method = "isValidAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z",
                cancellable = true,
                remap = false
        )
        void conditionalProcessing(Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
            BlockState blockState = level.getBlockState(pos);
            if (AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_HAUNTING.matches(blockState)) {
                if (blockState.getBlock() instanceof ConditionalFanProcessing processing) {
                    if (processing.canApplyFanType(AllFanProcessingTypes.HAUNTING, level, pos, blockState)) {
                        cir.setReturnValue(true);
                    } else {
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }
}
