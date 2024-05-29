package com.hlysine.create_connected.content.linkedtransmitter;

import com.hlysine.create_connected.CCBlockEntityTypes;
import com.hlysine.create_connected.CCItems;
import com.hlysine.create_connected.CCShapes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.schematics.requirement.ISpecialBlockItemRequirement;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LinkedLeverBlock extends LeverBlock implements IBE<LinkedTransmitterBlockEntity>, ISpecialBlockItemRequirement, IWrenchable, LinkedTransmitterBlock {
    public static BooleanProperty LOCKED = BlockStateProperties.LOCKED;

    private final LeverBlock base;

    public LinkedLeverBlock(Properties pProperties, LeverBlock base) {
        super(pProperties);
        this.base = base;
        registerDefaultState(defaultBlockState().setValue(LOCKED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(LOCKED));
    }

    @Override
    public Block getBlock() {
        return this;
    }

    @Override
    public Block getBase() {
        return base;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state,
                                        @NotNull BlockGetter level,
                                        @NotNull BlockPos pos,
                                        @NotNull CollisionContext context) {
        Direction facing = state.getValue(LeverBlock.FACING);
        return Shapes.or(switch (state.getValue(LeverBlock.FACE)) {
            case FLOOR -> CCShapes.FLOOR_LINKED_TRANSMITTER.get(facing);
            case WALL -> CCShapes.WALL_LINKED_TRANSMITTER.get(facing);
            case CEILING -> CCShapes.CEILING_LINKED_TRANSMITTER.get(facing);
        }, super.getShape(state, level, pos, context));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
        return base.getDrops(state, builder);
    }

    private boolean isHittingBase(BlockState state, BlockGetter level, BlockPos pos, HitResult hit) {
        return super.getShape(state, level, pos, CollisionContext.empty()).bounds().inflate(0.01 / 16).move(pos).contains(hit.getLocation());
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state,
                                          @NotNull Level level,
                                          @NotNull BlockPos pos,
                                          @NotNull Player player,
                                          @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult hit) {
        if (player.isSpectator())
            return InteractionResult.PASS;

        if (isHittingBase(state, level, pos, hit)) {
            if (!player.isShiftKeyDown())
                return super.use(state, level, pos, player, hand, hit);
            return InteractionResult.CONSUME;
        }
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide())
                level.setBlockAndUpdate(pos, state.cycle(LOCKED));
            return InteractionResult.SUCCESS;
        }
        if (state.getValue(LOCKED))
            return InteractionResult.CONSUME;
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && !isMoving && getBlockEntityOptional(world, pos).map(be -> be.containsBase).orElse(false))
            Block.popResource(world, pos, new ItemStack(CCItems.LINKED_TRANSMITTER));
        base.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        onWrenched(state, context);
        return IWrenchable.super.onSneakWrenched(state, context);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Player player = context.getPlayer();
        if (!player.isCreative()) {
            player.getInventory().placeItemBackInInventory(new ItemStack(CCItems.LINKED_TRANSMITTER));
        }
        withBlockEntityDo(context.getLevel(), context.getClickedPos(), be -> be.containsBase = false);
        replaceWithBase(state, context.getLevel(), context.getClickedPos());
        return InteractionResult.SUCCESS;
    }

    @Override
    public void replaceBase(BlockState baseState, Level world, BlockPos pos) {
        world.setBlockAndUpdate(pos, defaultBlockState()
                .setValue(FACING, baseState.getValue(FACING))
                .setValue(FACE, baseState.getValue(FACE))
                .setValue(POWERED, baseState.getValue(POWERED))
        );
        AllSoundEvents.CONTROLLER_PUT.playOnServer(world, pos);
    }

    public void replaceWithBase(BlockState state, Level world, BlockPos pos) {
        AllSoundEvents.CONTROLLER_TAKE.playOnServer(world, pos);
        world.setBlockAndUpdate(pos, base.defaultBlockState()
                .setValue(FACING, state.getValue(FACING))
                .setValue(FACE, state.getValue(FACE))
                .setValue(POWERED, state.getValue(POWERED)));
    }

    @Override
    public @NotNull BlockState pull(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        BlockState newState = super.pull(state, level, pos);
        updateTransmittedSignal(level, pos);
        return newState;
    }

    public void updateTransmittedSignal(Level worldIn, BlockPos pos) {
        if (worldIn.isClientSide)
            return;

        BlockState state = worldIn.getBlockState(pos);
        int power = state.getSignal(worldIn, pos, state.getValue(FACING));

        withBlockEntityDo(worldIn, pos, be -> be.transmit(power));
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        if (isHittingBase(state, world, pos, target))
            return base.getCloneItemStack(state, target, world, pos, player);
        return new ItemStack(CCItems.LINKED_TRANSMITTER.get());
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity be) {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();
        requiredItems.add(new ItemStack(base));
        requiredItems.add(new ItemStack(CCItems.LINKED_TRANSMITTER.get()));
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, requiredItems);
    }

    @Override
    public Class<LinkedTransmitterBlockEntity> getBlockEntityClass() {
        return LinkedTransmitterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends LinkedTransmitterBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.LINKED_TRANSMITTER.get();
    }
}
