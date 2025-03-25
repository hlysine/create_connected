package com.hlysine.create_connected.ponder;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.foundation.ponder.*;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class ParallelGearboxScenes {
    public static void parallelGearbox(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("parallel_gearbox", "Relaying rotational force using Parallel Gearboxes");
        scene.configureBasePlate(1, 1, 5);
        scene.setSceneOffsetY(-1);

        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.world().showSection(util.select().fromTo(4, 1, 6, 3, 2, 5), Direction.UP);
        scene.idle(10);

        BlockPos cogBack = util.grid().at(3, 2, 4);
        BlockPos cogLeft = util.grid().at(4, 2, 3);
        BlockPos cogFront = util.grid().at(3, 2, 2);
        BlockPos cogRight = util.grid().at(2, 2, 3);
        BlockPos cogTop = util.grid().at(3, 3, 3);

        scene.world().showSection(util.select().position(cogBack), Direction.SOUTH);
        scene.idle(5);
        scene.world().showSection(util.select().position(cogLeft), Direction.WEST);
        scene.world().showSection(util.select().position(cogLeft.east()), Direction.WEST);
        scene.world().showSection(util.select().position(cogRight), Direction.EAST);
        scene.world().showSection(util.select().position(cogRight.west()), Direction.EAST);
        scene.idle(5);
        scene.world().showSection(util.select().position(cogFront), Direction.SOUTH);
        scene.world().showSection(util.select().position(cogFront.north()), Direction.SOUTH);
        scene.idle(5);
        ElementLink<WorldSectionElement> topMovableCog = scene.world().showIndependentSection(util.select().position(cogTop), Direction.DOWN);
        scene.world().moveSection(topMovableCog, util.vector().of(0, -0.5, 0), 5);

        scene.idle(10);

        scene.overlay().showText(80)
                .colored(PonderPalette.RED)
                .pointAt(util.vector().blockSurface(cogRight.west(), Direction.WEST))
                .placeNearTarget()
                .text("This setup is simple but currently impossible");
        scene.idle(80);
        Selection gearbox = util.select().position(3, 2, 3);
        scene.world().hideSection(util.select().fromTo(4, 2, 2, 2, 2, 4)
                .substract(gearbox), Direction.UP);
        scene.world().hideIndependentSection(topMovableCog, Direction.UP);
        scene.idle(20);

        BlockState defaultState = AllBlocks.SHAFT.getDefaultState();
        BlockState cogState = AllBlocks.COGWHEEL.getDefaultState();
        scene.world().setBlock(cogBack, defaultState.setValue(CogWheelBlock.AXIS, Direction.Axis.Z), false);
        scene.world().setBlock(cogFront, defaultState.setValue(CogWheelBlock.AXIS, Direction.Axis.Z), false);
        scene.world().setBlock(cogRight, defaultState.setValue(CogWheelBlock.AXIS, Direction.Axis.X), false);
        scene.world().setBlock(cogLeft, defaultState.setValue(CogWheelBlock.AXIS, Direction.Axis.X), false);
        scene.world().showSection(util.select().fromTo(4, 2, 2, 2, 2, 4), Direction.DOWN);

        scene.idle(20);
        scene.overlay().showText(80)
                .colored(PonderPalette.GREEN)
                .pointAt(util.vector().topOf(3, 2, 3))
                .placeNearTarget()
                .attachKeyFrame()
                .text("A parallel gearbox encapsulates this into a block");

        scene.idle(90);
        scene.world().setBlock(cogFront.north(), cogState.setValue(CogWheelBlock.AXIS, Direction.Axis.Z), true);
        scene.world().setBlock(cogRight.west(), cogState.setValue(CogWheelBlock.AXIS, Direction.Axis.X), true);
        scene.idle(10);
        scene.effects().rotationDirectionIndicator(cogFront.north());
        scene.effects().rotationDirectionIndicator(cogRight.west());
        scene.idle(15);
        scene.overlay().showText(60)
                .pointAt(util.vector().of(3, 2.5, 3))
                .placeNearTarget()
                .text("Shafts around corners rotate in the same direction");

        scene.idle(70);

        scene.world().hideSection(util.select().fromTo(1, 2, 3, 2, 2, 3), Direction.WEST);
        scene.world().hideSection(util.select().fromTo(4, 2, 3, 5, 2, 3), Direction.EAST);
        scene.world().setBlock(cogBack.south(), cogState.setValue(CogWheelBlock.AXIS, Direction.Axis.Z), true);
        scene.idle(10);

        scene.effects().rotationDirectionIndicator(cogFront.north());
        scene.effects().rotationDirectionIndicator(cogBack.south());
        scene.idle(15);
        scene.overlay().showText(60)
                .pointAt(util.vector().centerOf(3, 2, 5))
                .placeNearTarget()
                .text("Straight connections will be reversed");
        scene.idle(80);
        scene.markAsFinished();
    }
}
