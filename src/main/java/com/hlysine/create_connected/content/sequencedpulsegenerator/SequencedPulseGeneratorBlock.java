package com.hlysine.create_connected.content.sequencedpulsegenerator;

import com.hlysine.create_connected.CCBlockEntityTypes;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.redstone.diodes.AbstractDiodeBlock;
import com.simibubi.create.content.redstone.diodes.BrassDiodeBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.gui.ScreenOpener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;

public class SequencedPulseGeneratorBlock extends AbstractDiodeBlock implements IBE<SequencedPulseGeneratorBlockEntity> {
    public static final BooleanProperty POWERING = BrassDiodeBlock.POWERING;

    public SequencedPulseGeneratorBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(POWERED, false)
                .setValue(POWERING, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, POWERING, FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel worldIn, @NotNull BlockPos pos, @NotNull RandomSource r) {
        super.tick(state, worldIn, pos, r);
        boolean isPowered = this.shouldTurnOn(worldIn, pos, state);
        withBlockEntityDo(worldIn, pos, spg -> spg.onRedstoneUpdate(isPowered));
    }

    @Override
    protected int getOutputSignal(@NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull BlockState state) {
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (!(be instanceof SequencedPulseGeneratorBlockEntity spg))
            return state.getValue(POWERING) ? 15 : 0;
        return spg.getCurrentSignal();
    }

    @Override
    public int getSignal(BlockState blockState, @NotNull BlockGetter blockAccess, @NotNull BlockPos pos, @NotNull Direction side) {
        return blockState.getValue(FACING) == side ? this.getOutputSignal(blockAccess, pos, blockState) : 0;
    }

    @Override
    protected int getDelay(@NotNull BlockState state) {
        return 2;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        if (side == null)
            return false;
        return side.getAxis() == state.getValue(FACING).getAxis();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state,
                                          @NotNull Level worldIn,
                                          @NotNull BlockPos pos,
                                          Player player,
                                          @NotNull InteractionHand handIn,
                                          @NotNull BlockHitResult hit) {
        ItemStack held = player.getMainHandItem();
        if (AllItems.WRENCH.isIn(held))
            return InteractionResult.PASS;

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
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
