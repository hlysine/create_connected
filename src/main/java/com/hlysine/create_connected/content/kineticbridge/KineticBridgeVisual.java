package com.hlysine.create_connected.content.kineticbridge;


import com.hlysine.create_connected.CCPartialModels;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.function.Consumer;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class KineticBridgeVisual extends KineticBlockEntityVisual<KineticBlockEntity> {

    protected final RotatingInstance shaft;
    protected final RotatingInstance coupling;
    final Direction direction;
    private final Direction opposite;

    public KineticBridgeVisual(VisualizationContext context, KineticBlockEntity blockEntity, float partialTick, boolean isDestination) {
        super(context, blockEntity, partialTick);

        Direction facing = blockState.getValue(FACING);
        if (isDestination) {
            facing = facing.getOpposite();
        }

        direction = facing;
        opposite = direction.getOpposite();
        shaft = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF))
                .createInstance();
        coupling = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(isDestination ? CCPartialModels.KINETIC_BRIDGE_DESTINATION : CCPartialModels.KINETIC_BRIDGE_SOURCE))
                .createInstance();

        shaft.setup(blockEntity)
                .setPosition(getVisualPosition())
                .rotateToFace(Direction.SOUTH, opposite)
                .setChanged();

        coupling.setup(blockEntity)
                .setPosition(getVisualPosition())
                .rotateToFace(Direction.SOUTH, opposite)
                .setChanged();
    }

    @Override
    public void update(float pt) {
        shaft.setup(blockEntity)
                .setChanged();
        coupling.setup(blockEntity)
                .setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        BlockPos behind = pos.relative(opposite);
        relight(behind, shaft);

        BlockPos inFront = pos.relative(direction);
        relight(inFront, coupling);
    }

    @Override
    protected void _delete() {
        shaft.delete();
        coupling.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(shaft);
        consumer.accept(coupling);
    }
}

