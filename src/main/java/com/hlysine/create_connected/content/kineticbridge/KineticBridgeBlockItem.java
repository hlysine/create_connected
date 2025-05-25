package com.hlysine.create_connected.content.kineticbridge;

import com.hlysine.create_connected.ConnectedLang;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class KineticBridgeBlockItem extends BlockItem {

    public KineticBridgeBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext ctx) {
        InteractionResult result = super.place(ctx);
        if (result == InteractionResult.FAIL && ctx.getLevel().isClientSide())
            CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> showBounds(ctx));
        return result;
    }

    @OnlyIn(Dist.CLIENT)
    public void showBounds(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Direction facing = ((KineticBridgeBlock) getBlock()).getDirectionForPlacement(context);
        if (!(context.getPlayer() instanceof LocalPlayer localPlayer))
            return;
        Outliner.getInstance().showAABB(Pair.of("kinetic_bridge", pos), new AABB(pos).expandTowards(facing.getNormal().getX(), facing.getNormal().getY(), facing.getNormal().getZ()))
                .colored(0xFF_ff5d6c);
        ConnectedLang.translate("kinetic_bridge.not_enough_space")
                .color(0xFF_ff5d6c)
                .sendStatus(localPlayer);
    }

}

