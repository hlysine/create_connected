package com.hlysine.create_connected.ponder;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.CCItems;
import com.hlysine.create_connected.content.kineticbattery.KineticBatteryBlock;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.chainDrive.ChainDriveBlock;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.saw.SawBlock;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.phys.Vec3;

public class KineticBatteryScene {
    public static void kineticBattery(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("kinetic_battery", "Storing kinetic stress with Kinetic Battery");
        scene.configureBasePlate(0, 0, 5);

        BlockPos battery = util.grid().at(3, 1, 2);
        BlockPos lever = util.grid().at(4, 2, 2);
        Selection leverGroup = util.select().fromTo(4, 1, 2, 4, 2, 2);
        Selection rotation0 = util.select().position(2, 0, 5);
        Selection rotation1 = util.select().fromTo(1, 1, 2, 1, 1, 5);
        BlockPos shaft = util.grid().at(2, 1, 2);
        BlockPos saw = util.grid().at(1, 1, 2);
        BlockPos comparator = util.grid().at(3, 1, 1);
        Selection comparatorGroup = util.select().fromTo(3, 1, 0, 3, 1, 1);

        scene.world().setKineticSpeed(util.select().position(battery), 0);

        scene.world().showSection(util.select().fromTo(0, 0, 0, 4, 0, 4), Direction.UP);
        scene.idle(10);
        scene.world().showSection(util.select().position(battery), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(70)
                .text("The Kinetic Battery stores stress units for future use")
                .placeNearTarget()
                .pointAt(util.vector().topOf(battery));
        scene.idle(75);

        scene.world().showSection(rotation0, Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(rotation1, Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(shaft), Direction.DOWN);
        scene.world().setKineticSpeed(util.select().position(battery), -64);
        scene.idle(20);

        scene.overlay().showText(80)
                .text("When unpowered, it consumes kinetic stress from the front to charge up")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(battery));
        scene.idle(35);
        scene.world().modifyBlock(battery, state -> state.setValue(KineticBatteryBlock.LEVEL, 4), false);
        scene.effects().indicateSuccess(battery);
        scene.idle(65);

        scene.world().hideSection(rotation1, Direction.UP);
        scene.world().setKineticSpeed(util.select().position(battery).add(util.select().position(shaft)), 0);
        scene.idle(15);
        scene.world().setBlock(saw, AllBlocks.MECHANICAL_SAW.getDefaultState().setValue(SawBlock.FACING, Direction.UP), false);
        scene.idle(10);
        scene.world().showSection(util.select().position(saw), Direction.DOWN);
        scene.idle(10);

        scene.world().showSection(leverGroup, Direction.DOWN);
        scene.idle(15);

        scene.world().toggleRedstonePower(util.select().position(lever));
        scene.effects().indicateRedstone(lever);
        scene.world().modifyBlock(battery, state -> state.setValue(KineticBatteryBlock.POWERED, true), false);
        scene.world().setKineticSpeed(util.select().position(battery).add(util.select().position(shaft)).add(util.select().position(saw)), -64);

        scene.idle(20);

        scene.overlay().showText(100)
                .text("When powered by redstone, it discharges by generating a fixed amount of stress units at a fixed speed")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(saw.below()));

        scene.idle(60);
        scene.world().modifyBlock(battery, state -> state.setValue(KineticBatteryBlock.LEVEL, 3), false);
        scene.effects().indicateSuccess(battery);
        scene.idle(75);

        scene.world().showSection(comparatorGroup, Direction.DOWN);
        scene.idle(20);

        scene.overlay().showText(80)
                .text("A comparator can be used to read the current battery level")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(comparator.below()));

        scene.idle(110);

