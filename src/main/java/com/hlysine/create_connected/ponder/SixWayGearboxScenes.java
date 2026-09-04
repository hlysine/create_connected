package com.hlysine.create_connected.ponder;

import com.hlysine.create_connected.registries.CCBlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class SixWayGearboxScenes {
    public static void sixWayGearbox(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("six_way_gearbox", "Relaying rotational force using 6-way Gearboxes");
        scene.configureBasePlate(0, 0, 5);
        scene.setSceneOffsetY(-1);

        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.world().showSection(util.select().fromTo(2, 0, 5, 2, 2, 5), Direction.UP);
        scene.idle(10);

        BlockPos center = util.grid().at(2, 2, 2);
        BlockPos cogFront = util.grid().at(2, 2, 1);
        BlockPos cogBack = util.grid().at(2, 2, 3);
        BlockPos cogRight = util.grid().at(1, 2, 2);
        BlockPos cogLeft = util.grid().at(3, 2, 2);
        BlockPos cogTop = util.grid().at(2, 3, 2);
        BlockPos cogBottom = util.grid().at(2, 1, 2);

        scene.world().showSection(util.select().position(cogBack), Direction.SOUTH);
        scene.world().showSection(util.select().position(cogBack.south()), Direction.SOUTH);
        scene.idle(5);
        scene.world().showSection(util.select().position(cogRight), Direction.EAST);
        scene.world().showSection(util.select().position(cogRight.west()), Direction.EAST);
        scene.world().showSection(util.select().position(cogLeft), Direction.WEST);
        scene.world().showSection(util.select().position(cogLeft.east()), Direction.WEST);
        scene.idle(5);
        scene.world().showSection(util.select().position(cogFront), Direction.SOUTH);
        scene.world().showSection(util.select().position(cogFront.north()), Direction.SOUTH);
        scene.idle(5);
        ElementLink<WorldSectionElement> topMovableCog = scene.world().showIndependentSection(util.select().position(cogTop), Direction.DOWN);
        scene.world().moveSection(topMovableCog, util.vector().of(0, -0.5, 0), 5);
        ElementLink<WorldSectionElement> bottomMovableCog = scene.world().showIndependentSection(util.select().position(cogBottom), Direction.UP);
        scene.world().moveSection(bottomMovableCog, util.vector().of(0, 0.5, 0), 5);

        scene.idle(10);

        scene.overlay().showText(80)
                .colored(PonderPalette.RED)
                .pointAt(util.vector().blockSurface(cogLeft.west(), Direction.WEST))
                .placeNearTarget()
                .text("This setup is simple but currently impossible");
        scene.idle(80);
        Selection gearbox = util.select().position(center);
        scene.world().hideSection(util.select().fromTo(1, 2, 1, 3, 2, 3)
                .substract(gearbox), Direction.UP);
        scene.world().hideIndependentSection(topMovableCog, Direction.UP);
        scene.world().hideIndependentSection(bottomMovableCog, Direction.DOWN);
        scene.idle(20);

        BlockState defaultState = AllBlocks.SHAFT.getDefaultState();
        BlockState cogState = AllBlocks.COGWHEEL.getDefaultState();
        scene.world().setBlock(cogFront, defaultState.setValue(CogWheelBlock.AXIS, Direction.Axis.Z), false);
        scene.world().setBlock(cogBack, defaultState.setValue(CogWheelBlock.AXIS, Direction.Axis.Z), false);
        scene.world().setBlock(cogRight, defaultState.setValue(CogWheelBlock.AXIS, Direction.Axis.X), false);
        scene.world().setBlock(cogLeft, defaultState.setValue(CogWheelBlock.AXIS, Direction.Axis.X), false);
        scene.world().setBlock(cogTop, defaultState.setValue(CogWheelBlock.AXIS, Direction.Axis.Y), false);
        scene.world().setBlock(cogBottom, defaultState.setValue(CogWheelBlock.AXIS, Direction.Axis.Y), false);
        scene.world().setBlock(center, CCBlocks.SIX_WAY_GEARBOX.getDefaultState(), false);
        scene.world().showSection(util.select().fromTo(1, 1, 1, 3, 3, 3), Direction.DOWN);

        scene.idle(20);
        scene.overlay().showText(80)
                .colored(PonderPalette.GREEN)
                .pointAt(util.vector().topOf(center))
                .placeNearTarget()
                .attachKeyFrame()
                .text("A 6-way gearbox encapsulates this into a block");

        scene.idle(90);
        scene.world().setBlock(cogFront.north(), cogState.setValue(CogWheelBlock.AXIS, Direction.Axis.Z), true);
        scene.world().setBlock(cogRight.west(), cogState.setValue(CogWheelBlock.AXIS, Direction.Axis.X), true);
        scene.idle(10);
        scene.effects().rotationDirectionIndicator(cogFront.north());
        scene.effects().rotationDirectionIndicator(cogRight.west());
        scene.idle(15);
        scene.overlay().showText(70)
                .pointAt(util.vector().centerOf(cogRight.west()))
                .placeNearTarget()
                .text("Shafts around corners rotate in the same direction");

        scene.idle(80);

        scene.world().hideSection(util.select().fromTo(0, 2, 2, 1, 2, 2), Direction.WEST);
        scene.world().hideSection(util.select().fromTo(3, 2, 2, 4, 2, 2), Direction.EAST);
        scene.world().setBlock(cogTop, cogState.setValue(CogWheelBlock.AXIS, Direction.Axis.Y), true);
        scene.world().setBlock(cogBottom, cogState.setValue(CogWheelBlock.AXIS, Direction.Axis.Y), true);
        scene.world().showSection(util.select().position(cogTop), Direction.DOWN);
        scene.world().showSection(util.select().position(cogBottom), Direction.UP);
        scene.idle(10);

        scene.effects().rotationDirectionIndicator(cogBottom);
        scene.effects().rotationDirectionIndicator(cogTop);
        scene.idle(15);
        scene.overlay().showText(70)
                .pointAt(util.vector().centerOf(cogTop))
                .placeNearTarget()
                .text("Shafts at the top and bottom rotate at half speed");
        scene.idle(80);
        scene.markAsFinished();
    }
}
