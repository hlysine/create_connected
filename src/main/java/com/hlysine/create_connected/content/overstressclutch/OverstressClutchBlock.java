package com.hlysine.create_connected.content.overstressclutch;

import com.hlysine.create_connected.CCBlockEntityTypes;
import com.hlysine.create_connected.datagen.advancements.AdvancementBehaviour;
import com.hlysine.create_connected.datagen.advancements.CCAdvancements;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.AbstractEncasedShaftBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public class OverstressClutchBlock extends AbstractEncasedShaftBlock implements IWrenchable, IBE<OverstressClutchBlockEntity> {
    public static final EnumProperty<ClutchState> STATE = EnumProperty.create("state", ClutchState.class);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public OverstressClutchBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(STATE, ClutchState.COUPLED)
                .setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATE, POWERED);
        super.createBlockStateDefinition(builder);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(@NotNull BlockState pState,
                                @NotNull Level pLevel,
                                @NotNull BlockPos pPos,
                                @NotNull Block pBlock,
                                @NotNull BlockPos pFromPos,
                                boolean pIsMoving) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
        boolean flag = pState.getValue(POWERED);
        boolean flag1 = pLevel.getBestNeighborSignal(pPos) > 0;
        if (flag != flag1) {
            pLevel.setBlockAndUpdate(pPos, pState.cycle(POWERED));
            withBlockEntityDo(pLevel, pPos, OverstressClutchBlockEntity::onKineticUpdate);
        }
    }

    @Override
    public Class<OverstressClutchBlockEntity> getBlockEntityClass() {
        return OverstressClutchBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends OverstressClutchBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.OVERSTRESS_CLUTCH.get();
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        withBlockEntityDo(context.getLevel(), context.getClickedPos(), OverstressClutchBlockEntity::resetClutch);
        return InteractionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getAnalogOutputSignal(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos) {
        return pState.getValue(STATE) == ClutchState.UNCOUPLED ? 0 : 15;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(@NotNull BlockState pState, ServerLevel pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
        BlockEntity be = pLevel.getBlockEntity(pPos);
        if (!(be instanceof OverstressClutchBlockEntity kte))
            return;

        ClutchState state = pState.getValue(STATE);

        if (pState.getValue(POWERED)) {
            pLevel.setBlockAndUpdate(pPos, pState.setValue(STATE, ClutchState.COUPLED));
            kte.delay = 0;
            return;
        }
        if (state == ClutchState.COUPLED || state == ClutchState.UNCOUPLED) {
            kte.delay = 0;
            return;
        }
        if (kte.delay <= 0) {
            if (!pLevel.isClientSide) {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(STATE, ClutchState.UNCOUPLED));
                RotationPropagator.handleRemoved(pLevel, pPos, kte);
                RotationPropagator.handleAdded(pLevel, pPos, kte);
                AdvancementBehaviour.tryAward(kte, CCAdvancements.OVERSTRESS_CLUTCH);
                return;
            }
        }
        kte.delay--;
    }

    public enum ClutchState implements StringRepresentable {
        COUPLED, UNCOUPLING, UNCOUPLED;

        @Override
        public @NotNull String getSerializedName() {
            return Lang.asId(name());
        }
    }
}
