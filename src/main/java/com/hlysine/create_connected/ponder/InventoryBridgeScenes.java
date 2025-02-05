package com.hlysine.create_connected.ponder;

import com.hlysine.create_connected.content.inventorybridge.InventoryBridgeBlock;
import com.hlysine.create_connected.content.inventorybridge.InventoryBridgeBlockEntity;
import com.simibubi.create.foundation.ponder.*;
import com.simibubi.create.foundation.ponder.element.EntityElement;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

public class InventoryBridgeScenes {
    public static void inventoryBridge(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("inventory_bridge", "Connecting two inventories with Inventory Bridge");
        scene.configureBasePlate(0, 0, 5);

        Selection vault1 = util.select.fromTo(3, 1, 0, 4, 2, 1);
        Selection vault2 = util.select.fromTo(3, 1, 3, 4, 2, 4);
        BlockPos mainBridge = util.grid.at(3, 2, 2);
        BlockPos vault1Funnel = util.grid.at(2, 1, 0);
        BlockPos beltStart = util.grid.at(2, 1, 2);
        BlockPos beltEnd = util.grid.at(0, 1, 2);
        BlockPos funnel = util.grid.at(2, 2, 2);
        Selection funnelBelt = util.select.fromTo(funnel, beltEnd);
        Selection gears = util.select.fromTo(2, 1, 3, 2, 1, 5)
                .add(util.select.position(1, 0, 5));
        BlockPos daisyBridge = util.grid.at(3, 3, 2);
        BlockPos daisyVault = util.grid.at(3, 4, 2);
        BlockPos secondBridge = util.grid.at(4, 2, 2);
        BlockPos secondFunnel = util.grid.at(4, 3, 2);

        scene.world.showSection(util.select.fromTo(0, 0, 0, 4, 0, 4), Direction.UP);
        scene.idle(5);
        scene.world.showSection(vault1, Direction.DOWN);
        scene.world.showSection(vault2, Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(gears, Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(funnelBelt, Direction.DOWN);
        scene.idle(10);
        scene.world.showSection(util.select.position(mainBridge), Direction.DOWN);
        scene.idle(10);

        scene.overlay.showText(80)
                .text("Inventory Bridge provides an interface to access two inventories simultaneously")
                .placeNearTarget()
                .pointAt(util.vector.topOf(mainBridge));

        scene.overlay.chaseBoundingBoxOutline(PonderPalette.INPUT, vault1, new AABB(3, 1, 0, 5, 3, 2), 20);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.INPUT, vault2, new AABB(3, 1, 3, 5, 3, 5), 20);
        scene.idle(20);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, mainBridge, Shapes.block().bounds().move(mainBridge), 60);

        for (int i = 0; i < 4; i++) {
            scene.idle(20);
            scene.world.createItemOnBelt(beltStart, Direction.DOWN, new ItemStack(Items.COPPER_BLOCK, 64));
        }
        scene.idle(20);

        scene.overlay.showText(60)
                .text("It does not provide additional storage space")
                .placeNearTarget()
                .pointAt(util.vector.topOf(mainBridge));

        for (int i = 0; i < 4; i++) {
            scene.world.createItemOnBelt(beltStart, Direction.DOWN, new ItemStack(Items.COPPER_BLOCK, 16));
            scene.idle(20);
        }

        scene.addKeyframe();

        scene.world.showSection(util.select.position(vault1Funnel), Direction.EAST);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.INPUT, vault1, new AABB(3, 1, 0, 5, 3, 2), 60);
        ItemStack vault1Stack = new ItemStack(Items.COPPER_BLOCK);
        ElementLink<EntityElement> vault1Item =
                scene.world.createItemEntity(util.vector.centerOf(vault1Funnel), util.vector.of(0, 0, 0), vault1Stack);

        scene.world.createItemOnBelt(beltStart, Direction.DOWN, new ItemStack(Items.COPPER_BLOCK, 16));
        scene.idle(20);


        scene.overlay.showText(80)
                .text("Only components attached to the bridge itself can access both inventories")
                .placeNearTarget()
                .pointAt(util.vector.centerOf(vault1Funnel));

