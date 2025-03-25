package com.hlysine.create_connected.mixin.fluidvessel;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.content.fluidvessel.FluidVesselBlockEntity;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlock;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.ref.WeakReference;
import java.util.Objects;

@Mixin(SteamEngineBlockEntity.class)
public class SteamEngineBlockEntityMixin {
    @Inject(
            method = "getTank()Lcom/simibubi/create/content/fluids/tank/FluidTankBlockEntity;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"
            ),
            cancellable = true
    )
    private void getTank(CallbackInfoReturnable<FluidTankBlockEntity> cir) {
        SteamEngineBlockEntity self = (SteamEngineBlockEntity) (Object) this;
        Direction facing = SteamEngineBlock.getFacing(self.getBlockState());
        BlockEntity be = Objects.requireNonNull(self.getLevel()).getBlockEntity(self.getBlockPos().relative(facing.getOpposite()));

        if (be instanceof FluidVesselBlockEntity vesselBe) {
            self.source = new WeakReference<>(vesselBe);
            cir.setReturnValue(vesselBe.getControllerBE());
        }
    }

    @Inject(
            method = "isValid()Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
            ),
            cancellable = true
    )
    private void isValid(CallbackInfoReturnable<Boolean> cir) {
        SteamEngineBlockEntity self = (SteamEngineBlockEntity) (Object) this;
        Direction dir = SteamEngineBlock.getConnectedDirection(self.getBlockState()).getOpposite();

        Level level = self.getLevel();
        if (level == null) {
            cir.setReturnValue(false);
            return;
        }

        BlockState state = level.getBlockState(self.getBlockPos().relative(dir));
        boolean isValid = state.is(AllBlocks.FLUID_TANK.get()) || state.is(CCBlocks.FLUID_VESSEL.get());

        cir.setReturnValue(isValid);
    }
}
