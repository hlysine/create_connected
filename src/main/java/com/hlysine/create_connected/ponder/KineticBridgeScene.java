package com.hlysine.create_connected.ponder;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class KineticBridgeScene {
    public static void kineticBridge(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("kinetic_bridge", "Relaying rotational force using a Kinetic Bridge");
        scene.configureBasePlate(0, 0, 7);

        Selection waterWheel = util.select().fromTo(0, 0, 7, 6, 3, 7);
        Selection rotationSpeedController = util.select().fromTo(5, 1, 6, 5, 2, 6);
        Selection sourceGroup = util.select().fromTo(1, 1, 6, 4, 1, 6);
        Selection sourceNetwork = util.select().fromTo(1, 1, 5, 5, 2, 7);
        BlockPos sourceShaft = util.grid().at(1, 1, 5);
        BlockPos bridgeSource = util.grid().at(1, 1, 4);
        Selection kineticBridge = util.select().fromTo(1, 1, 3, 1, 1, 4);
        BlockPos destShaft = util.grid().at(1, 1, 2);
        Selection destGroup = util.select().fromTo(1, 1, 1, 4, 1, 1);
        Selection destNetwork = util.select().fromTo(1, 1, 1, 4, 1, 2);
        BlockPos extraSaw = util.grid().at(5, 1, 1);

        scene.showBasePlate();
        scene.idle(10);
        scene.world().showSection(waterWheel, Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(rotationSpeedController, Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(sourceGroup, Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().position(sourceShaft), Direction.DOWN);
        scene.idle(15);
        scene.world().showSection(kineticBridge, Direction.DOWN);
        scene.idle(15);
        scene.world().showSection(util.select().position(destShaft), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(destGroup, Direction.DOWN);
        scene.idle(20);

        scene.overlay().showText(100)
                .text("A Kinetic Bridge transfers a constant amount of stress capacity from one network to another...")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector().topOf(bridgeSource));
        scene.idle(120);

        scene.overlay().showOutline(PonderPalette.BLUE, sourceNetwork, sourceNetwork, 90);
        scene.idle(10);
        scene.overlay().showOutline(PonderPalette.GREEN, destNetwork, destNetwork, 80);
        scene.idle(10);

        scene.overlay().showText(70)
                .text("...while keeping the two networks separate")
                .independent();

        scene.idle(90);

        scene.overlay().showControls(util.vector().blockSurface(bridgeSource, Direction.UP), Pointing.DOWN, 40).rightClick();
        scene.idle(20);
        scene.overlay().showText(90)
                .text("The amount of stress capacity transferred can be changed via the value panel")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector().topOf(bridgeSource));
        scene.idle(100);
    }
}