        for (int i = 0; i < 5; i++) {
            scene.world.createItemOnBelt(beltStart, Direction.DOWN, new ItemStack(Items.COPPER_BLOCK, 16));
            scene.idle(20);
        }

        scene.world.modifyEntity(vault1Item, Entity::discard);

        scene.addKeyframe();

        scene.world.hideSection(util.select.position(vault1Funnel), Direction.WEST);
        scene.idle(10);
        scene.world.showSection(util.select.position(secondBridge), Direction.DOWN);
        scene.idle(10);
        scene.world.showSection(util.select.position(secondFunnel), Direction.DOWN);
        scene.idle(20);

        scene.overlay.showText(60)
                .text("Multiple bridges can be attached to the same set of inventories")
                .placeNearTarget()
                .pointAt(util.vector.topOf(secondBridge));

        scene.idle(20);

        for (int i = 0; i < 3; i++) {
            ItemStack stack = new ItemStack(Items.COPPER_BLOCK);
            ElementLink<EntityElement> funnelItem =
                    scene.world.createItemEntity(util.vector.topOf(secondFunnel).add(0, 2, 0), util.vector.of(0, 0, 0), stack);
            scene.idle(10);
            scene.world.modifyEntity(funnelItem, Entity::discard);
            scene.idle(5);
            scene.world.createItemOnBelt(beltStart, Direction.DOWN, new ItemStack(Items.COPPER_BLOCK, 16));
            scene.idle(5);
        }

        scene.addKeyframe();

        scene.world.hideSection(util.select.position(secondFunnel), Direction.UP);
        scene.world.hideSection(util.select.position(secondBridge), Direction.UP);
        scene.idle(10);
        scene.world.showSection(util.select.position(daisyBridge), Direction.DOWN);
        scene.idle(10);
        scene.world.showSection(util.select.position(daisyVault), Direction.DOWN);
        scene.idle(20);

        scene.overlay.chaseBoundingBoxOutline(PonderPalette.RED, daisyBridge, Shapes.block().bounds().move(daisyBridge), 60);

        scene.overlay.showText(60)
                .text("But bridges cannot be daisy-chained")
                .placeNearTarget()
                .pointAt(util.vector.centerOf(daisyBridge));
        scene.idle(70);

        scene.world.hideSection(util.select.position(daisyBridge), Direction.UP);
        scene.world.hideSection(util.select.position(daisyVault), Direction.UP);

        scene.addKeyframe();

        scene.idle(10);
        scene.world.setBlock(secondBridge, Blocks.REDSTONE_BLOCK.defaultBlockState(), false);
        scene.world.showSection(util.select.position(secondBridge), Direction.DOWN);
        scene.idle(10);
        scene.world.modifyBlock(mainBridge, s -> s
                        .setValue(InventoryBridgeBlock.ATTACHED_NEGATIVE, false)
                        .setValue(InventoryBridgeBlock.ATTACHED_POSITIVE, false),
                false);
        scene.effects.indicateRedstone(mainBridge);
        scene.idle(10);

        scene.overlay.showText(60)
                .text("Use a redstone signal to disable the bridge")
                .placeNearTarget()
                .pointAt(util.vector.topOf(mainBridge));
        scene.idle(70);

        scene.world.hideSection(util.select.position(secondBridge), Direction.UP);
        scene.idle(10);
        scene.world.modifyBlock(mainBridge, s -> s
                        .setValue(InventoryBridgeBlock.ATTACHED_NEGATIVE, true)
                        .setValue(InventoryBridgeBlock.ATTACHED_POSITIVE, true),
                false);
        scene.effects.indicateRedstone(mainBridge);

