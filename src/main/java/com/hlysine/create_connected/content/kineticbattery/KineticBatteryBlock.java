package com.hlysine.create_connected.content.kineticbattery;


import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.hlysine.create_connected.registries.CCBlocks;
import com.hlysine.create_connected.registries.CCDataComponents;
import com.hlysine.create_connected.registries.CCItems;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.placement.PoleHelper;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@SuppressWarnings("deprecation")
public class KineticBatteryBlock extends DirectionalKineticBlock implements IBE<KineticBatteryBlockEntity> {

    public static final int placementHelperId = PlacementHelpers.register(new PlacementHelper());

    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 5);
    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    public KineticBatteryBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWER, 0)
                .setValue(LEVEL, 0)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER, LEVEL);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getPlayer() != null && context.getPlayer()
                .isShiftKeyDown())
            return super.getStateForPlacement(context);
        Direction preferredDirection = getPreferredFacing(context);
        return this.defaultBlockState()
                .setValue(FACING, preferredDirection == null ? context.getNearestLookingDirection() : preferredDirection);
    }

    @Override
    public Direction getPreferredFacing(BlockPlaceContext context) {
        for (Direction side : Iterate.directions) {
            BlockState blockState = context.getLevel()
                    .getBlockState(context.getClickedPos().relative(side));
            if (!(blockState.getBlock() instanceof KineticBatteryBlock))
                continue;
            if (blockState.getValue(FACING).getAxis() == side.getAxis())
                return blockState.getValue(FACING);
        }
        return super.getPreferredFacing(context);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (worldIn.isClientSide)
            return;
        if (stack == null)
            return;

        Direction facing = state.getValue(FACING);
        WindmillBearingBlockEntity.RotationDirection preferredRotation = null;
        BlockEntity forwardBE = worldIn.getBlockEntity(pos.relative(facing));
        if (forwardBE instanceof KineticBatteryBlockEntity forwardBattery && forwardBattery.getBlockState().getValue(FACING) == facing) {
            preferredRotation = forwardBattery.getRotationDirection();
        }
        BlockEntity backwardBE = worldIn.getBlockEntity(pos.relative(facing.getOpposite()));
        if (backwardBE instanceof KineticBatteryBlockEntity backwardBattery && backwardBattery.getBlockState().getValue(FACING) == facing) {
            preferredRotation = backwardBattery.getRotationDirection();
        }
        WindmillBearingBlockEntity.RotationDirection finalRotation = preferredRotation;

        withBlockEntityDo(worldIn, pos, be -> {
            //noinspection removal
            if (stack.is(CCItems.CHARGED_KINETIC_BATTERY))
                be.setBatteryLevel(KineticBatteryBlockEntity.getMaxBatteryLevel());
            else
                be.setBatteryLevel(stack.getOrDefault(CCDataComponents.KINETIC_BATTERY_CHARGE, 0.0));
            be.setComponentPatch(stack.getComponentsPatch());
            if (finalRotation != null)
                be.setRotationDirection(finalRotation);
        });
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState pState, LootParams.@NotNull Builder pBuilder) {
        List<ItemStack> lootDrops = super.getDrops(pState, pBuilder);

        BlockEntity blockEntity = pBuilder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (!(blockEntity instanceof KineticBatteryBlockEntity bbe))
            return lootDrops;

        DataComponentPatch components = bbe.getComponentPatch()
                .forget(c -> c.equals(CCDataComponents.KINETIC_BATTERY_CHARGE));
        if (components.isEmpty())
            return lootDrops;

        return lootDrops.stream()
                .peek(stack -> {
                    if (stack.getItem() instanceof KineticBatteryBlockItem)
                        stack.applyComponents(components);
                })
                .toList();
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull LevelReader pLevel, @NotNull BlockPos pos, @NotNull BlockState state) {
        Item item = asItem();

        Optional<KineticBatteryBlockEntity> blockEntityOptional = getBlockEntityOptional(pLevel, pos);

        DataComponentPatch components = blockEntityOptional.map(KineticBatteryBlockEntity::getComponentPatch)
                .orElse(DataComponentPatch.EMPTY);
        double level = blockEntityOptional.map(KineticBatteryBlockEntity::getBatteryLevel)
                .orElse(0.0);

        ItemStack stack = new ItemStack(item.builtInRegistryHolder(), 1, components);
        stack.set(CCDataComponents.KINETIC_BATTERY_CHARGE, level);
        return stack;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack,
                                                       @NotNull BlockState state,
                                                       @NotNull Level level,
                                                       @NotNull BlockPos pos,
                                                       @NotNull Player player,
                                                       @NotNull InteractionHand hand,
                                                       @NotNull BlockHitResult hitResult) {
        InteractionResultHolder<ItemStack> res =
                tryInsert(state, level, pos, stack, false, false);
        ItemStack leftover = res.getObject();
        if (!level.isClientSide && !leftover.isEmpty()) {
            if (stack.isEmpty()) {
                player.setItemInHand(hand, leftover);
            } else if (!player.getInventory().add(leftover)) {
                player.drop(leftover, false);
            }
        }

        if (res.getResult().consumesAction())
            return ItemInteractionResult.SUCCESS;

        IPlacementHelper helper = PlacementHelpers.get(placementHelperId);
        if (helper.matchesItem(stack))
            return helper.getOffset(player, level, state, pos, hitResult)
                    .placeInWorld(level, (BlockItem) stack.getItem(), player, hand, hitResult);

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @SuppressWarnings("removal")
    public static InteractionResultHolder<ItemStack> tryInsert(BlockState state, Level world, BlockPos pos,
                                                               ItemStack stack, boolean doNotConsume, boolean simulate) {
        if (stack.isEmpty())
            return InteractionResultHolder.fail(ItemStack.EMPTY);
        if (!state.hasBlockEntity())
            return InteractionResultHolder.fail(ItemStack.EMPTY);

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof KineticBatteryBlockEntity batteryBE))
            return InteractionResultHolder.fail(ItemStack.EMPTY);
        if (!stack.is(CCBlocks.KINETIC_BATTERY.asItem()) && !stack.is(CCItems.CHARGED_KINETIC_BATTERY.asItem()))
            return InteractionResultHolder.fail(ItemStack.EMPTY);

        double level = stack.getOrDefault(CCDataComponents.KINETIC_BATTERY_CHARGE, 0.0);
        if (stack.is(CCItems.CHARGED_KINETIC_BATTERY.asItem()))
            level = KineticBatteryBlockEntity.getMaxBatteryLevel();

        ItemStack returnedItem;
        if (isDischarging(state)) {
            double transfer = Math.min(KineticBatteryBlockEntity.getMaxBatteryLevel() - batteryBE.getBatteryLevel(), level);
            if (transfer <= 0)
                return InteractionResultHolder.fail(ItemStack.EMPTY);

            if (!simulate)
                batteryBE.setBatteryLevel(batteryBE.getBatteryLevel() + transfer);
            returnedItem = CCBlocks.KINETIC_BATTERY.asStack();
            returnedItem.set(CCDataComponents.KINETIC_BATTERY_CHARGE, level - transfer);
        } else {
            double transfer = Math.min(batteryBE.getBatteryLevel(), KineticBatteryBlockEntity.getMaxBatteryLevel() - level);
            if (transfer <= 0)
                return InteractionResultHolder.fail(ItemStack.EMPTY);

            if (!simulate)
                batteryBE.setBatteryLevel(batteryBE.getBatteryLevel() - transfer);
            returnedItem = CCBlocks.KINETIC_BATTERY.asStack();
            returnedItem.set(CCDataComponents.KINETIC_BATTERY_CHARGE, level + transfer);
        }

        if (!doNotConsume) {
            if (!world.isClientSide) {
                stack.shrink(1);
            }
            if (simulate) {
                // a hack to force mechanical arm to interact, since it normally cancels the interaction
                // if the returned item is the same as the input item
                return InteractionResultHolder.success(ItemStack.EMPTY);
            } else {
                return InteractionResultHolder.success(returnedItem);
            }
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
    protected void neighborChanged(@NotNull BlockState state,
                                   @NotNull Level level,
                                   @NotNull BlockPos pos,
                                   @NotNull Block block,
                                   @NotNull BlockPos fromPos,
                                   boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide) {
            updatePower(state, level, pos);
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide && !state.is(oldState.getBlock())) {
            updatePower(state, level, pos);
        }
    }

    private void updatePower(BlockState state, Level level, BlockPos pos) {
        int currentPower = state.getValue(POWER);
        int expectedPower = calculatePower(state, level, pos);

        if (currentPower != expectedPower) {
            KineticBlockEntity.switchToBlockState(level, pos, state.setValue(POWER, expectedPower));
        }
    }

    private int calculatePower(BlockState state, Level level, BlockPos pos) {
        if (level.hasNeighborSignal(pos)) {
            return 15;
        }

        int maxNeighborPower = 0;
        Direction facing = state.getValue(FACING);

        BlockPos posForward = pos.relative(facing);
        BlockPos posBackward = pos.relative(facing.getOpposite());

        maxNeighborPower = Math.max(maxNeighborPower, getConnectedBatteryPower(state, level, posForward));
        maxNeighborPower = Math.max(maxNeighborPower, getConnectedBatteryPower(state, level, posBackward));

        return Math.max(0, maxNeighborPower - 1);
    }

    private int getConnectedBatteryPower(BlockState myState, Level level, BlockPos targetPos) {
        BlockState targetState = level.getBlockState(targetPos);

        if (targetState.is(this) && targetState.getValue(FACING) == myState.getValue(FACING)) {
            return targetState.getValue(POWER);
        }
        return 0;
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos) {
        return getBlockEntityOptional(world, pos).map(be -> be.getCrudeBatteryLevel(be.getBatteryLevel(), 15)).orElse(0);
    }

    public static boolean isDischarging(BlockState state) {
        return state.getValue(POWER) > 0;
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

    @MethodsReturnNonnullByDefault
    private static class PlacementHelper extends PoleHelper<Direction> {
        private PlacementHelper() {
            super(state -> state.getBlock() instanceof KineticBatteryBlock && state.getValue(LEVEL) == 0, state -> state.getValue(FACING).getAxis(), FACING);
        }

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return i -> i.getItem() instanceof BlockItem blockItem
                    && blockItem.getBlock() instanceof KineticBatteryBlock
                    && i.getOrDefault(CCDataComponents.KINETIC_BATTERY_CHARGE, 0.0) <= 0
                    || i.is(CCItems.CHARGED_KINETIC_BATTERY.asItem());
        }

        @Override
        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            PlacementOffset offset = super.getOffset(player, world, state, pos, ray);
            if (offset.isSuccessful())
                offset.withTransform(offset.getTransform());
            return offset;
        }

    }
}

