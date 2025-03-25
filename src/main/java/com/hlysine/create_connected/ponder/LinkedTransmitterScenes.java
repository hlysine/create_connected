package com.hlysine.create_connected.ponder;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.CCItems;
import com.hlysine.create_connected.content.linkedtransmitter.LinkedTransmitterBlockEntity;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.foundation.element.InputWindowElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class LinkedTransmitterScenes {
    public static void linkedTransmitter(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("linked_transmitter", "Using Linked Transmitters");
        scene.configureBasePlate(0, 0, 5);

        BlockPos button = util.grid().at(1, 1, 1);
        BlockPos lever = util.grid().at(2, 1, 1);
        BlockPos analogLever = util.grid().at(3, 1, 1);
        BlockPos linkLeft = util.grid().at(3, 2, 2);
        BlockPos linkRight = util.grid().at(1, 2, 2);
        Selection buttonSelect = util.select().position(button);
        Selection analogLeverSelect = util.select().position(analogLever);
        Selection leverSelect = util.select().position(lever);
        Selection linkLeftSelect = util.select().position(linkLeft);
        Selection linkRightSelect = util.select().position(linkRight);
        Vec3 transmitVec = util.vector().blockSurface(lever, Direction.DOWN)
                .add(0, 3 / 16f, 0);
        Vec3 linkLeftVec = util.vector().blockSurface(linkLeft, Direction.SOUTH)
                .add(0, 0, -3 / 16f);
        Vec3 linkRightVec = util.vector().blockSurface(linkRight, Direction.SOUTH)
                .add(0, 0, -3 / 16f);


        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(4, 1, 3, 0, 2, 3), Direction.DOWN);
        scene.idle(10);

        scene.world().showSection(leverSelect, Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().position(linkRight), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().position(linkLeft), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showControls(transmitVec, Pointing.UP, 40)
                        .rightClick()
                        .withItem(new ItemStack(CCItems.LINKED_TRANSMITTER));
        scene.idle(10);
        scene.world().modifyBlock(lever, s -> CCBlocks.LINKED_LEVER.getDefaultState()
                        .setValue(ATTACH_FACE, s.getValue(ATTACH_FACE))
                        .setValue(HORIZONTAL_FACING, s.getValue(HORIZONTAL_FACING))
                        .setValue(POWERED, s.getValue(POWERED))
                , true);
        scene.idle(10);
        scene.overlay().showText(90)
                .text("Right-click a redstone input with a Linked Transmitter to attach it to the input")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(transmitVec);
        scene.idle(110);


        scene.overlay().showText(50)
                .text("Linked Transmitters can transmit input signals wirelessly")
                .placeNearTarget()
                .pointAt(transmitVec);
        scene.idle(60);

        scene.addKeyframe();
        scene.idle(10);
        scene.world().toggleRedstonePower(leverSelect);
        scene.effects().indicateRedstone(lever);
        scene.idle(5);
        scene.world().toggleRedstonePower(util.select().fromTo(3, 2, 3, 1, 2, 2));
        scene.effects().indicateRedstone(linkLeft);
        scene.effects().indicateRedstone(linkRight);

        scene.idle(10);
        scene.overlay().showText(70)
                .colored(PonderPalette.GREEN)
                .text("Receivers emit the redstone power of transmitters within 128 blocks")
                .placeNearTarget()
                .pointAt(linkRightVec);
        scene.idle(80);
        scene.world().toggleRedstonePower(leverSelect);
        scene.idle(5);
        scene.world().toggleRedstonePower(util.select().fromTo(3, 2, 3, 1, 2, 2));
        scene.idle(20);

        Vec3 frontSlot = transmitVec.add(-0.34375, -0.09375, -.15);
        Vec3 backSlot = transmitVec.add(-0.34375, -0.09375, .15);
        Vec3 top2Slot = linkRightVec.add(0, .15, 0);
        Vec3 bottom2Slot = linkRightVec.add(0, -.2, 0);
        Vec3 top3Slot = linkLeftVec.add(0, .15, 0);
        Vec3 bottom3Slot = linkLeftVec.add(0, -.2, 0);

        scene.addKeyframe();
        scene.idle(10);
        scene.overlay().showFilterSlotInput(frontSlot, Direction.UP, 100);
        scene.overlay().showFilterSlotInput(backSlot, Direction.UP, 100);
        scene.idle(10);

        scene.overlay().showText(50)
                .text("Placing items in the two slots can specify a Frequency")
                .placeNearTarget()
                .pointAt(backSlot);
        scene.idle(60);

        ItemStack iron = new ItemStack(Items.IRON_INGOT);
        ItemStack gold = new ItemStack(Items.GOLD_INGOT);
        ItemStack sapling = new ItemStack(Items.OAK_SAPLING);

        scene.overlay().showControls(frontSlot, Pointing.UP, 30).withItem(iron);
        scene.idle(7);
        scene.overlay().showControls(backSlot, Pointing.DOWN, 30).withItem(sapling);
        scene.world().modifyBlockEntityNBT(leverSelect, LinkedTransmitterBlockEntity.class,
                nbt -> nbt.put("FrequencyLast", iron.save(new CompoundTag())));
        scene.idle(7);
        scene.world().modifyBlockEntityNBT(leverSelect, LinkedTransmitterBlockEntity.class,
                nbt -> nbt.put("FrequencyFirst", sapling.save(new CompoundTag())));
        scene.idle(20);

        scene.overlay().showControls(bottom2Slot, Pointing.UP, 30).withItem(iron);
        scene.idle(7);
        scene.overlay().showControls(top2Slot, Pointing.DOWN, 30).withItem(sapling);
        scene.world().modifyBlockEntityNBT(linkRightSelect, RedstoneLinkBlockEntity.class,
                nbt -> nbt.put("FrequencyLast", iron.save(new CompoundTag())));
        scene.idle(7);
        scene.world().modifyBlockEntityNBT(linkRightSelect, RedstoneLinkBlockEntity.class,
                nbt -> nbt.put("FrequencyFirst", sapling.save(new CompoundTag())));
        scene.idle(20);

        scene.overlay().showControls(bottom3Slot, Pointing.UP, 30).withItem(gold);
        scene.idle(7);
        scene.overlay().showControls(top3Slot, Pointing.DOWN, 30).withItem(sapling);
        scene.world().modifyBlockEntityNBT(linkLeftSelect, RedstoneLinkBlockEntity.class,
                nbt -> nbt.put("FrequencyLast", gold.save(new CompoundTag())));
        scene.idle(7);
        scene.world().modifyBlockEntityNBT(linkLeftSelect, RedstoneLinkBlockEntity.class,
                nbt -> nbt.put("FrequencyFirst", sapling.save(new CompoundTag())));
        scene.idle(20);

        scene.world().toggleRedstonePower(leverSelect);
        scene.effects().indicateRedstone(lever);
        scene.idle(2);
        scene.world().toggleRedstonePower(util.select().fromTo(1, 2, 2, 1, 2, 3));
        scene.overlay().showText(90)
                .attachKeyFrame()
                .text("Only the links with matching Frequencies will communicate")
                .placeNearTarget()
                .pointAt(linkRightVec);

        scene.idle(30);
        for (int i = 0; i < 4; i++) {
            if (i % 2 == 1)
                scene.effects().indicateRedstone(lever);
            scene.world().toggleRedstonePower(leverSelect);
            scene.idle(2);
            scene.world().toggleRedstonePower(util.select().fromTo(1, 2, 2, 1, 2, 3));
            scene.idle(20);
        }

        scene.overlay().showText(40)
                .text("To avoid misclicks...")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(transmitVec);
        scene.idle(30);
        scene.overlay().showControls(transmitVec, Pointing.DOWN,40)
                        .rightClick()
                        .whileSneaking();
        scene.idle(20);
        scene.world().modifyBlock(lever, s -> s.cycle(LOCKED)
                , false);
        scene.idle(20);
        scene.overlay().showText(80)
                .text("...right-click the transmitter while sneaking to lock the frequency slots")
                .placeNearTarget()
                .pointAt(transmitVec);

        scene.idle(80);

        scene.world().showSection(buttonSelect, Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(analogLeverSelect, Direction.DOWN);
        scene.idle(20);
        scene.overlay().showControls(util.vector().blockSurface(analogLever, Direction.DOWN).add(0, 4 / 16.0, 0), Pointing.UP, 40)
                        .rightClick()
                        .withItem(new ItemStack(CCItems.LINKED_TRANSMITTER));
        scene.idle(5);
        scene.overlay().showControls(util.vector().blockSurface(button, Direction.DOWN).add(0, 2 / 16.0, 0), Pointing.DOWN,40)
                        .rightClick()
                        .withItem(new ItemStack(CCItems.LINKED_TRANSMITTER));
        scene.idle(20);
        scene.world().modifyBlock(analogLever, s -> CCBlocks.LINKED_ANALOG_LEVER.getDefaultState()
                        .setValue(ATTACH_FACE, s.getValue(ATTACH_FACE))
                        .setValue(HORIZONTAL_FACING, s.getValue(HORIZONTAL_FACING))
                , true);
        scene.idle(5);
        scene.world().modifyBlock(button, s -> CCBlocks.LINKED_BUTTONS.get(BlockSetType.STONE).getDefaultState()
                        .setValue(ATTACH_FACE, s.getValue(ATTACH_FACE))
                        .setValue(HORIZONTAL_FACING, s.getValue(HORIZONTAL_FACING))
                        .setValue(POWERED, s.getValue(POWERED))
                , true);
        scene.idle(20);


        scene.overlay().showText(70)
                .text("Linked Transmitters also work on buttons and analog levers")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().blockSurface(button, Direction.DOWN).add(0, 2 / 16.0, 0));
        scene.idle(80);

        scene.addKeyframe();

        scene.overlay().showControls(transmitVec, Pointing.DOWN, 40)
                        .rightClick()
                        .withItem(new ItemStack(AllItems.WRENCH));
        scene.idle(20);
        scene.world().modifyBlock(lever, s -> Blocks.LEVER.defaultBlockState()
                        .setValue(ATTACH_FACE, s.getValue(ATTACH_FACE))
                        .setValue(HORIZONTAL_FACING, s.getValue(HORIZONTAL_FACING))
                        .setValue(POWERED, s.getValue(POWERED))
                , true);
        scene.idle(2);
        scene.world().toggleRedstonePower(util.select().fromTo(1, 2, 2, 1, 2, 3));
        scene.idle(20);

        scene.overlay().showText(90)
                .text("Right-click the transmitter with a wrench to detach it from the input")
                .placeNearTarget()
                .pointAt(transmitVec);
        scene.idle(100);
        scene.markAsFinished();
    }
}
