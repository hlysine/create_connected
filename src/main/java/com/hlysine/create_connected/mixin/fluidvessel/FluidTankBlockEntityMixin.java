package com.hlysine.create_connected.mixin.fluidvessel;

import com.hlysine.create_connected.content.fluidvessel.FluidVesselBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FluidTankBlockEntity.class, remap = false)
public abstract class FluidTankBlockEntityMixin extends SmartBlockEntity {
    public FluidTankBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @SuppressWarnings("UnreachableCode")
    @Inject(
            at = @At("HEAD"),
            method = "createRenderBoundingBox()Lnet/minecraft/world/phys/AABB;",
            cancellable = true
    )
    private void createRenderBoundingBox(CallbackInfoReturnable<AABB> cir) {
        FluidTankBlockEntity self = (FluidTankBlockEntity) (Object) this;
        if (self instanceof FluidVesselBlockEntity) {
            cir.setReturnValue(super.createRenderBoundingBox());
        }
    }

    @SuppressWarnings("UnreachableCode")
    @Inject(
            at = @At("HEAD"),
            method = "read",
            cancellable = true
    )
    private void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        FluidTankBlockEntity self = (FluidTankBlockEntity) (Object) this;
        if (self instanceof FluidVesselBlockEntity) {
            super.read(compound, registries, clientPacket);
            ci.cancel();
        }
    }

    @SuppressWarnings("UnreachableCode")
    @Inject(
            at = @At("HEAD"),
            method = "write",
            cancellable = true
    )
    private void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        FluidTankBlockEntity self = (FluidTankBlockEntity) (Object) this;
        if (self instanceof FluidVesselBlockEntity) {
            super.write(compound, registries, clientPacket);
            ci.cancel();
        }
    }
}
