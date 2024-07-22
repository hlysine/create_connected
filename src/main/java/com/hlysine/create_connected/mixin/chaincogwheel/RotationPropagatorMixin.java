package com.hlysine.create_connected.mixin.chaincogwheel;

import com.hlysine.create_connected.CCBlocks;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.chainDrive.ChainDriveBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RotationPropagator.class, remap = false)
public class RotationPropagatorMixin {
    @Inject(
            method = "getRotationSpeedModifier(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;)F",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/chainDrive/ChainDriveBlock;areBlocksConnected(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z"),
            cancellable = true
    )
    private static void handleChainCogwheels(KineticBlockEntity from,
                                             KineticBlockEntity to,
                                             CallbackInfoReturnable<Float> cir) {
        final BlockState stateFrom = from.getBlockState();
        final BlockState stateTo = to.getBlockState();
        Block fromBlock = stateFrom.getBlock();
        Block toBlock = stateTo.getBlock();
        final IRotate definitionFrom = (IRotate) fromBlock;
        final IRotate definitionTo = (IRotate) toBlock;
        final BlockPos diff = to.getBlockPos()
                .subtract(from.getBlockPos());
        final Direction direction = Direction.fromDelta(diff.getX(), diff.getY(), diff.getZ());

        if (stateFrom.is(CCBlocks.ENCASED_CHAIN_COGWHEEL.get()) && stateTo.is(CCBlocks.ENCASED_CHAIN_COGWHEEL.get())) {
            if (direction == null) {
                cir.setReturnValue(0f);
                return;
            }
            boolean connected = ChainDriveBlock.areBlocksConnected(stateFrom, stateTo, direction);
            if (!connected) {
                if (direction.getAxis() == definitionFrom.getRotationAxis(stateFrom)) {
                    cir.setReturnValue(0f);
                    return;
                }
                if (definitionFrom.getRotationAxis(stateFrom) == definitionTo.getRotationAxis(stateTo)) {
                    cir.setReturnValue(-1f);
                    return;
                }
                cir.setReturnValue(0f);
            }
        } else if (stateFrom.getBlock() instanceof ChainDriveBlock && stateTo.getBlock() instanceof ChainDriveBlock) {
            if (direction == null) {
                cir.setReturnValue(0f);
            }
        }
    }
}
