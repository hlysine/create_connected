package com.hlysine.create_connected.content.kineticbattery;

import java.util.ArrayList;
import java.util.function.Consumer;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import com.simibubi.create.foundation.render.AllInstanceTypes;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.AbstractInstance;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.model.Models;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;

public class KineticBatteryVisual extends KineticBlockEntityVisual<KineticBatteryBlockEntity> {

    protected final ArrayList<RotatingInstance> keys;

    public KineticBatteryVisual(VisualizationContext modelManager, KineticBatteryBlockEntity blockEntity, float partialTick) {
        super(modelManager, blockEntity, partialTick);

        keys = new ArrayList<>(2);

        float speed = blockEntity.getSpeed();

        for (Direction dir : Iterate.directionsInAxis(rotationAxis())) {

            float splitSpeed = speed * blockEntity.getRotationSpeedModifier(dir);

            var instance = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF))
                    .createInstance();

            instance.setup(blockEntity, splitSpeed)
                    .setPosition(getVisualPosition())
                    .rotateToFace(Direction.SOUTH, dir)
                    .setChanged();

            keys.add(instance);
        }
    }

    @Override
    public void update(float pt) {
        Block block = blockState.getBlock();
        final Direction.Axis boxAxis = ((IRotate) block).getRotationAxis(blockState);

        Direction[] directions = Iterate.directionsInAxis(boxAxis);

        for (int i : Iterate.zeroAndOne) {
            keys.get(i)
                    .setup(blockEntity, blockEntity.getSpeed() * blockEntity.getRotationSpeedModifier(directions[i]))
                    .setChanged();
        }
    }

    @Override
    public void updateLight(float partialTick) {
        relight(keys.toArray(FlatLit[]::new));
    }

    @Override
    protected void _delete() {
        keys.forEach(AbstractInstance::delete);
        keys.clear();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        keys.forEach(consumer);
    }
}
