package com.hlysine.create_connected.content.parallelgearbox;

import com.hlysine.create_connected.CCBlockEntityTypes;
import com.hlysine.create_connected.CCItems;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ParallelGearboxBlock extends RotatedPillarKineticBlock implements IBE<ParallelGearboxBlockEntity> {

    public ParallelGearboxBlock(Properties properties) {
        super(properties);
    }

    @Override
    public PushReaction getPistonPushReaction(@NotNull BlockState state) {
        return PushReaction.PUSH_ONLY;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, @NotNull LootParams.Builder builder) {
        if (state.getValue(AXIS).isVertical())
            return super.getDrops(state, builder);
        return List.of(new ItemStack(CCItems.VERTICAL_PARALLEL_GEARBOX.get()));
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(BlockState state,
                                                @NotNull HitResult target,
                                                @NotNull LevelReader world,
                                                @NotNull BlockPos pos,
                                                @NotNull Player player) {
        if (state.getValue(AXIS).isVertical())
            return super.getCloneItemStack(state, target, world, pos, player);
        return new ItemStack(CCItems.VERTICAL_PARALLEL_GEARBOX.get());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(AXIS, Axis.Y);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() != state.getValue(AXIS);
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }

    @Override
    public Class<ParallelGearboxBlockEntity> getBlockEntityClass() {
        return ParallelGearboxBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ParallelGearboxBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.PARALLEL_GEARBOX.get();
    }
}
