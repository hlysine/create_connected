package com.hlysine.create_connected.content.brassgearbox;

import com.hlysine.create_connected.CCBlockEntityTypes;
import com.hlysine.create_connected.CCItems;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BrassGearboxBlock extends RotatedPillarKineticBlock implements IBE<BrassGearboxBlockEntity> {

    public static final BooleanProperty FACE_1_FLIPPED = BooleanProperty.create("face_1_flipped");
    public static final BooleanProperty FACE_2_FLIPPED = BooleanProperty.create("face_2_flipped");
    public static final BooleanProperty FACE_3_FLIPPED = BooleanProperty.create("face_3_flipped");
    public static final BooleanProperty FACE_4_FLIPPED = BooleanProperty.create("face_4_flipped");

    public BrassGearboxBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACE_1_FLIPPED, true)
                .setValue(FACE_2_FLIPPED, true)
                .setValue(FACE_3_FLIPPED, false)
                .setValue(FACE_4_FLIPPED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACE_1_FLIPPED);
        builder.add(FACE_2_FLIPPED);
        builder.add(FACE_3_FLIPPED);
        builder.add(FACE_4_FLIPPED);
    }

    @Override
    public void fillItemCategory(@NotNull CreativeModeTab group, @NotNull NonNullList<ItemStack> items) {
        super.fillItemCategory(group, items);
        items.add(CCItems.VERTICAL_BRASS_GEARBOX.asStack());
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.PUSH_ONLY;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootContext.@NotNull Builder builder) {
        if (state.getValue(AXIS).isVertical())
            return super.getDrops(state, builder);
        return List.of(new ItemStack(CCItems.VERTICAL_BRASS_GEARBOX.get()));
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos,
                                       Player player) {
        if (state.getValue(AXIS).isVertical())
            return super.getCloneItemStack(state, target, world, pos, player);
        return new ItemStack(CCItems.VERTICAL_BRASS_GEARBOX.get());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(AXIS, Axis.Y);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(
            BlockState pState,
            @NotNull Level pLevel,
            @NotNull BlockPos pPos,
            @NotNull Player pPlayer,
            @NotNull InteractionHand pHand,
            @NotNull BlockHitResult pHit) {
        if (pState.getBlock() != this) return InteractionResult.PASS;
        if (pPlayer.isHolding(AllItems.WRENCH.get())) return InteractionResult.PASS;
        int face = getFaceId(pHit.getDirection(), pState.getValue(AXIS));
        if (face == 0) return InteractionResult.PASS;
        KineticBlockEntity.switchToBlockState(pLevel, pPos, setFaceFlipped(face, pState, !isFaceFlipped(face, pState)));
        return InteractionResult.SUCCESS;
    }

    @Override
    protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
        if (!super.areStatesKineticallyEquivalent(oldState, newState)) return false;
        if (oldState.getValue(FACE_1_FLIPPED) != newState.getValue(FACE_1_FLIPPED)) return false;
        if (oldState.getValue(FACE_2_FLIPPED) != newState.getValue(FACE_2_FLIPPED)) return false;
        if (oldState.getValue(FACE_3_FLIPPED) != newState.getValue(FACE_3_FLIPPED)) return false;
        if (oldState.getValue(FACE_4_FLIPPED) != newState.getValue(FACE_4_FLIPPED)) return false;
        return true;
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
    public Class<BrassGearboxBlockEntity> getBlockEntityClass() {
        return BrassGearboxBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BrassGearboxBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.BRASS_GEARBOX.get();
    }

    private static final List<Direction> DIRECTIONS = Direction.stream().toList();

    public static int getFaceId(Direction face, Axis blockAxis) {
        List<Direction> directions = new ArrayList<>(DIRECTIONS);
        directions.removeIf(d -> d.getAxis() == blockAxis);
        return directions.indexOf(face) + 1;
    }

    public static boolean isFaceFlipped(int faceId, BlockState state) {
        return switch (faceId) {
            case 1 -> state.getValue(FACE_1_FLIPPED);
            case 2 -> state.getValue(FACE_2_FLIPPED);
            case 3 -> state.getValue(FACE_3_FLIPPED);
            case 4 -> state.getValue(FACE_4_FLIPPED);
            default -> throw new IllegalStateException("Unexpected value: " + faceId);
        };
    }

    public static BlockState setFaceFlipped(int faceId, BlockState state, boolean flipped) {
        return switch (faceId) {
            case 1 -> state.setValue(FACE_1_FLIPPED, flipped);
            case 2 -> state.setValue(FACE_2_FLIPPED, flipped);
            case 3 -> state.setValue(FACE_3_FLIPPED, flipped);
            case 4 -> state.setValue(FACE_4_FLIPPED, flipped);
            default -> throw new IllegalStateException("Unexpected value: " + faceId);
        };
    }
}
