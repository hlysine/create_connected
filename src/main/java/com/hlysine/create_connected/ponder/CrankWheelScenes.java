package com.hlysine.create_connected.ponder;

import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.Selection;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class CrankWheelScenes {
    public static void crankWheel(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("crank_wheel", "Generating Rotational Force using Crank Wheels");
        scene.configureBasePlate(0, 0, 5);
        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.idle(5);

        BlockPos gaugePos1 = util.grid.at(0, 3, 3);
        BlockPos handlePos1 = util.grid.at(1, 2, 1);
        Selection handleSelect1 = util.select.position(handlePos1);
        BlockPos gaugePos2 = util.grid.at(3, 3, 3);
        BlockPos handlePos2 = util.grid.at(4, 2, 1);
        Selection handleSelect2 = util.select.position(handlePos2);

        scene.world.showSection(util.select.layersFrom(1).substract(handleSelect1).substract(handleSelect2), Direction.DOWN);
        scene.idle(10);
        scene.world.showSection(handleSelect1, Direction.DOWN);
        scene.world.showSection(handleSelect2, Direction.DOWN);
        scene.idle(20);

        Vec3 centerOf1 = util.vector.centerOf(handlePos1);
        Vec3 sideOf1 = centerOf1.add(-0.5, 0, 0);
        Vec3 centerOf2 = util.vector.centerOf(handlePos2);
        Vec3 sideOf2 = centerOf2.add(-0.5, 0, 0);

        scene.overlay.showText(70)
                .text("Crank Wheels can be used by players to apply rotational force manually")
                .placeNearTarget()
                .pointAt(sideOf1);
        scene.idle(80);

        scene.overlay.showControls(new InputWindowElement(centerOf1, Pointing.DOWN).rightClick(), 40);
        scene.overlay.showControls(new InputWindowElement(centerOf2, Pointing.DOWN).rightClick(), 40);
        scene.idle(7);
        scene.world.setKineticSpeed(util.select.everywhere(), 32);
        scene.world.modifyKineticSpeed(util.select.column(0, 3), f -> f * -0.5f);
        scene.world.modifyKineticSpeed(util.select.column(3, 3), f -> f * -2f);
        scene.effects.rotationDirectionIndicator(handlePos1);
        scene.effects.rotationDirectionIndicator(handlePos2);
        scene.effects.indicateSuccess(gaugePos1);
        scene.effects.indicateSuccess(gaugePos2);
        scene.idle(10);
        scene.overlay.showText(50)
                .text("Hold Right-Click to rotate it Counter-Clockwise")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(sideOf1);

        scene.idle(35);
        scene.world.setKineticSpeed(util.select.everywhere(), 0);
        scene.idle(15);

        scene.overlay.showControls(new InputWindowElement(centerOf1, Pointing.DOWN).rightClick()
                .whileSneaking(), 40);
        scene.overlay.showControls(new InputWindowElement(centerOf2, Pointing.DOWN).rightClick()
                .whileSneaking(), 40);
        scene.idle(7);
        scene.world.setKineticSpeed(util.select.everywhere(), -32);
        scene.world.modifyKineticSpeed(util.select.column(0, 3), f -> f * -0.5f);
        scene.world.modifyKineticSpeed(util.select.column(3, 3), f -> f * -2f);
        scene.effects.rotationDirectionIndicator(handlePos1);
        scene.effects.rotationDirectionIndicator(handlePos2);
        scene.effects.indicateSuccess(gaugePos1);
        scene.effects.indicateSuccess(gaugePos2);
        scene.idle(10);
        scene.overlay.showText(90)
                .text("Sneak and Hold Right-Click to rotate it Clockwise")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(sideOf1);

        scene.idle(35);
        scene.world.setKineticSpeed(util.select.everywhere(), 0);
        scene.idle(45);
    }
}