        scene.markAsFinished();
    }

    public static void kineticBatteryChaining(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("kinetic_battery_chaining", "Chaining multiple Kinetic Batteries");
        scene.configureBasePlate(0, 0, 5);

        BlockPos battery = util.grid().at(3, 1, 2);
        BlockPos battery2 = util.grid().at(2, 1, 2);
        BlockPos battery3 = util.grid().at(1, 1, 2);
        Selection rotation0 = util.select().position(5, 0, 3);
        Selection rotation1 = util.select().fromTo(5, 1, 2, 4, 1, 2);
        BlockPos tempLever = util.grid().at(3, 1, 1);
        Selection casings = util.select().fromTo(0, 1, 3, 3, 1, 3);
        Selection redstone = util.select().fromTo(1, 2, 3, 3, 2, 3);
        BlockPos lever = util.grid().at(0, 2, 3);
        BlockPos shaft = util.grid().at(4, 1, 2);
        Selection chainDrives = util.select().fromTo(4, 1, 0, 4, 1, 2);
        Selection parallelBatteries = util.select().fromTo(3, 1, 0, 3, 1, 2);
        Selection casings2 = util.select().fromTo(2, 1, 0, 2, 1, 2);
        Selection redstone2 = util.select().fromTo(2, 2, 0, 2, 2, 2);
        BlockPos lever2 = util.grid().at(1, 1, 1);

        scene.world().showSection(util.select().fromTo(0, 0, 0, 4, 0, 4), Direction.UP);
        scene.idle(10);
        scene.world().setKineticSpeed(util.select().position(battery), 0);
        scene.world().showSection(util.select().position(battery), Direction.DOWN);
        scene.idle(10);


        scene.overlay().showText(70)
                .text("The Kinetic Battery has a rear output...")
                .placeNearTarget()
                .pointAt(util.vector().blockSurface(battery, Direction.WEST));
        scene.idle(80);

        scene.world().showSection(rotation0, Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(rotation1, Direction.DOWN);
        scene.world().setKineticSpeed(util.select().position(battery), 64);
        scene.idle(10);

        scene.overlay().showText(100)
                .text("which is coupled to the front input when it finishes charging/discharging...")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector().blockSurface(battery, Direction.WEST));
        scene.idle(120);

        scene.world().setBlock(tempLever, Blocks.LEVER.defaultBlockState().setValue(LeverBlock.FACE, AttachFace.FLOOR), false);
        scene.world().showSection(util.select().position(tempLever), Direction.DOWN);
        scene.idle(15);
        scene.world().toggleRedstonePower(util.select().position(tempLever));
        scene.effects().indicateRedstone(tempLever);
        scene.world().modifyBlock(battery, state -> state.setValue(KineticBatteryBlock.POWERED, true).setValue(KineticBatteryBlock.LEVEL, 4), false);
        scene.world().setKineticSpeed(util.select().position(battery), 0);
        scene.idle(20);

        scene.overlay().showText(80)
                .text("but is disconnected when the battery is operating")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector().blockSurface(battery, Direction.WEST));

        scene.idle(100);
        scene.world().hideSection(util.select().position(tempLever), Direction.UP);
        scene.world().modifyBlock(battery, state -> state.setValue(KineticBatteryBlock.POWERED, false), false);
        scene.idle(10);
        scene.world().showSection(util.select().position(battery2), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().position(battery3), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(casings, Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(redstone, Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(lever), Direction.DOWN);
        scene.idle(20);

        scene.world().toggleRedstonePower(util.select().position(lever).add(redstone));
        scene.effects().indicateRedstone(lever);
        scene.world().modifyBlock(battery, state -> state.setValue(KineticBatteryBlock.POWERED, true).setValue(KineticBatteryBlock.LEVEL, 3), false);
        scene.world().modifyBlock(battery2, state -> state.setValue(KineticBatteryBlock.POWERED, true), false);
        scene.world().modifyBlock(battery3, state -> state.setValue(KineticBatteryBlock.POWERED, true), false);
        scene.world().setKineticSpeed(util.select().position(battery), 64);
        scene.world().setKineticSpeed(util.select().position(battery2).add(util.select().position(battery3)), 0);
        scene.idle(30);
        scene.world().modifyBlock(battery, state -> state.setValue(KineticBatteryBlock.LEVEL, 2), false);
        scene.idle(30);
        scene.overlay().showText(80)
                .text("This allows you to chain batteries to extend the discharge duration")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector().blockSurface(battery3, Direction.WEST));
        scene.world().modifyBlock(battery, state -> state.setValue(KineticBatteryBlock.LEVEL, 1), false);
        scene.idle(30);
        scene.world().modifyBlock(battery, state -> state.setValue(KineticBatteryBlock.LEVEL, 0), false);
        scene.world().modifyBlock(battery2, state -> state.setValue(KineticBatteryBlock.LEVEL, 4), false);
        scene.idle(30);
        scene.world().modifyBlock(battery2, state -> state.setValue(KineticBatteryBlock.LEVEL, 3), false);
        scene.idle(30);
        scene.world().modifyBlock(battery2, state -> state.setValue(KineticBatteryBlock.LEVEL, 2), false);
        scene.idle(30);

        scene.world().hideSection(util.select().position(battery).add(util.select().position(battery2)).add(util.select().position(battery3)), Direction.UP);
        scene.idle(10);
        scene.world().hideSection(casings.add(redstone).add(util.select().position(lever)), Direction.UP);
        scene.world().modifyBlock(battery, state -> state.setValue(KineticBatteryBlock.POWERED, false), false);
        scene.idle(10);
        scene.world().hideSection(util.select().position(shaft), Direction.UP);
        scene.world().setKineticSpeed(util.select().position(battery), 0);
        scene.idle(30);
        scene.world().setBlocks(parallelBatteries, CCBlocks.KINETIC_BATTERY.getDefaultState().setValue(KineticBatteryBlock.FACING, Direction.EAST).setValue(KineticBatteryBlock.LEVEL, 5), false);
        scene.world().showSection(parallelBatteries, Direction.DOWN);
        scene.idle(15);
        scene.world().setBlocks(chainDrives, AllBlocks.ENCASED_CHAIN_DRIVE.getDefaultState().setValue(ChainDriveBlock.AXIS, Direction.Axis.X), false);
        scene.world().showSection(chainDrives, Direction.DOWN);
        scene.idle(15);
        scene.world().setBlocks(casings2, AllBlocks.ANDESITE_CASING.getDefaultState(), false);
        scene.world().showSection(casings2, Direction.DOWN);
        scene.idle(15);
        scene.world().setBlocks(redstone2, Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.NORTH, RedstoneSide.SIDE).setValue(RedStoneWireBlock.SOUTH, RedstoneSide.SIDE), false);
        scene.world().showSection(redstone2, Direction.DOWN);
        scene.idle(15);
        scene.world().setBlock(lever2, Blocks.LEVER.defaultBlockState().setValue(LeverBlock.FACING, Direction.WEST), false);
        scene.world().showSection(util.select().position(lever2), Direction.DOWN);
        scene.idle(30);

        scene.world().toggleRedstonePower(util.select().position(lever2).add(redstone2));
        scene.effects().indicateRedstone(lever2);
        scene.world().modifyBlocks(parallelBatteries.add(util.select().position(battery)), state -> state.setValue(KineticBatteryBlock.POWERED, true).setValue(KineticBatteryBlock.LEVEL, 4), false);
        scene.world().setKineticSpeed(util.select().layer(1), 64);
        scene.idle(30);

        scene.overlay().showText(90)
                .text("To increase the total stress capacity, connect batteries in parallel instead")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector().topOf(battery));

        scene.idle(100);
        scene.markAsFinished();
    }

    public static void kineticBatteryAutomation(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("kinetic_battery_automation", "Automating energy transfer");
        scene.configureBasePlate(0, 0, 5);

        BlockPos battery = util.grid().at(2, 1, 2);
        Selection saw = util.select().fromTo(0, 1, 2, 1, 1, 2);
        BlockPos lever = util.grid().at(2, 1, 1);
        BlockPos deployer = util.grid().at(4, 1, 2);
        Selection deployerGroup = util.select().fromTo(4, 1, 2, 4, 1, 5).add(util.select().position(3, 0, 5));
        BlockPos arm = util.grid().at(2, 1, 4);
        Selection armGroup = util.select().fromTo(2, 1, 4, 2, 1, 5).add(util.select().position(2, 0, 5));
        Selection depot = util.select().position(0, 1, 4);

        scene.world().showSection(util.select().fromTo(0, 0, 0, 4, 0, 4), Direction.UP);
        scene.idle(10);
        scene.world().showSection(util.select().position(battery), Direction.DOWN);
        scene.idle(20);

        scene.overlay().showText(70)
                .text("When the battery is fully charged...")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(battery));
        scene.idle(90);

        scene.overlay().showControls(util.vector().blockSurface(battery, Direction.UP), Pointing.DOWN, 30).withItem(AllItems.WRENCH.asStack());
        scene.idle(30);
        scene.world().destroyBlock(battery);
        var item = scene.world().createItemEntity(util.vector().centerOf(battery), Vec3.ZERO, CCItems.CHARGED_KINETIC_BATTERY.asStack());
        scene.idle(20);

        scene.overlay().showText(70)
                .text("the charge is retained in item form")
                .placeNearTarget()
                .pointAt(util.vector().topOf(battery.below()));
        scene.idle(70);

        scene.world().hideSection(util.select().position(battery), Direction.UP);
        scene.world().modifyEntity(item, e -> e.remove(Entity.RemovalReason.DISCARDED));
        scene.idle(20);
        scene.world().restoreBlocks(util.select().position(battery));
        scene.world().modifyBlock(battery, state -> state.setValue(KineticBatteryBlock.LEVEL, 2), false);
        scene.world().showSection(util.select().position(battery), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(saw, Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(lever), Direction.DOWN);
        scene.idle(20);
        scene.world().toggleRedstonePower(util.select().position(lever));
        scene.effects().indicateRedstone(lever);
        scene.world().setKineticSpeed(util.select().position(battery).add(saw), 64);
        scene.world().modifyBlock(battery, state -> state.setValue(KineticBatteryBlock.POWERED, true).setValue(KineticBatteryBlock.LEVEL, 1), false);
        scene.idle(20);

        scene.overlay().showControls(util.vector().blockSurface(battery, Direction.UP), Pointing.DOWN, 30).withItem(CCItems.CHARGED_KINETIC_BATTERY.asStack());
        scene.idle(30);
        scene.world().modifyBlock(battery, state -> state.setValue(KineticBatteryBlock.LEVEL, 5), false);
        item = scene.world().createItemEntity(util.vector().centerOf(battery.above()), Vec3.ZERO, CCBlocks.KINETIC_BATTERY.asStack());

        scene.overlay().showText(120)
                .text("When a battery is discharging, right click with a charged battery to recharge")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector().topOf(battery));
        scene.idle(140);

        scene.world().hideSection(util.select().position(lever), Direction.UP);
        scene.idle(10);
        scene.world().hideSection(saw, Direction.UP);
        scene.idle(10);
        scene.world().modifyEntity(item, e -> e.remove(Entity.RemovalReason.DISCARDED));
        scene.world().hideSection(util.select().position(battery), Direction.UP);
        scene.idle(20);
        scene.world().setKineticSpeed(util.select().position(battery), 0);
        scene.world().modifyBlock(battery, state -> state.setValue(KineticBatteryBlock.POWERED, false).setValue(KineticBatteryBlock.LEVEL, 5), false);
        scene.world().showSection(util.select().position(battery), Direction.DOWN);
        scene.idle(20);

        scene.overlay().showControls(util.vector().blockSurface(battery, Direction.UP), Pointing.DOWN, 30).withItem(CCBlocks.KINETIC_BATTERY.asStack());
        scene.idle(30);
        scene.world().modifyBlock(battery, state -> state.setValue(KineticBatteryBlock.LEVEL, 0), false);
        item = scene.world().createItemEntity(util.vector().centerOf(battery.above()), Vec3.ZERO, CCItems.CHARGED_KINETIC_BATTERY.asStack());

        scene.overlay().showText(120)
                .text("When a battery is fully charged, right click with an empty battery to discharge")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector().topOf(battery));
        scene.idle(140);
        scene.world().modifyEntity(item, e -> e.remove(Entity.RemovalReason.DISCARDED));

        scene.world().modifyBlockEntityNBT(util.select().position(deployer), DeployerBlockEntity.class, nbt -> nbt.put("HeldItem", CCItems.CHARGED_KINETIC_BATTERY.asStack().saveOptional(scene.world().getHolderLookupProvider())));
        scene.world().showSection(deployerGroup, Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(armGroup, Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(depot, Direction.DOWN);
        scene.idle(20);

        scene.overlay().showText(100)
                .text("Deployers and mechanical arms can automate the energy transfer")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector().topOf(arm.below()));
        scene.idle(120);

        scene.markAsFinished();
    }
}
