package com.hlysine.create_connected.content.centrifugalclutch;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.Lang;
import com.hlysine.create_connected.content.ClutchValueBox;
import com.hlysine.create_connected.content.RotationScrollValueBehaviour;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import java.util.List;

import static com.hlysine.create_connected.content.centrifugalclutch.CentrifugalClutchBlock.UNCOUPLED;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class CentrifugalClutchBlockEntity extends SplitShaftBlockEntity {

    public static final int DEFAULT_SPEED = 64;
    public static final int MAX_SPEED = 256;

    public ScrollValueBehaviour speedThreshold;

    public boolean reattachNextTick = false;

    public CentrifugalClutchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        speedThreshold = new RotationScrollValueBehaviour(
                Lang.translateDirect("centrifugal_clutch.speed_threshold"),
                this,
                new ClutchValueBox()
        );
        speedThreshold.between(0, MAX_SPEED);
        speedThreshold.value = DEFAULT_SPEED;
        speedThreshold.withCallback(i -> this.onKineticUpdate());
        behaviours.add(speedThreshold);
    }

    @Override
    public void initialize() {
        onKineticUpdate();
        super.initialize();
    }

    private void onKineticUpdate() {
        boolean coupled = !getBlockState().getValue(UNCOUPLED);
        boolean thresholdReached = Mth.abs(getSpeed()) >= Mth.abs(speedThreshold.getValue()) && Mth.abs(getSpeed()) > 0;
        if (coupled != thresholdReached && !isOverStressed()) {
            if (level != null) {
                level.setBlockAndUpdate(getBlockPos(), getBlockState().cycle(UNCOUPLED));
                level.scheduleTick(getBlockPos(), CCBlocks.CENTRIFUGAL_CLUTCH.get(), 0, TickPriority.EXTREMELY_HIGH);
                reattachNextTick = true;
            }
        }
    }

    @Override
    public void updateFromNetwork(float maxStress, float currentStress, int networkSize) {
        super.updateFromNetwork(maxStress, currentStress, networkSize);
        onKineticUpdate();
    }

    @Override
    public void tick() {
        super.tick();
        if (reattachNextTick && level != null) {
            reattachNextTick = false;
            RotationPropagator.handleAdded(level, getBlockPos(), this);
        }
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (face == getBlockState().getValue(FACING) && getBlockState().getValue(UNCOUPLED))
            return 0;
        return 1;
    }
}

