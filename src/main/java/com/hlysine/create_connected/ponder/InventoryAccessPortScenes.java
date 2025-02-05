package com.hlysine.create_connected.ponder;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.content.inventoryaccessport.InventoryAccessPortBlock;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchBlock;
import com.simibubi.create.foundation.ponder.ElementLink;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.Selection;
import com.simibubi.create.foundation.ponder.element.EntityElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class InventoryAccessPortScenes {
    public static void inventoryAccessPort(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("inventory_access_port", "Connecting to inventories with Inventory Access Port");
        scene.configureBasePlate(0, 0, 5);

        Selection vault = util.select.fromTo(4, 1, 1, 4, 3, 3);
        BlockPos chute = util.grid.at(3, 2, 2);
        BlockPos beltStart = util.grid.at(3, 1, 2);
        BlockPos beltEnd = util.grid.at(0, 1, 2);
        Selection chuteBelt = util.select.fromTo(beltEnd, chute);
        Selection gears = util.select.fromTo(3, 1, 3, 3, 1, 5).add(util.select.position(2, 0, 5));
        BlockPos chutePort = util.grid.at(3, 3, 2);
        BlockPos funnelPort = util.grid.at(4, 1, 0);
        BlockPos funnel = util.grid.at(4, 2, 0);
        BlockPos comparatorPort = util.grid.at(4, 4, 2);
        BlockPos comparator = util.grid.at(4, 4, 1);
        BlockPos daisyPort = util.grid.at(3, 1, 1);
        BlockPos daisyThresholdSwitch = util.grid.at(2, 1, 1);
        Selection daisy = util.select.fromTo(2, 1, 1, 1, 1, 1);
        BlockPos daisyComparator = util.grid.at(0, 1, 1);
        BlockPos lever = util.grid.at(2, 3, 2);

        scene.world.showSection(util.select.fromTo(0, 0, 0, 4, 0, 4), Direction.UP);
        scene.idle(5);
        scene.world.showSection(vault, Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(gears, Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(chuteBelt, Direction.DOWN);
        scene.idle(5);

        scene.overlay.showText(60)
                .text("Accessing inventory can be challenging in tight spaces")
                .placeNearTarget()
                .pointAt(util.vector.topOf(chute));
        scene.idle(65);

        scene.world.showSection(util.select.position(chutePort), Direction.DOWN);
        scene.idle(10);

        scene.overlay.showText(60)
                .text("Inventory Access Ports extend inventories to help with that")
                .placeNearTarget()
                .pointAt(util.vector.topOf(chutePort));
        scene.idle(5);

        for (int i = 0; i < 6; i++) {
            scene.world.createItemOnBelt(beltStart, Direction.DOWN, new ItemStack(Items.COPPER_BLOCK, 16));
            scene.idle(10);
        }
        scene.idle(10);

        scene.world.hideSection(chuteBelt, Direction.UP);
        scene.world.hideSection(gears, Direction.UP);
        scene.world.hideSection(util.select.position(chutePort), Direction.UP);

        scene.idle(10);

        scene.addKeyframe();

        scene.idle(10);

        scene.world.showSection(util.select.position(funnelPort), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(comparatorPort), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(daisyPort), Direction.DOWN);
        scene.idle(5);

        scene.overlay.showText(60)
                .text("Multiple ports can be attached to one inventory")
                .placeNearTarget()
                .pointAt(util.vector.topOf(comparatorPort));
        scene.idle(65);

        scene.overlay.showText(60)
                .text("Ports do not provide additional storage space")
                .placeNearTarget()
                .pointAt(util.vector.topOf(comparatorPort));
        scene.idle(70);

        scene.addKeyframe();

        scene.world.showSection(util.select.position(funnel), Direction.DOWN);
        scene.idle(5);
        ItemStack stack = new ItemStack(Items.COPPER_BLOCK);
        ElementLink<EntityElement> funnelItem =
                scene.world.createItemEntity(util.vector.topOf(funnel), util.vector.of(0, 0.1, 0), stack);
        scene.world.showSection(util.select.position(comparator), Direction.DOWN);
        scene.idle(5);
        scene.world.setBlock(
                daisyThresholdSwitch,
                AllBlocks.THRESHOLD_SWITCH.getDefaultState()
                        .setValue(ThresholdSwitchBlock.FACING, Direction.EAST)
                        .setValue(ThresholdSwitchBlock.LEVEL, 4),
                false
        );
        scene.world.showSection(util.select.position(daisyThresholdSwitch), Direction.DOWN);
        scene.idle(5);

        scene.overlay.showText(60)
                .text("But components attached to ports can access inventories...")
                .placeNearTarget()
                .pointAt(util.vector.topOf(comparatorPort));
        scene.idle(70);

        scene.overlay.showText(60)
                .text("...as if they are directly connected")
                .placeNearTarget()
                .pointAt(util.vector.topOf(comparatorPort));
        scene.idle(70);

        scene.world.hideSection(util.select.position(funnelPort), Direction.UP);
        scene.world.hideSection(util.select.position(comparatorPort), Direction.UP);
        scene.world.hideSection(util.select.position(funnel), Direction.UP);
        scene.world.hideSection(util.select.position(comparator), Direction.UP);
        scene.world.hideSection(util.select.position(daisyThresholdSwitch), Direction.UP);
        scene.world.modifyEntity(funnelItem, Entity::discard);

        scene.idle(10);
        scene.addKeyframe();
        scene.idle(10);

        scene.world.setBlock(
                daisyThresholdSwitch,
                CCBlocks.INVENTORY_ACCESS_PORT.getDefaultState()
                        .setValue(InventoryAccessPortBlock.FACING, Direction.EAST)
                        .setValue(InventoryAccessPortBlock.ATTACHED, false),
                false
        );
        scene.world.showSection(daisy, Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(daisyComparator), Direction.DOWN);
        scene.idle(10);

        scene.overlay.showText(60)
                .text("Ports cannot be daisy-chained")
                .placeNearTarget()
                .pointAt(util.vector.topOf(daisyThresholdSwitch));
        scene.idle(70);

        scene.world.hideSection(daisy, Direction.UP);
        scene.world.hideSection(util.select.position(daisyComparator), Direction.UP);
        scene.world.hideSection(util.select.position(daisyPort), Direction.UP);

        scene.idle(10);
        scene.addKeyframe();
        scene.idle(10);

        scene.world.showSection(gears, Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(chuteBelt, Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(chutePort), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(lever), Direction.DOWN);
        scene.idle(10);

        for (int i = 0; i < 4; i++) {
            scene.world.createItemOnBelt(beltStart, Direction.DOWN, new ItemStack(Items.COPPER_BLOCK, 16));
            scene.idle(10);
        }

        scene.world.toggleRedstonePower(util.select.position(lever));
        scene.world.modifyBlock(chutePort, state -> state.setValue(InventoryAccessPortBlock.ATTACHED, false), false);
        scene.effects.indicateRedstone(lever);

        scene.overlay.showText(60)
                .text("But they can be disabled with redstone")
                .placeNearTarget()
                .pointAt(util.vector.topOf(lever));
        scene.idle(70);

        scene.world.toggleRedstonePower(util.select.position(lever));
        scene.world.modifyBlock(chutePort, state -> state.setValue(InventoryAccessPortBlock.ATTACHED, true), false);
        scene.effects.indicateRedstone(lever);
        scene.idle(10);

        for (int i = 0; i < 4; i++) {
            scene.world.createItemOnBelt(beltStart, Direction.DOWN, new ItemStack(Items.COPPER_BLOCK, 16));
            scene.idle(10);
        }

        scene.world.toggleRedstonePower(util.select.position(lever));
        scene.world.modifyBlock(chutePort, state -> state.setValue(InventoryAccessPortBlock.ATTACHED, false), false);
        scene.effects.indicateRedstone(lever);
    }
}
