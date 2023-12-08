package com.hlysine.create_connected.content.fancatalyst;

import com.hlysine.create_connected.CCTags;
import com.hlysine.create_connected.CCTags.AllItemTags;
import com.hlysine.create_connected.content.ConditionalFanProcessing;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;

public class FanCatalystBlock extends Block implements ConditionalFanProcessing {

    public static final EnumProperty<CatalystContent> CONTENT = EnumProperty.create("content", CatalystContent.class);

    public FanCatalystBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(CONTENT, CatalystContent.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONTENT);
        super.createBlockStateDefinition(builder);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(
            @NotNull BlockState state,
            @NotNull Level world,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull InteractionHand hand,
            @NotNull BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        boolean doNotConsume = player.isCreative();

        InteractionResultHolder<ItemStack> res =
                tryInsert(state, world, pos, heldItem, doNotConsume, false);
        ItemStack leftover = res.getObject();
        if (!world.isClientSide && !doNotConsume && !leftover.isEmpty()) {
            if (heldItem.isEmpty()) {
                player.setItemInHand(hand, leftover);
            } else if (!player.getInventory()
                    .add(leftover)) {
                player.drop(leftover, false);
            }
        }

        return res.getResult() == InteractionResult.SUCCESS ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }


    public static InteractionResultHolder<ItemStack> tryInsert(BlockState state, Level world, BlockPos pos,
                                                               ItemStack stack, boolean doNotConsume, boolean simulate) {
        boolean itemSuccess = false;
        if (AllItemTags.FAN_CATALYST_BLASTING.matches(stack)) {
            world.setBlockAndUpdate(pos, state.setValue(CONTENT, CatalystContent.LAVA));
            itemSuccess = true;
        } else if (AllItemTags.FAN_CATALYST_SMOKING.matches(stack)) {
            world.setBlockAndUpdate(pos, state.setValue(CONTENT, CatalystContent.FIRE));
            itemSuccess = true;
        } else if (AllItemTags.FAN_CATALYST_SPLASHING.matches(stack)) {
            world.setBlockAndUpdate(pos, state.setValue(CONTENT, CatalystContent.WATER));
            itemSuccess = true;
        } else if (AllItemTags.FAN_CATALYST_HAUNTING.matches(stack)) {
            world.setBlockAndUpdate(pos, state.setValue(CONTENT, CatalystContent.SOUL_FIRE));
            itemSuccess = true;
        }

        if (!itemSuccess)
            return InteractionResultHolder.fail(ItemStack.EMPTY);

        if (!doNotConsume) {
            ItemStack container = stack.hasCraftingRemainingItem() ? stack.getCraftingRemainingItem() : ItemStack.EMPTY;
            if (!world.isClientSide) {
                stack.shrink(1);
            }
            return InteractionResultHolder.success(container);
        }
        return InteractionResultHolder.success(ItemStack.EMPTY);
    }

    public static int getLight(BlockState state) {
        return switch (state.getValue(CONTENT)) {
            case NONE, WATER -> 0;
            case LAVA, FIRE -> 10;
            case SOUL_FIRE -> 5;
        };
    }

    @Override
    public boolean canApplyFanType(FanProcessingType type, Level level, BlockPos pos, BlockState state) {
        if (type == AllFanProcessingTypes.BLASTING) {
            return state.getValue(CONTENT) == CatalystContent.LAVA;
        }
        if (type == AllFanProcessingTypes.SMOKING) {
            return state.getValue(CONTENT) == CatalystContent.FIRE;
        }
        if (type == AllFanProcessingTypes.SPLASHING) {
            return state.getValue(CONTENT) == CatalystContent.WATER;
        }
        if (type == AllFanProcessingTypes.HAUNTING) {
            return state.getValue(CONTENT) == CatalystContent.SOUL_FIRE;
        }
        return false;
    }

    public enum CatalystContent implements StringRepresentable {
        NONE, WATER, LAVA, FIRE, SOUL_FIRE;

        @Override
        public @NotNull String getSerializedName() {
            return Lang.asId(name());
        }
    }
}
