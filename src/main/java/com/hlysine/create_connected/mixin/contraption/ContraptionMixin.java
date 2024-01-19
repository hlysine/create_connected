package com.hlysine.create_connected.mixin.contraption;

import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Contraption.class, remap = false)
public class ContraptionMixin {
    @Inject(
            at = @At("TAIL"),
            method = "addBlock(Lnet/minecraft/core/BlockPos;Lorg/apache/commons/lang3/tuple/Pair;)V"
    )
    private void stopJukeboxOnMove(BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair, CallbackInfo ci) {
        if (pair.getValue() instanceof JukeboxBlockEntity be) {
            if (JukeboxBlockEntity.recordIsPlaying(be.getBlockState(), be)) {
                be.isPlaying = false;
                be.setChanged();
                be.getLevel().levelEvent(1010, be.getBlockPos(), 0);
            }
        }
    }
}
