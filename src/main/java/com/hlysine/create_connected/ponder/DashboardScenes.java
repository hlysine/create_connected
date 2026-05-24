package com.hlysine.create_connected.ponder;

import com.hlysine.create_connected.content.dashboard.DashboardBlock;
import com.hlysine.create_connected.content.dashboard.DashboardBlockEntity;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
import com.simibubi.create.content.equipment.clipboard.ClipboardOverrides;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.ParrotElement;
import net.createmod.ponder.api.element.ParrotPose;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class DashboardScenes {
    public static void dashboard(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("dashboard", "Using Dashboards");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        BlockPos boardPos = util.grid().at(2, 1, 3);
        Selection board = util.select().position(boardPos);
        BlockPos seatPos = util.grid().at(2, 1, 2);
        Selection seat = util.select().position(seatPos);
        BlockPos depotPos = util.grid().at(3, 1, 1);
        Selection depot = util.select().position(depotPos);
        BlockPos linkPos = util.grid().at(2, 1, 1);
        Selection link = util.select().position(linkPos);

        scene.idle(15);

        scene.world().showSection(board, Direction.DOWN);

        scene.idle(10);

        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("A Dashboard is a mini-display with 4 lines")
                .pointAt(util.vector().blockSurface(boardPos, Direction.WEST))
                .placeNearTarget();
        scene.idle(80);

        Vec3 target = util.vector().topOf(boardPos);
        ItemStack clipboard = AllBlocks.CLIPBOARD.asStack();
        clipboard.set(AllDataComponents.CLIPBOARD_CONTENT, ClipboardContent.EMPTY.setType(ClipboardOverrides.ClipboardType.WRITTEN));
        scene.overlay().showControls(target, Pointing.RIGHT, 40).withItem(clipboard)
                .rightClick();
        scene.idle(6);
        scene.world().modifyBlockEntity(boardPos, DashboardBlockEntity.class,
                be -> be.setLine(0, Component.literal("Connected"))
        );
        scene.idle(25);

        scene.overlay().showText(50)
                .text("Static text can be applied using written Clipboards")
                .pointAt(target)
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(80);

        scene.world().showSection(depot, Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(link, Direction.EAST);
        scene.idle(15);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.INPUT, depot, new AABB(linkPos).contract(-.5f, 0, 0), 60);
        scene.idle(5);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.OUTPUT, link, new AABB(boardPos).contract(0, 0, -.25f), 60);
        scene.idle(20);

        scene.overlay().showText(70)
                .text("And dynamic text through the use of Display Links")
                .pointAt(target)
                .attachKeyFrame()
                .colored(PonderPalette.OUTPUT)
                .placeNearTarget();
        scene.idle(50);

        ItemStack item1 = AllItems.PROPELLER.asStack();
        scene.world().createItemOnBeltLike(depotPos, Direction.SOUTH, item1);
        scene.world().modifyBlockEntity(boardPos, DashboardBlockEntity.class,
                be -> be.setLine(0, item1.getHoverName())
        );
        scene.world().flashDisplayLink(linkPos);
        scene.idle(50);

        scene.overlay().showControls(target, Pointing.RIGHT, 40).withItem(new ItemStack(Items.GLOW_INK_SAC))
                .rightClick();
        scene.idle(6);
        scene.world().modifyBlockEntity(boardPos, DashboardBlockEntity.class,
                be -> be.setText(be.getText().setHasGlowingText(true))
        );
        scene.idle(25);

        scene.overlay().showText(70)
                .text("Dyes and Glow Ink Sac can be applied to the dashboard")
                .pointAt(target)
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(80);

        scene.world().showSection(seat, Direction.DOWN);
        scene.idle(10);
        ElementLink<ParrotElement> birb = scene.special().createBirb(Vec3.atCenterOf(seatPos).add(0, 1, 0), ParrotPose.FaceCursorPose::new);
        scene.special().moveParrot(birb, util.vector().of(0, -1.1, 0), 20);
        scene.idle(20);

        scene.overlay().showText(90)
                .text("Information displayed on the dashboard is sent to the HUD of players sitting in front of it")
                .pointAt(util.vector().centerOf(seatPos))
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(100);

        scene.overlay().showControls(target, Pointing.RIGHT, 40).withItem(AllItems.WRENCH.asStack());
        scene.idle(6);
        scene.world().modifyBlock(boardPos, state -> state.setValue(DashboardBlock.OPEN, false), false);
        scene.idle(25);

        scene.overlay().showText(50)
                .text("This can be disabled using a wrench")
                .pointAt(target)
                .placeNearTarget();
        scene.idle(60);

        scene.world().hideSection(link, Direction.WEST);
        scene.idle(5);
        scene.world().hideSection(depot, Direction.UP);
        scene.idle(5);
        scene.overlay().showControls(target, Pointing.RIGHT, 40).rightClick();
        scene.idle(6);
        scene.world().modifyBlockEntity(boardPos, DashboardBlockEntity.class,
                be -> be.setLine(0, CommonComponents.EMPTY)
        );
        scene.idle(25);

        scene.overlay().showText(70)
                .text("The dashboard can be reset by clicking it with an empty hand")
                .pointAt(target)
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(40);

    }
}
