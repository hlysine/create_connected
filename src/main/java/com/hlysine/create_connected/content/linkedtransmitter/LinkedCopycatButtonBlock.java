package com.hlysine.create_connected.content.linkedtransmitter;

import com.copycatsplus.copycats.content.copycat.button.CopycatButtonBlock;
import com.hlysine.create_connected.compat.CopycatsCompatButtonRegistry;
import com.hlysine.create_connected.registries.CCItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LinkedCopycatButtonBlock extends CopycatButtonBlock implements SpecialBlockItemRequirement, IWrenchable, LinkedTransmitterBlock {
    public static BooleanProperty LOCKED = BlockStateProperties.LOCKED;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public final NonNullSupplier<CopycatButtonBlock> baseSupplier;

    public LinkedCopycatButtonBlock(Properties pProperties, NonNullSupplier<CopycatButtonBlock> baseSupplier) {
        super(baseSupplier.get().type, baseSupplier.get().ticksToStayPressed, pProperties);
        this.baseSupplier = baseSupplier;
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
        return baseSupplier.get();
    }

    @Override
    @NotNull
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (isHittingBase(state, level, pos, hit))
            return super.useItemOn(stack, state, level, pos, player, hand, hit);

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void replaceBase(BlockState baseState, Level world, BlockPos pos) {
        BlockEntity oldBE = world.getBlockEntity(pos);
        if (oldBE == null) return;
        CompoundTag tag = oldBE.saveWithFullMetadata(world.registryAccess());

        BlockState newState = defaultBlockState()
                .setValue(FACING, baseState.getValue(FACING))
                .setValue(FACE, baseState.getValue(FACE))
                .setValue(POWERED, false);
        world.removeBlockEntity(pos); // prevent it from duping the consumed item
        world.setBlock(pos, newState, Block.UPDATE_NEIGHBORS);

        BlockEntity newBE = world.getBlockEntity(pos);
        if (!(newBE instanceof LinkedCopycatButtonBlockEntity linkedBE)) return;
        linkedBE.loadWithComponents(tag, world.registryAccess());
        linkedBE.notifyUpdate();

        AllSoundEvents.CONTROLLER_PUT.playOnServer(world, pos);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state,
                                        @NotNull BlockGetter level,
                                        @NotNull BlockPos pos,
                                        @NotNull CollisionContext context) {
        return Shapes.or(getTransmitterShape(state), super.getShape(state, level, pos, context));
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
        return getBase().defaultBlockState().getDrops(builder);
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state,
                                                     @NotNull Level level,
                                                     @NotNull BlockPos pos,
                                                     @NotNull Player player,
                                                     @NotNull BlockHitResult hit) {
        if (player.isSpectator())
            return InteractionResult.PASS;

        if (!isHittingBase(state, level, pos, hit))
            return LinkedTransmitterBlock.super.useTransmitter(state, level, pos, player);

        if (!player.isShiftKeyDown())
            return super.useWithoutItem(state, level, pos, player, hit);

        return InteractionResult.CONSUME;
    }


    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (state.is(newState.getBlock())) return;
        if (isMoving) return;
        if (!getBlockEntityOptional(world, pos).map(be -> ((LinkedCopycatButtonBlockEntity) be).containsBase).orElse(false)) return;

        Block.popResource(world, pos, new ItemStack(CCItems.LINKED_TRANSMITTER.get()));
        getBase().defaultBlockState().onRemove(world, pos, newState, isMoving);
    }

    @Override
    public @NotNull InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Player player = context.getPlayer();
        if (!player.isCreative()) {
            player.getInventory().placeItemBackInInventory(new ItemStack(CCItems.LINKED_TRANSMITTER.get()));
        }
        withBlockEntityDo(context.getLevel(), context.getClickedPos(), be -> {
            if (be instanceof LinkedCopycatButtonBlockEntity linkedBE)
                linkedBE.containsBase = false;
        });
        replaceWithBase(state, context.getLevel(), context.getClickedPos());
        return InteractionResult.SUCCESS;
    }

    public void replaceWithBase(BlockState oldState, Level world, BlockPos pos) {
        AllSoundEvents.CONTROLLER_TAKE.playOnServer(world, pos);

        BlockEntity oldBE = world.getBlockEntity(pos);
        if (!(oldBE instanceof LinkedCopycatButtonBlockEntity linkedBE)) return;
        linkedBE.transmit(0);

        CompoundTag tag = oldBE.saveWithFullMetadata(world.registryAccess());

        BlockState newState = getBase().defaultBlockState()
                .setValue(FACING, oldState.getValue(FACING))
                .setValue(FACE, oldState.getValue(FACE))
                .setValue(POWERED, oldState.getValue(POWERED));
        world.setBlock(pos, newState, Block.UPDATE_NEIGHBORS);

        BlockEntity newBE = world.getBlockEntity(pos);
        if (!(newBE instanceof SmartBlockEntity baseBE)) return;
        baseBE.loadWithComponents(tag, world.registryAccess());
        world.sendBlockUpdated(pos, oldState, newState, Block.UPDATE_ALL);
        super.checkPressed(world.getBlockState(pos), world, pos);
    }

    @Override
    protected void checkPressed(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        super.checkPressed(state, level, pos);
        updateTransmittedSignal(level, pos);
    }

    @Override
    public void press(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @Nullable Player player) {
        super.press(state, level, pos, player);
        updateTransmittedSignal(level, pos);
    }

    public void updateTransmittedSignal(Level worldIn, BlockPos pos) {
        if (worldIn.isClientSide)
            return;

        BlockState state = worldIn.getBlockState(pos);
        int power = state.getSignal(worldIn, pos, state.getValue(FACING));

        withBlockEntityDo(worldIn, pos,
                be ->
                        ((LinkedCopycatButtonBlockEntity) be).transmit(power));
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockState state,
                                                @NotNull HitResult target,
                                                @NotNull LevelReader world,
                                                @NotNull BlockPos pos,
                                                @NotNull Player player) {
        if (isHittingBase(state, world, pos, target))
            return getBase().getCloneItemStack(state, target, world, pos, player);
        return new ItemStack(CCItems.LINKED_TRANSMITTER.get());
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity be) {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();
        requiredItems.add(new ItemStack(getBase()));
        requiredItems.add(new ItemStack(CCItems.LINKED_TRANSMITTER.get()));
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, requiredItems);
    }

    @Override
    public @NotNull BlockEntityType<? extends LinkedCopycatButtonBlockEntity> getBlockEntityType() {
        return CopycatsCompatButtonRegistry.LINKED_TRANSMITTER.get();
    }
}
