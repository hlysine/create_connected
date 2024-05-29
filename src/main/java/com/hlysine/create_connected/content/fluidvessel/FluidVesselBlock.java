package com.hlysine.create_connected.content.fluidvessel;

import com.hlysine.create_connected.CCBlockEntityTypes;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.tank.CreativeFluidTankBlockEntity;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.ComparatorUtil;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.fluid.FluidHelper.FluidExchange;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidVesselBlock extends Block implements IWrenchable, IBE<FluidVesselBlockEntity> {

    public static final BooleanProperty POSITIVE = BooleanProperty.create("positive");
    public static final BooleanProperty NEGATIVE = BooleanProperty.create("negative");
    public static final EnumProperty<Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);

    private final boolean creative;

    public static FluidVesselBlock regular(Properties p_i48440_1_) {
        return new FluidVesselBlock(p_i48440_1_, false);
    }

    public static FluidVesselBlock creative(Properties p_i48440_1_) {
        return new FluidVesselBlock(p_i48440_1_, true);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }

    protected FluidVesselBlock(Properties p_i48440_1_, boolean creative) {
        super(p_i48440_1_);
        this.creative = creative;
        registerDefaultState(defaultBlockState().setValue(POSITIVE, true)
                .setValue(POSITIVE, true)
                .setValue(AXIS, Axis.X)
                .setValue(SHAPE, Shape.WINDOW));
    }

    public static boolean isVessel(BlockState state) {
        return state.getBlock() instanceof FluidVesselBlock;
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean moved) {
        if (oldState.getBlock() == state.getBlock())
            return;
        if (moved)
            return;
        withBlockEntityDo(world, pos, FluidVesselBlockEntity::updateConnectivity);
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(POSITIVE, NEGATIVE, AXIS, SHAPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        if (pContext.getPlayer() == null || !pContext.getPlayer()
                .isShiftKeyDown()) {
            BlockState placedOn = pContext.getLevel()
                    .getBlockState(pContext.getClickedPos()
                            .relative(pContext.getClickedFace()
                                    .getOpposite()));
            Axis preferredAxis = placedOn.getOptionalValue(AXIS).orElse(null);
            if (preferredAxis != null)
                return this.defaultBlockState()
                        .setValue(AXIS, preferredAxis);
        }
        return this.defaultBlockState()
                .setValue(AXIS, pContext.getHorizontalDirection()
                        .getAxis());
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        FluidVesselBlockEntity vesselAt = ConnectivityHandler.partAt(getBlockEntityType(), world, pos);
        if (vesselAt == null)
            return 0;
        FluidVesselBlockEntity controllerBE = vesselAt.getControllerBE();
        if (controllerBE == null || !controllerBE.hasWindow())
            return 0;
        return vesselAt.getLuminosity();
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        withBlockEntityDo(context.getLevel(), context.getClickedPos(), FluidVesselBlockEntity::toggleWindows);
        return InteractionResult.SUCCESS;
    }

    static final VoxelShape CAMPFIRE_SMOKE_CLIP = Block.box(0, 4, 0, 16, 16, 16);

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos,
                                        CollisionContext pContext) {
        if (pContext == CollisionContext.empty())
            return CAMPFIRE_SMOKE_CLIP;
        return pState.getShape(pLevel, pPos);
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return Shapes.block();
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
                                  LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        if (pDirection == Direction.DOWN && pNeighborState.getBlock() != this)
            withBlockEntityDo(pLevel, pCurrentPos, FluidVesselBlockEntity::updateBoilerTemperature);
        return pState;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult ray) {
        ItemStack heldItem = player.getItemInHand(hand);
        boolean onClient = world.isClientSide;

        if (heldItem.isEmpty())
            return InteractionResult.PASS;
        if (!player.isCreative() && !creative)
            return InteractionResult.PASS;

        FluidExchange exchange = null;
        FluidVesselBlockEntity be = ConnectivityHandler.partAt(getBlockEntityType(), world, pos);
        if (be == null)
            return InteractionResult.FAIL;

        LazyOptional<IFluidHandler> vesselCapability = be.getCapability(ForgeCapabilities.FLUID_HANDLER);
        if (!vesselCapability.isPresent())
            return InteractionResult.PASS;
        IFluidHandler fluidVessel = vesselCapability.orElse(null);
        FluidStack prevFluidInTank = fluidVessel.getFluidInTank(0)
                .copy();

        if (FluidHelper.tryEmptyItemIntoBE(world, player, hand, heldItem, be))
            exchange = FluidExchange.ITEM_TO_TANK;
        else if (FluidHelper.tryFillItemFromBE(world, player, hand, heldItem, be))
            exchange = FluidExchange.TANK_TO_ITEM;

        if (exchange == null) {
            if (GenericItemEmptying.canItemBeEmptied(world, heldItem)
                    || GenericItemFilling.canItemBeFilled(world, heldItem))
                return InteractionResult.SUCCESS;
            return InteractionResult.PASS;
        }

        SoundEvent soundevent = null;
        BlockState fluidState = null;
        FluidStack fluidInVessel = vesselCapability.map(fh -> fh.getFluidInTank(0))
                .orElse(FluidStack.EMPTY);

        if (exchange == FluidExchange.ITEM_TO_TANK) {
            if (creative && !onClient) {
                FluidStack fluidInItem = GenericItemEmptying.emptyItem(world, heldItem, true)
                        .getFirst();
                if (!fluidInItem.isEmpty() && fluidVessel instanceof CreativeFluidTankBlockEntity.CreativeSmartFluidTank creativeVessel)
                    creativeVessel.setContainedFluid(fluidInItem);
            }

            Fluid fluid = fluidInVessel.getFluid();
            fluidState = fluid.defaultFluidState()
                    .createLegacyBlock();
            soundevent = FluidHelper.getEmptySound(fluidInVessel);
        }

        if (exchange == FluidExchange.TANK_TO_ITEM) {
            if (creative && !onClient)
                if (fluidVessel instanceof CreativeFluidTankBlockEntity.CreativeSmartFluidTank creativeVessel)
                    creativeVessel.setContainedFluid(FluidStack.EMPTY);

            Fluid fluid = prevFluidInTank.getFluid();
            fluidState = fluid.defaultFluidState()
                    .createLegacyBlock();
            soundevent = FluidHelper.getFillSound(prevFluidInTank);
        }

        if (soundevent != null && !onClient) {
            float pitch = Mth
                    .clamp(1 - (1f * fluidInVessel.getAmount() / (FluidVesselBlockEntity.getCapacityMultiplier() * 16)), 0, 1);
            pitch /= 1.5f;
            pitch += .5f;
            pitch += (world.random.nextFloat() - .5f) / 4f;
            world.playSound(null, pos, soundevent, SoundSource.BLOCKS, .5f, pitch);
        }

        if (!fluidInVessel.isFluidStackIdentical(prevFluidInTank)) {
            if (be instanceof FluidVesselBlockEntity) {
                FluidVesselBlockEntity controllerBE = be.getControllerBE();
                if (controllerBE != null) {
                    if (fluidState != null && onClient) {
                        BlockParticleOption blockParticleData =
                                new BlockParticleOption(ParticleTypes.BLOCK, fluidState);
                        float level = (float) fluidInVessel.getAmount() / fluidVessel.getTankCapacity(0);

                        boolean reversed = fluidInVessel.getFluid()
                                .getFluidType()
                                .isLighterThanAir();
                        if (reversed)
                            level = 1 - level;

                        Vec3 vec = ray.getLocation();
                        vec = new Vec3(vec.x, controllerBE.getBlockPos()
                                .getY() + level * (controllerBE.getHeight() - .5f) + .25f, vec.z);
                        Vec3 motion = player.position()
                                .subtract(vec)
                                .scale(1 / 20f);
                        vec = vec.add(motion);
                        world.addParticle(blockParticleData, vec.x, vec.y, vec.z, motion.x, motion.y, motion.z);
                        return InteractionResult.SUCCESS;
                    }

                    controllerBE.sendDataImmediately();
                    controllerBE.setChanged();
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof FluidVesselBlockEntity))
                return;
            FluidVesselBlockEntity vesselBE = (FluidVesselBlockEntity) be;
            world.removeBlockEntity(pos);
            ConnectivityHandler.splitMulti(vesselBE);
        }
    }

    @Override
    public Class<FluidVesselBlockEntity> getBlockEntityClass() {
        return FluidVesselBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FluidVesselBlockEntity> getBlockEntityType() {
        return creative ? CCBlockEntityTypes.CREATIVE_FLUID_VESSEL.get() : CCBlockEntityTypes.FLUID_VESSEL.get();
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE)
            return state;
        Axis mirrorAxis = mirror == Mirror.FRONT_BACK ? Axis.X : Axis.Z;
        Axis axis = state.getValue(AXIS);
        if (axis == mirrorAxis) {
            return state.setValue(POSITIVE, state.getValue(NEGATIVE))
                    .setValue(NEGATIVE, state.getValue(POSITIVE));
        } else {
            return state;
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        for (int i = 0; i < rotation.ordinal(); i++)
            state = rotateOnce(state);
        return state;
    }

    private BlockState rotateOnce(BlockState state) {
        Axis axis = state.getValue(AXIS);
        if (axis == Axis.X) {
            return state.setValue(AXIS, Axis.Z);
        } else if (axis == Axis.Z) {
            return state.setValue(AXIS, Axis.X)
                    .setValue(POSITIVE, state.getValue(NEGATIVE))
                    .setValue(NEGATIVE, state.getValue(POSITIVE));
        }
        return state;
    }

    public enum Shape implements StringRepresentable {
        PLAIN, WINDOW, WINDOW_TOP, WINDOW_MIDDLE, WINDOW_BOTTOM, WINDOW_SINGLE, WINDOW_TOP_SINGLE, WINDOW_MIDDLE_SINGLE, WINDOW_BOTTOM_SINGLE;

        @Override
        public String getSerializedName() {
            return Lang.asId(name());
        }

        public Shape nonSingleVariant() {
            return switch (this) {
                case WINDOW_SINGLE -> WINDOW;
                case WINDOW_TOP_SINGLE -> WINDOW_TOP;
                case WINDOW_MIDDLE_SINGLE -> WINDOW_MIDDLE;
                case WINDOW_BOTTOM_SINGLE -> WINDOW_BOTTOM;
                default -> this;
            };
        }
    }

    public enum WindowType implements StringRepresentable {
        SIDE_WIDE, SIDE_NARROW_ENDS, SIDE_NARROW_THIRDS, SIDE_HORIZONTAL;

        @Override
        public String getSerializedName() {
            return Lang.asId(name());
        }
    }

    // Vessels are less noisy when placed in batch
    public static final SoundType SILENCED_METAL =
            new ForgeSoundType(0.1F, 1.5F, () -> SoundEvents.METAL_BREAK, () -> SoundEvents.METAL_STEP,
                    () -> SoundEvents.METAL_PLACE, () -> SoundEvents.METAL_HIT, () -> SoundEvents.METAL_FALL);

    @Override
    public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, Entity entity) {
        SoundType soundType = super.getSoundType(state, world, pos, entity);
        if (entity != null && entity.getPersistentData().contains("SilenceVesselSound"))
            return SILENCED_METAL;
        return soundType;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        return getBlockEntityOptional(worldIn, pos).map(FluidVesselBlockEntity::getControllerBE)
                .map(be -> ComparatorUtil.fractionToRedstoneLevel(be.getFillState()))
                .orElse(0);
    }

    public static void updateBoilerState(BlockState pState, Level pLevel, BlockPos vesselPos) {
        BlockState vesselState = pLevel.getBlockState(vesselPos);
        if (!(vesselState.getBlock() instanceof FluidVesselBlock vessel))
            return;
        FluidVesselBlockEntity vesselBE = vessel.getBlockEntity(pLevel, vesselPos);
        if (vesselBE == null)
            return;
        FluidVesselBlockEntity controllerBE = vesselBE.getControllerBE();
        if (controllerBE == null)
            return;
        controllerBE.updateBoilerState();
    }

}
