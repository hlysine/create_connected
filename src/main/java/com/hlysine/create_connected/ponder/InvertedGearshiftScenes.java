package com.hlysine.create_connected.ponder;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class InvertedGearshiftScenes {
    public static void invertedGearshift(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("inverted_gearshift", "Controlling rotational force using an Inverted Gearshift");
        scene.configureBasePlate(0, 0, 5);
        scene.world().showSection(util.select().layer(0), Direction.UP);
        BlockPos leverPos = util.grid().at(3, 1, 0);
        scene.world().showSection(util.select().fromTo(leverPos, leverPos.south()), Direction.UP);

        scene.idle(5);
        scene.world().showSection(util.select().position(5, 1, 2), Direction.DOWN);
        scene.idle(10);

        for (int i = 4; i >= 1; i--) {
            scene.idle(5);
            scene.world().showSection(util.select().position(i, 1, 2), Direction.DOWN);
        }

        BlockPos gearshift = util.grid().at(3, 1, 2);
        scene.idle(10);
        scene.overlay().showText(60)
                .placeNearTarget()
                .text("Inverted Gearshifts will reverse rotation in a straight line")
                .pointAt(util.vector().topOf(gearshift));

        scene.idle(60);
        scene.world().toggleRedstonePower(util.select().fromTo(leverPos, leverPos.south(2)));
        scene.effects().indicateRedstone(leverPos);
        scene.world().modifyKineticSpeed(util.select().fromTo(0, 1, 2, 2, 2, 2), f -> -f);
        scene.effects().rotationDirectionIndicator(gearshift.east(2));
        scene.effects().rotationDirectionIndicator(gearshift.west(2));
        scene.idle(30);

        scene.overlay().showText(80)
                .colored(PonderPalette.RED)
                .placeNearTarget()
                .text("When powered by Redstone, it relays rotation with its direction unchanged")
                .attachKeyFrame()
                .pointAt(util.vector().topOf(gearshift));

        for (int i = 0; i < 3; i++) {
            scene.idle(60);
            scene.world().toggleRedstonePower(util.select().fromTo(leverPos, leverPos.south(2)));
            scene.effects().indicateRedstone(leverPos);
            scene.world().modifyKineticSpeed(util.select().fromTo(0, 1, 2, 2, 2, 2), f -> -f);
            scene.effects().rotationDirectionIndicator(gearshift.east(2));
            scene.effects().rotationDirectionIndicator(gearshift.west(2));
        }
    }
}
