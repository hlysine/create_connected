package com.hlysine.create_connected.content.sequencedpulsegenerator;

import com.hlysine.create_connected.CCBlockEntityTypes;
import com.hlysine.create_connected.datagen.advancements.AdvancementBehaviour;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.redstone.diodes.AbstractDiodeBlock;
import com.simibubi.create.content.redstone.diodes.BrassDiodeBlock;
import com.simibubi.create.content.redstone.diodes.PoweredLatchBlock;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.ticks.TickPriority;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class SequencedPulseGeneratorBlock extends AbstractDiodeBlock implements IBE<SequencedPulseGeneratorBlockEntity> {
    public static final BooleanProperty POWERING = BrassDiodeBlock.POWERING;
    public static final BooleanProperty POWERED_SIDE = PoweredLatchBlock.POWERED_SIDE;

    public static final MapCodec<SequencedPulseGeneratorBlock> CODEC = simpleCodec(SequencedPulseGeneratorBlock::new);

    public SequencedPulseGeneratorBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(POWERED, false)
                .setValue(POWERING, false)
                .setValue(POWERED_SIDE, false)
        );
    }

    @Override
    protected @NotNull MapCodec<? extends DiodeBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, POWERING, POWERED_SIDE, FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public void setPlacedBy(@NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull LivingEntity placer, @NotNull ItemStack stack) {
        AdvancementBehaviour.trackOwner(worldIn, pos, placer);
    }

    @Override
    protected void checkTickOnNeighbor(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState) {
        if (!this.isLocked(pLevel, pPos, pState)) {
            boolean prevPower = pState.getValue(POWERED);
            boolean currPower = shouldTurnOn(pLevel, pPos, pState);
            boolean prevSide = pState.getValue(POWERED_SIDE);
            boolean currSide = getAlternateSignal(pLevel, pPos, pState) > 0;
            if ((prevPower != currPower || prevSide != currSide) && !pLevel.getBlockTicks().willTickThisTick(pPos, this)) {
                TickPriority tickpriority = TickPriority.HIGH;
                if (this.shouldPrioritize(pLevel, pPos, pState)) {
                    tickpriority = TickPriority.EXTREMELY_HIGH;
                } else if (prevPower || prevSide) {
                    tickpriority = TickPriority.VERY_HIGH;
                }
                pLevel.scheduleTick(pPos, this, this.getDelay(pState), tickpriority);
            }
        }

    }

    @Override
    public void updateNeighborsInFront(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState) {
        super.updateNeighborsInFront(pLevel, pPos, pState);
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel worldIn, @NotNull BlockPos pos, @NotNull RandomSource r) {
        if (!this.isLocked(worldIn, pos, state)) {
            boolean prevPower = state.getValue(POWERED);
            boolean currPower = shouldTurnOn(worldIn, pos, state);
            boolean prevSide = state.getValue(POWERED_SIDE);
            boolean currSide = getAlternateSignal(worldIn, pos, state) > 0;
            BlockState oldState = state;

            if (prevPower != currPower)
                state = state.cycle(POWERED);
            if (prevSide != currSide)
                state = state.cycle(POWERED_SIDE);

            if (oldState != state)
                worldIn.setBlock(pos, state, 2);

            if (currSide) {
                withBlockEntityDo(worldIn, pos, SequencedPulseGeneratorBlockEntity::reset);
                return;
            }
            withBlockEntityDo(worldIn, pos, spg -> spg.onRedstoneUpdate(currPower));
        }
    }

    @Override
    protected int getOutputSignal(@NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull BlockState state) {
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (!(be instanceof SequencedPulseGeneratorBlockEntity spg))
            return state.getValue(POWERING) ? 15 : 0;
        return spg.getCurrentSignal();
    }

    @Override
    public int getSignal(BlockState blockState, @NotNull BlockGetter blockAccess, @NotNull BlockPos
            pos, @NotNull Direction side) {
        return blockState.getValue(FACING) == side ? this.getOutputSignal(blockAccess, pos, blockState) : 0;
    }

    @Override
    protected int getDelay(@NotNull BlockState state) {
        return 2;
    }

    @Override
    public boolean canConnectRedstone(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, Direction side) {
        if (side == null)
            return false;
        return side.getAxis().isHorizontal();
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos) {
        return getBlockEntityOptional(world, pos).map(be -> be.currentInstruction + 1).orElse(0);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack,
                                                       @NotNull BlockState state,
                                                       @NotNull Level level,
                                                       @NotNull BlockPos pos,
                                                       @NotNull Player player,
                                                       @NotNull InteractionHand hand,
                                                       @NotNull BlockHitResult hitResult) {
        if (AllItems.WRENCH.isIn(stack))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (stack.getItem() instanceof BlockItem blockItem) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        CatnipServices.PLATFORM.executeOnClientOnly(
                () -> () -> withBlockEntityDo(level, pos, be -> this.displayScreen(be, player)));
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state,
                                                     @NotNull Level worldIn,
                                                     @NotNull BlockPos pos,
                                                     @NotNull Player player,
                                                     @NotNull BlockHitResult hit) {
        CatnipServices.PLATFORM.executeOnClientOnly(
                () -> () -> withBlockEntityDo(worldIn, pos, be -> this.displayScreen(be, player)));
        return InteractionResult.SUCCESS;
    }

    @OnlyIn(value = Dist.CLIENT)
    protected void displayScreen(SequencedPulseGeneratorBlockEntity be, Player player) {
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new SequencedPulseGeneratorScreen(be));
    }

    @Override
    public Class<SequencedPulseGeneratorBlockEntity> getBlockEntityClass() {
        return SequencedPulseGeneratorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SequencedPulseGeneratorBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.SEQUENCED_PULSE_GENERATOR.get();
    }
}
