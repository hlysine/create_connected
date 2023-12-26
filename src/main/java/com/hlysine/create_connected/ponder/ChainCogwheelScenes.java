package com.hlysine.create_connected.ponder;

import com.simibubi.create.content.kinetics.chainDrive.ChainDriveBlock;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.Selection;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class ChainCogwheelScenes {
    public static void chainCogwheelAsRelay(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("chain_cogwheel_relay", "Relaying rotational force with Chain Cogwheels");
        scene.configureBasePlate(0, 0, 5);
        scene.world.showSection(util.select.layer(0), Direction.UP);
        BlockPos gaugePos = util.grid.at(0, 1, 3);
        Selection gauge = util.select.position(gaugePos);
        scene.world.showSection(gauge, Direction.UP);
        scene.world.setKineticSpeed(gauge, 0);

        scene.idle(5);
        scene.world.showSection(util.select.fromTo(5, 1, 2, 4, 1, 2), Direction.DOWN);
        scene.idle(10);

        for (int i = 0; i < 3; i++) {
            scene.idle(5);
            scene.world.showSection(util.select.position(3, 1, 2 - i), Direction.DOWN);
            if (i != 0)
                scene.world.showSection(util.select.position(3, 1, 2 + i), Direction.DOWN);
        }

        scene.idle(10);
        scene.world.showSection(util.select.position(gaugePos.east(2)), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(gaugePos.east()), Direction.DOWN);
        scene.idle(5);

        scene.world.setKineticSpeed(gauge, 64);
        scene.effects.indicateSuccess(gaugePos);
        scene.idle(20);
        scene.overlay.showText(60)
                .text("Chain Cogwheels are Chain Drives with an extra cogwheel")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.blockSurface(util.grid.at(3, 1, 3), Direction.WEST));
        scene.idle(60);
        scene.overlay.showText(60)
                .text("They connect to other chained components in a row")
                .placeNearTarget()
                .pointAt(util.vector.blockSurface(util.grid.at(3, 1, 3), Direction.WEST));
        scene.idle(60);

        Selection shafts = util.select.fromTo(2, 1, 0, 2, 1, 1);
        BlockPos rotatedECD = util.grid.at(3, 1, 0);
        Selection verticalShaft = util.select.fromTo(rotatedECD.above(), rotatedECD.above(2));
        Selection extraLargeCog = util.select.position(3, 2, 4);
        Selection extraCog = util.select.position(4, 1, 0);

        scene.world.showSection(shafts, Direction.EAST);
        scene.idle(10);
        scene.effects.rotationDirectionIndicator(util.grid.at(2, 1, 0));
        scene.effects.rotationDirectionIndicator(util.grid.at(2, 1, 1));
        scene.idle(20);
        scene.overlay.showText(60)
                .text("All shafts connected like this will rotate in the same direction")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.blockSurface(util.grid.at(2, 1, 1), Direction.WEST));
        scene.idle(50);
        scene.world.hideSection(shafts, Direction.WEST);
        scene.idle(25);

        scene.world.showSection(extraLargeCog, Direction.DOWN);
        scene.idle(10);
        scene.overlay.showText(60)
                .text("Cogwheels can connect to Chain Cogwheels anywhere in the row")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(extraLargeCog.getCenter());
        scene.idle(60);

        scene.addKeyframe();
        scene.overlay.showControls(new InputWindowElement(util.vector.topOf(rotatedECD), Pointing.DOWN).rightClick()
                .withWrench(), 30);
        scene.idle(7);
        scene.world.modifyBlock(rotatedECD, s -> s.setValue(ChainDriveBlock.AXIS, Direction.Axis.Y), true);
        scene.idle(40);

        scene.world.showSection(verticalShaft, Direction.DOWN);
        scene.world.showSection(extraCog, Direction.DOWN);
        scene.idle(10);

        scene.effects.rotationDirectionIndicator(util.grid.at(3, 3, 0));
        scene.idle(10);
        scene.overlay.showText(60)
                .text("Any part of the row can be rotated by 90 degrees")
                .placeNearTarget()
                .pointAt(util.vector.centerOf(3, 2, 0));

        scene.markAsFinished();
    }
}
