package com.hlysine.create_connected.content.shearpin;

import com.google.common.base.Predicates;
import com.hlysine.create_connected.CCBlockEntityTypes;
import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.content.AbstractBEShaftBlock;
import com.hlysine.create_connected.datagen.advancements.AdvancementBehaviour;
import com.hlysine.create_connected.datagen.advancements.CCAdvancements;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.foundation.placement.IPlacementHelper;
import com.simibubi.create.foundation.placement.PlacementHelpers;
import com.simibubi.create.foundation.placement.PlacementOffset;
import com.simibubi.create.foundation.placement.PoleHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ShearPinBlock extends AbstractBEShaftBlock<ShearPinBlockEntity> {

    public static final int placementHelperId = PlacementHelpers.register(new PlacementHelper());

    public ShearPinBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<ShearPinBlockEntity> getBlockEntityClass() {
        return ShearPinBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ShearPinBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.SHEAR_PIN.get();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(@NotNull BlockState pState, ServerLevel pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
        BlockEntity be = pLevel.getBlockEntity(pPos);
        if (!(be instanceof ShearPinBlockEntity kte))
            return;
        if (!kte.isOverStressed())
            return;

        if (!pLevel.isClientSide) {
            pLevel.destroyBlock(pPos, false);
            Vec3 center = pPos.getCenter();
            pLevel.sendParticles(ParticleTypes.LARGE_SMOKE, center.x, center.y, center.z, 5, 0.1, 0.1, 0.1, 0.05);
            AdvancementBehaviour.tryAward(kte, CCAdvancements.SHEAR_PIN);
        }
    }

    public static boolean isShaft(BlockState state) {
        return CCBlocks.SHEAR_PIN.has(state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return AllShapes.SIX_VOXEL_POLE.get(state.getValue(AXIS));
    }

    @Override
    public float getParticleTargetRadius() {
        return .35f;
    }

    @Override
    public float getParticleInitialRadius() {
        return .125f;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult ray) {
        if (player.isShiftKeyDown() || !player.mayBuild())
            return InteractionResult.PASS;

        ItemStack heldItem = player.getItemInHand(hand);

        IPlacementHelper helper = PlacementHelpers.get(placementHelperId);
        if (helper.matchesItem(heldItem))
            return helper.getOffset(player, world, state, pos, ray)
                    .placeInWorld(world, (BlockItem) heldItem.getItem(), player, hand, ray);

        return InteractionResult.PASS;
    }

    @MethodsReturnNonnullByDefault
    private static class PlacementHelper extends PoleHelper<Direction.Axis> {
        // used for extending a shaft in its axis, like the piston poles. works with
        // shafts and cogs

        private PlacementHelper() {
            super(Predicates.or(Predicates.or(AllBlocks.SHAFT::has, AllBlocks.POWERED_SHAFT::has), CCBlocks.SHEAR_PIN::has),
                    state -> state.getValue(AXIS), AXIS);
        }

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return i -> i.getItem() instanceof BlockItem
                    && (((BlockItem) i.getItem()).getBlock() instanceof AbstractSimpleShaftBlock);
        }

        @Override
        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos,
                                         BlockHitResult ray) {
            PlacementOffset offset = super.getOffset(player, world, state, pos, ray);
            if (offset.isSuccessful())
                offset.withTransform(offset.getTransform()
                        .andThen(s -> ShaftBlock.pickCorrectShaftType(s, world, offset.getBlockPos())));
            return offset;
        }

    }
}
