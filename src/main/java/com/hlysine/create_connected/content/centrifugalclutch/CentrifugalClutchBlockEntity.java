package com.hlysine.create_connected.content.centrifugalclutch;

import com.hlysine.create_connected.Lang;
import com.hlysine.create_connected.content.RotationScrollValueBehaviour;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static com.hlysine.create_connected.content.centrifugalclutch.CentrifugalClutchBlock.UNCOUPLED;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class CentrifugalClutchBlockEntity extends SplitShaftBlockEntity {

    public static final int DEFAULT_SPEED = 64;
    public static final int MAX_SPEED = 256;

    public ScrollValueBehaviour speedThreshold;

    private boolean reattachNextTick = false;

    public CentrifugalClutchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        speedThreshold = new RotationScrollValueBehaviour(
                Lang.translateDirect("centrifugal_clutch.speed_threshold"),
                this,
                new CenteredSideValueBoxTransform((state, d) -> {
                    Direction.Axis axis = d.getAxis();
                    Direction.Axis bearingAxis = state.getValue(FACING).getAxis();
                    return bearingAxis != axis;
                }) {
                    @Override
                    public void rotate(BlockState state, PoseStack ms) {
                        Direction facing = getSide();
                        float xRot = facing == Direction.UP ? 90 : facing == Direction.DOWN ? 270 : 0;
                        float yRot = AngleHelper.horizontalAngle(facing) + 180;

                        if (facing.getAxis() == Direction.Axis.Y)
                            TransformStack.cast(ms)
                                    .rotateY(180 + AngleHelper.horizontalAngle(state.getValue(FACING)));

                        TransformStack.cast(ms)
                                .rotateY(yRot)
                                .rotateX(xRot);
                    }
                }
        );
        speedThreshold.between(0, MAX_SPEED);
        speedThreshold.value = DEFAULT_SPEED;
        speedThreshold.withCallback(i -> this.updateThreshold());
        behaviours.add(speedThreshold);
    }

    @Override
    public void initialize() {
        updateThreshold();
        super.initialize();
    }

    private void updateThreshold() {
        KineticNetwork network = getOrCreateNetwork();
        updateFromNetwork(capacity, stress, network == null ? 0 : network.getSize());
    }

    @Override
    public void updateFromNetwork(float maxStress, float currentStress, int networkSize) {
        super.updateFromNetwork(maxStress, currentStress, networkSize);

        boolean coupled = !getBlockState().getValue(UNCOUPLED);
        boolean thresholdReached = Mth.abs(getSpeed()) >= Mth.abs(speedThreshold.getValue()) && Mth.abs(getSpeed()) > 0;
        if (coupled != thresholdReached) {
            if (level != null) {
                level.setBlockAndUpdate(getBlockPos(), getBlockState().cycle(UNCOUPLED));
                RotationPropagator.handleRemoved(level, getBlockPos(), this);
                reattachNextTick = true;
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

