package com.hlysine.create_connected.content.brake;

import com.hlysine.create_connected.CCBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.AbstractEncasedShaftBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

public class BrakeBlock extends AbstractEncasedShaftBlock implements IBE<BrakeBlockEntity> {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public BrakeBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(POWERED,
                context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
        if (!super.areStatesKineticallyEquivalent(oldState, newState)) return false;
        if (oldState.getValue(POWERED) != newState.getValue(POWERED)) return false;
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(
            @NotNull BlockState state,
            Level worldIn,
            @NotNull BlockPos pos,
            @NotNull Block blockIn,
            @NotNull BlockPos fromPos,
            boolean isMoving) {
        if (worldIn.isClientSide)
            return;

        boolean previouslyPowered = state.getValue(POWERED);
        if (previouslyPowered != worldIn.hasNeighborSignal(pos)) {
            KineticBlockEntity.switchToBlockState(worldIn, pos, state.cycle(POWERED));
        }
    }

    @Override
    public Class<BrakeBlockEntity> getBlockEntityClass() {
        return BrakeBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BrakeBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.BRAKE.get();
    }
}
