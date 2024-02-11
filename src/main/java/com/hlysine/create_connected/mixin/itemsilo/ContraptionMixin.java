package com.hlysine.create_connected.mixin.itemsilo;

import com.hlysine.create_connected.content.itemsilo.ItemSiloBlockEntity;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Contraption.class, remap = false)
public abstract class ContraptionMixin {
    @Shadow
    protected abstract BlockPos toLocalPos(BlockPos globalPos);

    @Inject(
            at = @At("HEAD"),
            method = "getBlockEntityNBT(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/nbt/CompoundTag;",
            cancellable = true
    )
    private void getItemSiloNBT(Level world, BlockPos pos, CallbackInfoReturnable<CompoundTag> cir) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ItemSiloBlockEntity) {
            CompoundTag nbt = blockEntity.saveWithFullMetadata();
            nbt.remove("x");
            nbt.remove("y");
            nbt.remove("z");

            if (nbt.contains("Controller"))
                nbt.put("Controller",
                        NbtUtils.writeBlockPos(toLocalPos(NbtUtils.readBlockPos(nbt.getCompound("Controller")))));

            cir.setReturnValue(nbt);
        }
    }
}
