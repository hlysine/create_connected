package com.hlysine.create_connected.content.kineticbattery;


import com.hlysine.create_connected.CCBlockEntityTypes;
import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.CCItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.content.logistics.stockTicker.StockTickerInteractionHandler;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;

public class KineticBatteryBlock extends DirectionalKineticBlock implements IBE<KineticBatteryBlockEntity> {

    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 5);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public KineticBatteryBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(LEVEL, 0)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, LEVEL);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getPlayer() != null && context.getPlayer()
                .isShiftKeyDown())
            return super.getStateForPlacement(context);
        Direction preferredDirection = getPreferredFacing(context);
        return this.defaultBlockState()
                .setValue(FACING, preferredDirection == null ? context.getNearestLookingDirection() : preferredDirection)
                .setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()))
                .setValue(LEVEL, 0);
    }


    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack,
                                                       @NotNull BlockState state,
                                                       @NotNull Level level,
                                                       @NotNull BlockPos pos,
                                                       Player player,
                                                       @NotNull InteractionHand hand,
                                                       @NotNull BlockHitResult hitResult) {
        boolean doNotConsume = player.isCreative();
        boolean forceInsert = !(player instanceof FakePlayer);

        InteractionResultHolder<ItemStack> res =
                tryInsert(state, level, pos, stack, doNotConsume, forceInsert, false);
        ItemStack leftover = res.getObject();
        if (!level.isClientSide && !doNotConsume && !leftover.isEmpty()) {
            if (stack.isEmpty()) {
                player.setItemInHand(hand, leftover);
            } else if (!player.getInventory()
                    .add(leftover)) {
                player.drop(leftover, false);
            }
        }

        return res.getResult() == InteractionResult.SUCCESS ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public static InteractionResultHolder<ItemStack> tryInsert(BlockState state, Level world, BlockPos pos,
                                                               ItemStack stack, boolean doNotConsume, boolean forceInsert, boolean simulate) {
        if (stack.isEmpty())
            return InteractionResultHolder.fail(ItemStack.EMPTY);
        if (!state.hasBlockEntity())
            return InteractionResultHolder.fail(ItemStack.EMPTY);

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof KineticBatteryBlockEntity batteryBE))
            return InteractionResultHolder.fail(ItemStack.EMPTY);

        ItemStack returnedItem;
        if (isDischarging(state)) {
            if (!stack.is(CCItems.CHARGED_KINETIC_BATTERY))
                return InteractionResultHolder.fail(ItemStack.EMPTY);
            if (forceInsert && state.getValue(LEVEL) == 5)
                return InteractionResultHolder.fail(ItemStack.EMPTY);
            if (!forceInsert && batteryBE.getBatteryLevel() > KineticBatteryBlockEntity.CHARGE_THRESHOlD)
                return InteractionResultHolder.fail(ItemStack.EMPTY);

            if (!simulate)
                batteryBE.setBatteryLevel(KineticBatteryBlockEntity.getMaxBatteryLevel());
            returnedItem = CCBlocks.KINETIC_BATTERY.asStack();
        } else {
            if (!stack.is(CCBlocks.KINETIC_BATTERY.asItem()))
                return InteractionResultHolder.fail(ItemStack.EMPTY);
            if (state.getValue(LEVEL) < 5)
                return InteractionResultHolder.fail(ItemStack.EMPTY);

            if (!simulate)
                batteryBE.setBatteryLevel(0);
            returnedItem = CCItems.CHARGED_KINETIC_BATTERY.asStack();
        }

        if (!doNotConsume) {
            if (!world.isClientSide) {
                stack.shrink(1);
            }
            return InteractionResultHolder.success(returnedItem);
        }
        return InteractionResultHolder.success(ItemStack.EMPTY);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == getRotationAxis(state);
    }

    @Override
    protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
        if (oldState.getValue(FACING) != newState.getValue(FACING))
            return false;
        if (isDischarging(oldState) != isDischarging(newState))
            return false;
        if (isCurrentStageComplete(oldState) != isCurrentStageComplete(newState))
            return false;
        return true;
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, Level worldIn, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos,
                                boolean isMoving) {
        if (worldIn.isClientSide)
            return;

        boolean previouslyPowered = state.getValue(POWERED);
        if (previouslyPowered != worldIn.hasNeighborSignal(pos)) {
            KineticBlockEntity.switchToBlockState(worldIn, pos, state.cycle(POWERED));
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos) {
        return getBlockEntityOptional(world, pos).map(be -> be.getCrudeBatteryLevel(15)).orElse(0);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(BlockState state, @NotNull HitResult target, @NotNull LevelReader level, @NotNull BlockPos pos, @NotNull Player player) {
        if (state.getValue(LEVEL) == 5) {
            return CCItems.CHARGED_KINETIC_BATTERY.asStack();
        }
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    public static boolean isDischarging(BlockState state) {
        return state.getValue(POWERED);
    }

    public static boolean isCurrentStageComplete(BlockState state) {
        if (isDischarging(state)) {
            return state.getValue(LEVEL) == 0;
        } else {
            return state.getValue(LEVEL) == 5;
        }
    }

    @Override
    public Class<KineticBatteryBlockEntity> getBlockEntityClass() {
        return KineticBatteryBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends KineticBatteryBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.KINETIC_BATTERY.get();
    }
}

