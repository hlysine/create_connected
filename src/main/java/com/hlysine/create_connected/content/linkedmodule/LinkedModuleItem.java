package com.hlysine.create_connected.content.linkedmodule;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedList;
import java.util.List;

public class LinkedModuleItem extends Item {
    public static final List<LinkedModuleBlock> MODULE_BLOCKS = new LinkedList<>();

    public static <T extends Block & LinkedModuleBlock, P, S extends BlockBuilder<T, P>> NonNullUnaryOperator<S> register() {
        return b -> {
            b.onRegister(MODULE_BLOCKS::add);
            return b;
        };
    }

    public LinkedModuleItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
        Player player = ctx.getPlayer();
        if (player == null)
            return InteractionResult.PASS;
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState hitState = world.getBlockState(pos);

        if (player.mayBuild()) {
            for (LinkedModuleBlock moduleBlock : MODULE_BLOCKS) {
                if (hitState.is(moduleBlock.getBase())) {
                    if (!world.isClientSide) {
                        if (!player.isCreative()) stack.shrink(1);
                        moduleBlock.replaceBase(hitState, world, pos);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return use(world, player, ctx.getHand()).getResult();
    }
}
