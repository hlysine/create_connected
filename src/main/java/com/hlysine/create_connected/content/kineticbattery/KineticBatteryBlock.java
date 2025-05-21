package com.hlysine.create_connected.content.kineticbattery;


import com.hlysine.create_connected.CCBlockEntityTypes;
import com.hlysine.create_connected.CCItems;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.HitResult;
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
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
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
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
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