        for (int i = 0; i < 3; i++) {
            scene.idle(20);
            scene.world.createItemOnBelt(beltStart, Direction.DOWN, new ItemStack(Items.COPPER_BLOCK, 16));
        }
    }

    public static void filtering(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("inventory_bridge_filter", "Filtering items with Inventory Bridge");
        scene.configureBasePlate(0, 0, 5);

        BlockPos chute1 = util.grid.at(3, 2, 2);
        BlockPos chute2 = util.grid.at(1, 2, 2);
        BlockPos bridge = util.grid.at(2, 2, 2);
        BlockPos funnelIn = util.grid.at(2, 3, 2);
        BlockPos funnelOut = util.grid.at(2, 2, 1);
        Vec3 leftSlot = util.vector.blockSurface(bridge, Direction.EAST).add(0, 7.5 / 16, 0);
        Vec3 rightSlot = util.vector.blockSurface(bridge, Direction.WEST).add(0, 7.5 / 16, 0);

        scene.world.showSection(util.select.fromTo(0, 0, 0, 4, 0, 4), Direction.UP);
        scene.idle(5);
        scene.world.showSection(util.select.position(bridge), Direction.DOWN);
        scene.idle(10);
        scene.world.showSection(util.select.position(funnelIn), Direction.DOWN);
        scene.idle(10);

        ItemStack iron = new ItemStack(Items.IRON_INGOT);
        ItemStack gold = new ItemStack(Items.GOLD_INGOT);
        scene.overlay.showFilterSlotInput(rightSlot, Direction.WEST, 25);
        scene.overlay.showFilterSlotInput(leftSlot, Direction.EAST, 55);
        scene.idle(20);
        scene.overlay.showControls(new InputWindowElement(rightSlot, Pointing.DOWN).withItem(iron), 30);
        scene.idle(5);
        scene.world.modifyBlockEntity(bridge, InventoryBridgeBlockEntity.class, be -> be.negativeFilter.setFilter(iron));
        scene.idle(25);
        scene.overlay.showControls(new InputWindowElement(leftSlot, Pointing.DOWN).withItem(gold), 30);
        scene.idle(5);
        scene.world.modifyBlockEntity(bridge, InventoryBridgeBlockEntity.class, be -> be.positiveFilter.setFilter(gold));
        scene.idle(25);

        scene.world.showSection(util.select.position(chute1), Direction.DOWN);
        scene.world.showSection(util.select.position(chute2), Direction.DOWN);
        scene.idle(10);

        scene.addKeyframe();

        scene.overlay.showText(100)
                .text("Filter slots on the Inventory Bridge specify which inventory the inserted items should go to")
                .placeNearTarget()
                .pointAt(util.vector.centerOf(bridge));

        ElementLink<EntityElement> itemIn;
        ElementLink<EntityElement> itemOut;

        scene.idle(20);
        itemIn = scene.world.createItemEntity(util.vector.centerOf(funnelIn).add(0, 2, 0), util.vector.of(0, 0, 0), new ItemStack(Items.IRON_INGOT, 16));
        scene.idle(10);
        scene.world.modifyEntity(itemIn, Entity::discard);
        scene.idle(1);
        itemOut = scene.world.createItemEntity(util.vector.centerOf(chute2).add(0, -1, 0), util.vector.of(0, 0, 0), new ItemStack(Items.IRON_INGOT, 16));
        scene.idle(30);
        scene.world.modifyEntity(itemOut, Entity::discard);

        scene.idle(20);
        itemIn = scene.world.createItemEntity(util.vector.centerOf(funnelIn).add(0, 2, 0), util.vector.of(0, 0, 0), new ItemStack(Items.GOLD_INGOT, 16));
        scene.idle(10);
        scene.world.modifyEntity(itemIn, Entity::discard);
        scene.idle(1);
        itemOut = scene.world.createItemEntity(util.vector.centerOf(chute1).add(0, -1, 0), util.vector.of(0, 0, 0), new ItemStack(Items.GOLD_INGOT, 16));
        scene.idle(30);
        scene.world.modifyEntity(itemOut, Entity::discard);

        scene.idle(20);
        scene.addKeyframe();

        scene.overlay.showText(70)
                .text("Items that do not match either filter cannot be inserted into the bridge")
                .placeNearTarget()
                .pointAt(util.vector.centerOf(funnelIn));

        scene.idle(20);
        itemIn = scene.world.createItemEntity(util.vector.centerOf(funnelIn).add(0, 2, 0), util.vector.of(0, 0, 0), new ItemStack(Items.OAK_SAPLING, 16));
        scene.idle(50);
        scene.world.modifyEntity(itemIn, Entity::discard);

        scene.idle(20);
        scene.addKeyframe();

        scene.overlay.showFilterSlotInput(rightSlot, Direction.WEST, 25);
        scene.overlay.showFilterSlotInput(leftSlot, Direction.EAST, 55);
        scene.idle(20);
        scene.overlay.showControls(new InputWindowElement(rightSlot, Pointing.DOWN).withItem(gold), 30);
        scene.idle(5);
        scene.world.modifyBlockEntity(bridge, InventoryBridgeBlockEntity.class, be -> be.negativeFilter.setFilter(gold));
        scene.idle(25);
        scene.overlay.showControls(new InputWindowElement(leftSlot, Pointing.DOWN).rightClick(), 30);
        scene.idle(5);
        scene.world.modifyBlockEntity(bridge, InventoryBridgeBlockEntity.class, be -> be.positiveFilter.setFilter(ItemStack.EMPTY));
        scene.idle(25);


        scene.overlay.showText(70)
                .text("Non-empty filters take precedence over empty ones...")
                .placeNearTarget()
                .pointAt(util.vector.centerOf(chute2));

        scene.idle(20);
        itemIn = scene.world.createItemEntity(util.vector.centerOf(funnelIn).add(0, 2, 0), util.vector.of(0, 0, 0), new ItemStack(Items.GOLD_INGOT, 16));
        scene.idle(10);
        scene.world.modifyEntity(itemIn, Entity::discard);
        scene.idle(1);
        itemOut = scene.world.createItemEntity(util.vector.centerOf(chute2).add(0, -1, 0), util.vector.of(0, 0, 0), new ItemStack(Items.GOLD_INGOT, 16));
        scene.idle(30);
        scene.world.modifyEntity(itemOut, Entity::discard);
        scene.idle(20);

        scene.world.hideSection(util.select.position(chute2), Direction.WEST);

        scene.idle(20);

        scene.overlay.showText(60)
                .text("...and prevent matching items from going to the unfiltered side")
                .placeNearTarget()
                .pointAt(util.vector.centerOf(bridge));

        itemIn = scene.world.createItemEntity(util.vector.centerOf(funnelIn).add(0, 2, 0), util.vector.of(0, 0, 0), new ItemStack(Items.GOLD_INGOT, 16));
        scene.idle(65);
        scene.world.modifyEntity(itemIn, Entity::discard);

        scene.idle(10);
        scene.addKeyframe();

        scene.world.showSection(util.select.position(chute2), Direction.EAST);
        scene.world.hideSection(util.select.position(funnelIn), Direction.UP);
        scene.idle(10);
        scene.world.showSection(util.select.position(funnelOut), Direction.SOUTH);
        scene.idle(10);

        scene.overlay.showText(80)
                .text("Filters are ignored when extracting items from inventories")
                .placeNearTarget()
                .pointAt(util.vector.centerOf(funnelOut));

        scene.idle(20);
        itemIn = scene.world.createItemEntity(util.vector.centerOf(chute1).add(0, 2, 0), util.vector.of(0, 0, 0), new ItemStack(Items.GOLD_INGOT, 16));
        scene.idle(10);
        scene.world.modifyEntity(itemIn, Entity::discard);
        scene.idle(1);
        itemOut = scene.world.createItemEntity(util.vector.centerOf(funnelOut).add(0, -0.5, 0), util.vector.of(0, 0, 0), new ItemStack(Items.GOLD_INGOT, 16));
        scene.idle(30);
        scene.world.modifyEntity(itemOut, Entity::discard);

        scene.idle(20);
        itemIn = scene.world.createItemEntity(util.vector.centerOf(chute2).add(0, 2, 0), util.vector.of(0, 0, 0), new ItemStack(Items.GOLD_INGOT, 16));
        scene.idle(10);
        scene.world.modifyEntity(itemIn, Entity::discard);
        scene.idle(1);
        itemOut = scene.world.createItemEntity(util.vector.centerOf(funnelOut).add(0, -0.5, 0), util.vector.of(0, 0, 0), new ItemStack(Items.GOLD_INGOT, 16));
        scene.idle(30);
        scene.world.modifyEntity(itemOut, Entity::discard);
    }
}
