package com.hlysine.create_connected.ponder;

import com.simibubi.create.foundation.ponder.PonderPalette;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class InvertedClutchScenes {
    public static void invertedClutch(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("inverted_clutch", "Controlling rotational force using an Inverted Clutch");
        scene.configureBasePlate(0, 0, 5);
        scene.world.showSection(util.select.layer(0), Direction.UP);
        BlockPos leverPos = util.grid.at(3, 1, 0);
        scene.world.showSection(util.select.fromTo(leverPos, leverPos.south()), Direction.UP);

        BlockPos gaugePos = util.grid.at(0, 1, 2);
        Selection gauge = util.select.position(gaugePos);
        scene.world.showSection(gauge, Direction.UP);

        scene.idle(5);
        scene.world.showSection(util.select.position(5, 1, 2), Direction.DOWN);
        scene.idle(10);

        for (int i = 4; i >= 1; i--) {
            scene.idle(5);
            scene.world.showSection(util.select.position(i, 1, 2), Direction.DOWN);
        }

        BlockPos clutch = util.grid.at(3, 1, 2);

        scene.effects.indicateSuccess(gaugePos);
        scene.idle(10);
        scene.overlay.showText(50)
                .text("Inverted Clutches will not relay rotation...")
                .placeNearTarget()
                .pointAt(util.vector.topOf(clutch));

        scene.idle(60);
        scene.world.toggleRedstonePower(util.select.fromTo(leverPos, leverPos.south(2)));
        scene.effects.indicateRedstone(leverPos);
        scene.world.setKineticSpeed(util.select.fromTo(0, 1, 2, 2, 1, 2), 32);
        scene.idle(10);

        scene.idle(10);
        scene.overlay.showText(50)
                .colored(PonderPalette.RED)
                .text("...unless they are powered by Redstone")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.topOf(clutch));

        scene.idle(70);
        scene.markAsFinished();
    }
}
