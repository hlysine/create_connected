package com.hlysine.create_connected.ponder;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.content.overstressclutch.OverstressClutchBlock;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.saw.SawBlock;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.ParticleEmitter;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

public class KineticBridgeScene {
    public static void kineticBridge(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("kinetic_bridge", "Relaying rotational force using a Kinetic Bridge");
        scene.configureBasePlate(0, 0, 7);

        Selection waterWheelGroup = util.select().fromTo(0, 0, 7, 6, 3, 7);
        Selection rotationSpeedController = util.select().fromTo(5, 1, 6, 5, 2, 6);
        Selection sourceGroup = util.select().fromTo(1, 1, 6, 4, 1, 6);
        Selection sourceNetwork = util.select().fromTo(1, 1, 5, 5, 2, 7);
        BlockPos sourceExtraSaw = util.grid().at(2, 1, 6);
        BlockPos sourceShaft = util.grid().at(1, 1, 5);
        BlockPos bridgeSource = util.grid().at(1, 1, 4);
        BlockPos bridgeDest = util.grid().at(1, 1, 3);
        Selection kineticBridge = util.select().fromTo(1, 1, 3, 1, 1, 4);
        BlockPos destShaft = util.grid().at(1, 1, 2);
        Selection destGroup = util.select().fromTo(1, 1, 1, 4, 1, 1);
        Selection destNetwork = util.select().fromTo(1, 1, 1, 4, 1, 2);
        BlockPos extraSaw = util.grid().at(5, 1, 1);

        scene.showBasePlate();
        scene.idle(10);
        scene.world().showSection(waterWheelGroup, Direction.DOWN);
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

        scene.overlay().showOutline(PonderPalette.INPUT, sourceNetwork, sourceNetwork, 90);
        scene.idle(10);
        scene.overlay().showOutline(PonderPalette.OUTPUT, destNetwork, destNetwork, 80);
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

        scene.world().setBlock(extraSaw, AllBlocks.MECHANICAL_SAW.getDefaultState().setValue(SawBlock.FACING, Direction.UP), false);
        scene.world().showSection(util.select().position(extraSaw), Direction.DOWN);
        scene.idle(10);
        ParticleEmitter smoke = scene.effects().particleEmitterWithinBlockSpace(ParticleTypes.SMOKE, util.vector().of(0, 0, 0));
        scene.effects().emitParticles(Vec3.atCenterOf(extraSaw), smoke, 15, 1);
        scene.effects().emitParticles(Vec3.atCenterOf(destShaft), smoke, 15, 1);
        scene.world().setKineticSpeed(destNetwork.add(util.select().position(bridgeDest)), 0);
        scene.idle(20);

        scene.overlay().showText(80)
                .text("When one network is overstressed, the other network is unaffected")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector().topOf(bridgeSource));
        scene.idle(100);

        scene.world().hideSection(util.select().position(extraSaw), Direction.UP);
        scene.idle(5);
        scene.world().restoreBlocks(destNetwork.add(util.select().position(bridgeDest)));
        ParticleEmitter cloud = scene.effects().particleEmitterWithinBlockSpace(ParticleTypes.CLOUD, util.vector().of(0, 0, 0));
        scene.effects().emitParticles(Vec3.atCenterOf(destShaft), cloud, 5, 1);
        scene.idle(20);

        scene.world().hideSection(util.select().position(sourceExtraSaw), Direction.UP);
        scene.idle(20);
        scene.world().setBlock(sourceExtraSaw, AllBlocks.MECHANICAL_SAW.getDefaultState().setValue(SawBlock.FACING, Direction.UP), false);
        scene.world().showSection(util.select().position(sourceExtraSaw), Direction.DOWN);
        scene.idle(10);
        scene.effects().emitParticles(Vec3.atCenterOf(sourceExtraSaw), smoke, 15, 1);
        scene.effects().emitParticles(Vec3.atCenterOf(sourceShaft), smoke, 15, 1);
        scene.world().setKineticSpeed(util.select().everywhere(), 0);
        scene.idle(20);

        scene.overlay().showText(120)
                .text("However, when the source network is overstressed, the bridge is unable to transfer the consumed capacity")
                .colored(PonderPalette.RED)
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector().topOf(bridgeDest));
        scene.idle(140);

        scene.world().hideSection(util.select().position(sourceShaft), Direction.UP);
        scene.idle(20);
        scene.world().setBlock(sourceShaft, CCBlocks.OVERSTRESS_CLUTCH.getDefaultState().setValue(OverstressClutchBlock.AXIS, Direction.Axis.Z), false);
        scene.world().showSection(util.select().position(sourceShaft), Direction.DOWN);
        scene.idle(20);
        scene.world().modifyBlock(sourceShaft, state -> state.setValue(OverstressClutchBlock.STATE, OverstressClutchBlock.ClutchState.UNCOUPLED), false);
        scene.effects().emitParticles(Vec3.atCenterOf(sourceShaft), smoke, 15, 1);
        scene.world().restoreBlocks(sourceNetwork.substract(util.select().position(sourceShaft)).substract(util.select().position(sourceExtraSaw)));
        scene.world().setKineticSpeed(util.select().position(sourceExtraSaw), 18);

        scene.overlay().showText(120)
                .text("Add an Overstress Clutch in front to free up the capacity consumed by an overstressed bridge")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector().topOf(sourceShaft));
        scene.idle(140);
        scene.markAsFinished();
    }
}
