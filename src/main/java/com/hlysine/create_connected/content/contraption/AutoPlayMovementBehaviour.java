package com.hlysine.create_connected.content.contraption;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionWorld;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.elevator.ElevatorContraption;
import com.simibubi.create.content.decoration.slidingDoor.DoorControlBehaviour;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorMovementBehaviour;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public abstract class AutoPlayMovementBehaviour extends SlidingDoorMovementBehaviour {

    @Override
    public boolean renderAsNormalBlockEntity() {
        return false;
    }

    @Override
    public void tick(MovementContext context) {
        Contraption contraption = context.contraption;
        StructureBlockInfo structureBlockInfo = contraption.getBlocks()
                .get(context.localPos);
        if (structureBlockInfo == null)
            return;

        boolean wasActive = Objects.equals(context.temporaryData, true);
        boolean isActive = shouldOpen(context);

        BlockState state = structureBlockInfo.state();
        ContraptionWorld contraptionWorld = contraption.getContraptionWorld();
        BlockPos contraptionPos = context.localPos;
        Level realWorld = contraption.entity.level();
        BlockPos realPos = BlockPos.containing(contraption.entity.toGlobalVector(Vec3.atCenterOf(contraptionPos), 1));

        if (wasActive != isActive) {
            update(context, state, contraptionWorld, contraptionPos, realWorld, realPos, wasActive, isActive);
        }

        context.temporaryData = isActive;
    }

    protected abstract void update(MovementContext context, BlockState state, ContraptionWorld contraptionWorld, BlockPos contraptionPos, Level realWorld, BlockPos realPos, boolean wasActive, boolean isActive);

    @Override
    protected boolean shouldOpen(MovementContext context) {
        Contraption contraption = context.contraption;
        DoorControlBehaviour doorControls = null;

        if (contraption instanceof ElevatorContraption ec)
            doorControls = getElevatorDoorControl(ec, context);
        if (contraption.entity instanceof CarriageContraptionEntity cce)
            doorControls = getTrainStationDoorControl(cce, context);

        if (doorControls == null)
            return false;

        return context.motion.length() < 1 / 128f && !contraption.entity.isStalled()
                || contraption instanceof ElevatorContraption ec && ec.arrived;
    }
}


