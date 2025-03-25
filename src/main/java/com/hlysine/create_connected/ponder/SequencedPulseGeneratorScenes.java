package com.hlysine.create_connected.ponder;

import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlock;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.foundation.element.InputWindowElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.RedStoneWireBlock;

public class SequencedPulseGeneratorScenes {
    public static void pulseGenerator(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("sequenced_pulse_generator", "Controlling signals using Sequenced Pulse Generators");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        BlockPos inputButton = util.grid().at(4, 1, 2);
        BlockPos pulseGenerator = util.grid().at(2, 1, 2);
        BlockPos lamp = util.grid().at(0, 1, 2);
        BlockPos comparator = util.grid().at(2, 1, 1);
        BlockPos wire = util.grid().at(2, 1, 0);
        Selection nixie = util.select().position(1, 1, 0);
        Selection input = util.select().fromTo(4, 1, 2, 3, 1, 2);
        Selection output = util.select().fromTo(1, 1, 2, 0, 1, 2);
        Selection reset = util.select().fromTo(2, 1, 4, 2, 1, 3);
        Selection lineCounter = util.select().fromTo(2, 1, 1, 1, 1, 0);

        scene.world().showSection(input.add(output), Direction.UP);
        scene.idle(10);
        scene.world().showSection(util.select().position(pulseGenerator), Direction.DOWN);
        scene.idle(20);

        scene.overlay().showText(70)
                .text("Seq. Pulse Gen. outputs signals by following a timed list of instructions")
                .attachKeyFrame()
                .pointAt(util.vector().centerOf(pulseGenerator))
                .placeNearTarget();
        scene.idle(80);

        scene.overlay().showControls(util.vector().centerOf(pulseGenerator), Pointing.DOWN, 40).rightClick();
        scene.idle(7);
        scene.overlay().showOutlineWithText(util.select().position(pulseGenerator), 60)
                .colored(PonderPalette.BLUE)
                .text("Right-click it to open the Configuration UI")
                .pointAt(util.vector().centerOf(pulseGenerator))
                .placeNearTarget();
        scene.idle(80);

        scene.world().toggleRedstonePower(input);
        scene.effects().indicateRedstone(inputButton);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERED);

