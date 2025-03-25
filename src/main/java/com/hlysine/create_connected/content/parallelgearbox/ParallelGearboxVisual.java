package com.hlysine.create_connected.content.parallelgearbox;


import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.AbstractInstance;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.model.Models;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
public class ParallelGearboxVisual extends KineticBlockEntityVisual<ParallelGearboxBlockEntity> {
    protected final EnumMap<Direction, RotatingInstance> keys = new EnumMap(Direction.class);
    protected Direction sourceFacing;

    public ParallelGearboxVisual(VisualizationContext context, ParallelGearboxBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        Direction.Axis boxAxis = blockState.getValue(BlockStateProperties.AXIS);
        updateSourceFacing();
        Instancer<RotatingInstance> instancer = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF));

        for(Direction direction : Iterate.directions) {
            Direction.Axis axis = direction.getAxis();
            if (boxAxis != axis) {
                RotatingInstance instance = instancer.createInstance();
                instance.setup(blockEntity, axis, getSpeed(direction)).setPosition(getVisualPosition()).rotateToFace(Direction.SOUTH, direction).setChanged();
                keys.put(direction, instance);
            }
        }

    }

    private float getSpeed(Direction direction) {
        float speed = blockEntity.getSpeed();

        if (speed != 0 && sourceFacing != null) {
            speed *= ParallelGearboxBlockEntity.getRotationSpeedModifier(direction, sourceFacing);
        }
        return speed;
    }

    protected void updateSourceFacing() {
        if (blockEntity.hasSource()) {
            BlockPos source = blockEntity.source.subtract(pos);
            sourceFacing = Direction.getNearest((float)source.getX(), (float)source.getY(), (float)source.getZ());
        } else {
            sourceFacing = null;
        }

    }

    public void update(float pt) {
        updateSourceFacing();
        for(Map.Entry<Direction, RotatingInstance> key : keys.entrySet()) {
            Direction direction = key.getKey();
            Direction.Axis axis = direction.getAxis();
            (key.getValue()).setup(blockEntity, axis, getSpeed(direction)).setChanged();
        }

    }

    public void updateLight(float partialTick) {
        relight(keys.values().toArray(FlatLit[]::new));
    }

    protected void _delete() {
        keys.values().forEach(AbstractInstance::delete);
        keys.clear();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        keys.values().forEach(consumer);
    }
}
