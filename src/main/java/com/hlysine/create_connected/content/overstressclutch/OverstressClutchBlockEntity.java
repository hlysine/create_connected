package com.hlysine.create_connected.content.overstressclutch;

import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class OverstressClutchBlockEntity extends SplitShaftBlockEntity {

    boolean detachNextTick = false;
    boolean reattachNextTick = false;

    public OverstressClutchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void updateFromNetwork(float maxStress, float currentStress, int networkSize) {
        super.updateFromNetwork(maxStress, currentStress, networkSize);

        if (IRotate.StressImpact.isEnabled()) {
            if (isOverStressed() && !getBlockState().getValue(OverstressClutchBlock.POWERED)) {
                if (level != null) {
                    detachNextTick = true;
                }
            }
        }
    }

    @Override
    public void onSpeedChanged(float prevSpeed) {
        super.onSpeedChanged(prevSpeed);
        KineticNetwork network = getOrCreateNetwork();
        updateFromNetwork(capacity, stress, network == null ? 0 : network.getSize());
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (hasSource()) {
            if (face != getSourceFacing() && getBlockState().getValue(BlockStateProperties.POWERED))
                return 0;
        }
        return 1;
    }

    public void unpowerClutch() {
        if (getBlockState().getValue(OverstressClutchBlock.POWERED)) {
            level.setBlock(getBlockPos(), getBlockState().cycle(OverstressClutchBlock.POWERED), 2 | 16);
            RotationPropagator.handleRemoved(level, getBlockPos(), this);
            reattachNextTick = true;
        }
    }

    @Override
    public void tick() {
        if (level != null) {
            if (detachNextTick) {
                detachNextTick = false;
                if (!getBlockState().getValue(OverstressClutchBlock.POWERED)) {
                    level.setBlock(getBlockPos(), getBlockState().cycle(OverstressClutchBlock.POWERED), 2 | 16);
                    RotationPropagator.handleRemoved(level, getBlockPos(), this);
                }
            }
            if (reattachNextTick) {
                reattachNextTick = false;
                RotationPropagator.handleAdded(level, getBlockPos(), this);
            }
        }
        super.tick();
    }
}