        scene.idle(2);
        scene.world().toggleRedstonePower(output);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERING);

        scene.idle(10);

        scene.overlay().showText(50)
                .text("Upon receiving a signal from its back...")
                .attachKeyFrame()
                .pointAt(util.vector().centerOf(pulseGenerator))
                .placeNearTarget();

        scene.idle(8);
        scene.world().toggleRedstonePower(input);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERED);
        scene.idle(22);
        scene.world().toggleRedstonePower(output);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERING);
        scene.idle(20);
        scene.world().toggleRedstonePower(output);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERING);
        scene.idle(10);

        scene.overlay().showText(70)
                .text("...it will start running its configured sequence")
                .pointAt(util.vector().topOf(lamp))
                .placeNearTarget();

        scene.idle(20);
        scene.world().toggleRedstonePower(output);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERING);
        scene.idle(20);
        scene.world().toggleRedstonePower(output);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERING);
        scene.idle(10);
        scene.world().toggleRedstonePower(output);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERING);

        scene.idle(40);

        scene.overlay().showText(70)
                .text("Once finished, it waits for the next signal and starts over")
                .attachKeyFrame()
                .pointAt(util.vector().centerOf(pulseGenerator))
                .placeNearTarget();

        scene.idle(90);

        scene.world().toggleRedstonePower(input);
        scene.effects().indicateRedstone(inputButton);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERED);

        scene.idle(2);
        scene.world().toggleRedstonePower(output);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERING);
        scene.world().toggleRedstonePower(util.select().position(comparator));
        scene.world().cycleBlockProperty(wire, RedStoneWireBlock.POWER);
        scene.world().modifyBlockEntityNBT(nixie, NixieTubeBlockEntity.class, nbt -> nbt.putInt("RedstoneStrength", 1));
        scene.idle(18);
        scene.world().toggleRedstonePower(input);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERED);

        scene.world().showSection(lineCounter, Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(80)
                .text("A redstone comparator can be used to read the current progress")
                .attachKeyFrame()
                .pointAt(util.vector().centerOf(comparator));

        scene.idle(12);
        scene.world().toggleRedstonePower(output);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERING);
        scene.world().cycleBlockProperty(wire, RedStoneWireBlock.POWER);
        scene.world().modifyBlockEntityNBT(nixie, NixieTubeBlockEntity.class, nbt -> nbt.putInt("RedstoneStrength", 2));
        scene.idle(20);
        scene.world().toggleRedstonePower(output);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERING);
        scene.world().cycleBlockProperty(wire, RedStoneWireBlock.POWER);
        scene.world().modifyBlockEntityNBT(nixie, NixieTubeBlockEntity.class, nbt -> nbt.putInt("RedstoneStrength", 3));
        scene.idle(30);
        scene.world().toggleRedstonePower(output);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERING);
        scene.world().cycleBlockProperty(wire, RedStoneWireBlock.POWER);
        scene.world().modifyBlockEntityNBT(nixie, NixieTubeBlockEntity.class, nbt -> nbt.putInt("RedstoneStrength", 4));
        scene.idle(20);
        scene.world().toggleRedstonePower(output);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERING);
        scene.world().cycleBlockProperty(wire, RedStoneWireBlock.POWER);
        scene.world().modifyBlockEntityNBT(nixie, NixieTubeBlockEntity.class, nbt -> nbt.putInt("RedstoneStrength", 5));
        scene.idle(10);
        scene.world().toggleRedstonePower(output);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERING);
        scene.world().modifyBlock(wire, state -> state.setValue(RedStoneWireBlock.POWER, 0), false);
        scene.world().modifyBlockEntityNBT(nixie, NixieTubeBlockEntity.class, nbt -> nbt.putInt("RedstoneStrength", 0));

        scene.idle(40);

        scene.world().showSection(reset, Direction.NORTH);

        scene.idle(30);

        scene.world().toggleRedstonePower(input);
        scene.effects().indicateRedstone(inputButton);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERED);

        scene.idle(2);
        scene.world().toggleRedstonePower(output);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERING);
        scene.world().toggleRedstonePower(util.select().position(comparator));
        scene.world().cycleBlockProperty(wire, RedStoneWireBlock.POWER);
        scene.world().modifyBlockEntityNBT(nixie, NixieTubeBlockEntity.class, nbt -> nbt.putInt("RedstoneStrength", 1));
        scene.idle(18);

        scene.overlay().showText(60)
                .text("Upon receiving a signal from its sides...")
                .attachKeyFrame()
                .pointAt(util.vector().blockSurface(pulseGenerator.south(), Direction.DOWN))
                .placeNearTarget();

        scene.idle(10);
        scene.world().toggleRedstonePower(input);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERED);
        scene.idle(12);
        scene.world().toggleRedstonePower(output);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERING);
        scene.world().cycleBlockProperty(wire, RedStoneWireBlock.POWER);
        scene.world().modifyBlockEntityNBT(nixie, NixieTubeBlockEntity.class, nbt -> nbt.putInt("RedstoneStrength", 2));
        scene.idle(20);
        scene.world().toggleRedstonePower(output);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERING);
        scene.world().cycleBlockProperty(wire, RedStoneWireBlock.POWER);
        scene.world().modifyBlockEntityNBT(nixie, NixieTubeBlockEntity.class, nbt -> nbt.putInt("RedstoneStrength", 3));
        scene.idle(10);

        scene.world().toggleRedstonePower(reset);
        scene.idle(2);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERED_SIDE);
        scene.world().toggleRedstonePower(output);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERING);
        scene.world().modifyBlock(wire, state -> state.setValue(RedStoneWireBlock.POWER, 0), false);
        scene.world().modifyBlockEntityNBT(nixie, NixieTubeBlockEntity.class, nbt -> nbt.putInt("RedstoneStrength", 0));

        scene.idle(18);
        scene.world().toggleRedstonePower(reset);
        scene.idle(2);
        scene.world().cycleBlockProperty(pulseGenerator, SequencedPulseGeneratorBlock.POWERED_SIDE);
        scene.idle(20);

        scene.overlay().showText(70)
                .text("...it will reset and terminate the sequence immediately")
                .pointAt(util.vector().centerOf(pulseGenerator))
                .placeNearTarget();
        scene.idle(80);
        scene.markAsFinished();
    }
}
