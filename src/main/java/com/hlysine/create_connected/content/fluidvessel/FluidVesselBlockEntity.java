package com.hlysine.create_connected.content.fluidvessel;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.hlysine.create_connected.content.fluidvessel.FluidVesselBlock.*;
import static net.minecraft.core.Direction.*;

public class FluidVesselBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IMultiBlockEntityContainer.Fluid {

    private static final int MAX_SIZE = 3;

    protected LazyOptional<IFluidHandler> fluidCapability;
    protected boolean forceFluidLevelUpdate;
    protected FluidTank vesselInventory;
    protected BlockPos controller;
    protected BlockPos lastKnownPos;
    protected boolean updateConnectivity;
    protected boolean window;
    protected int luminosity;
    protected int width;
    protected int length;

    public BoilerData boiler;

    private static final int SYNC_RATE = 8;
    protected int syncCooldown;
    protected boolean queuedSync;

    // For rendering purposes only
    private LerpedFloat fluidLevel;

    public FluidVesselBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        vesselInventory = createInventory();
        fluidCapability = LazyOptional.of(() -> vesselInventory);
        forceFluidLevelUpdate = true;
        updateConnectivity = false;
        window = true;
        length = 1;
        width = 1;
        boiler = new BoilerData();
        refreshCapability();
    }

    protected SmartFluidTank createInventory() {
        return new SmartFluidTank(getCapacityMultiplier(), this::onFluidStackChanged);
    }

    protected void updateConnectivity() {
        updateConnectivity = false;
        if (level.isClientSide)
            return;
        if (!isController())
            return;
        ConnectivityHandler.formMulti(this);
    }

    @Override
    public void tick() {
        super.tick();
        if (syncCooldown > 0) {
            syncCooldown--;
            if (syncCooldown == 0 && queuedSync)
                sendData();
        }

        if (lastKnownPos == null)
            lastKnownPos = getBlockPos();
        else if (!lastKnownPos.equals(worldPosition) && worldPosition != null) {
            onPositionChanged();
            return;
        }

        if (updateConnectivity)
            updateConnectivity();
        if (fluidLevel != null)
            fluidLevel.tickChaser();
        if (isController())
            boiler.tick(this);
    }

    @Override
    public BlockPos getLastKnownPos() {
        return lastKnownPos;
    }

    public Axis getAxis() {
        return getBlockState().getValue(AXIS);
    }

    @Override
    public boolean isController() {
        return controller == null || worldPosition.getX() == controller.getX()
                && worldPosition.getY() == controller.getY() && worldPosition.getZ() == controller.getZ();
    }

    @Override
    public void initialize() {
        super.initialize();
        sendData();
        if (level.isClientSide)
            invalidateRenderBoundingBox();
    }

    private void onPositionChanged() {
        removeController(true);
        lastKnownPos = worldPosition;
    }

    protected void onFluidStackChanged(FluidStack newFluidStack) {
        if (!hasLevel())
            return;

        FluidType attributes = newFluidStack.getFluid()
                .getFluidType();
        int luminosity = (int) (attributes.getLightLevel(newFluidStack) / 1.2f);
        boolean reversed = attributes.isLighterThanAir();
        int maxY = (int) ((getFillState() * width) + 1);
        Axis axis = getAxis();

        for (int yOffset = 0; yOffset < width; yOffset++) {
            boolean isBright = reversed ? (width - yOffset <= maxY) : (yOffset < maxY);
            int actualLuminosity = isBright ? luminosity : luminosity > 0 ? 1 : 0;

            for (int lengthOffset = 0; lengthOffset < length; lengthOffset++) {
                for (int widthOffset = 0; widthOffset < width; widthOffset++) {
                    BlockPos pos = this.worldPosition.offset(
                            axis == Axis.X ? lengthOffset : widthOffset,
                            yOffset,
                            axis == Axis.Z ? lengthOffset : widthOffset
                    );
                    FluidVesselBlockEntity vesselAt = ConnectivityHandler.partAt(getType(), level, pos);
                    if (vesselAt == null)
                        continue;
                    level.updateNeighbourForOutputSignal(pos, vesselAt.getBlockState()
                            .getBlock());
                    if (vesselAt.luminosity == actualLuminosity)
                        continue;
                    vesselAt.setLuminosity(actualLuminosity);
                }
            }
        }

        if (!level.isClientSide) {
            setChanged();
            sendData();
        }

        if (isVirtual()) {
            if (fluidLevel == null)
                fluidLevel = LerpedFloat.linear()
                        .startWithValue(getFillState());
            fluidLevel.chase(getFillState(), .5f, Chaser.EXP);
        }
    }

    protected void setLuminosity(int luminosity) {
        if (level.isClientSide)
            return;
        if (this.luminosity == luminosity)
            return;
        this.luminosity = luminosity;
        sendData();
    }

    @SuppressWarnings("unchecked")
    @Override
    public FluidVesselBlockEntity getControllerBE() {
        if (isController())
            return this;
        BlockEntity blockEntity = level.getBlockEntity(controller);
        if (blockEntity instanceof FluidVesselBlockEntity)
            return (FluidVesselBlockEntity) blockEntity;
        return null;
    }

    public void applyFluidVesselSize(int blocks) {
        vesselInventory.setCapacity(blocks * getCapacityMultiplier());
        int overflow = vesselInventory.getFluidAmount() - vesselInventory.getCapacity();
        if (overflow > 0)
            vesselInventory.drain(overflow, FluidAction.EXECUTE);
        forceFluidLevelUpdate = true;
    }

    public void removeController(boolean keepFluids) {
        if (level.isClientSide)
            return;
        updateConnectivity = true;
        if (!keepFluids)
            applyFluidVesselSize(1);
        controller = null;
        width = 1;
        length = 1;
        boiler.clear();
        onFluidStackChanged(vesselInventory.getFluid());

        BlockState state = getBlockState();
        if (isVessel(state)) {
            state = state.setValue(POSITIVE, true);
            state = state.setValue(NEGATIVE, true);
            state = state.setValue(SHAPE, window ? Shape.WINDOW : Shape.PLAIN);
            getLevel().setBlock(worldPosition, state, 22);
        }

        refreshCapability();
        setChanged();
        sendData();
    }

    public void toggleWindows() {
        FluidVesselBlockEntity be = getControllerBE();
        if (be == null)
            return;
        if (be.boiler.isActive())
            return;
        be.setWindows(!be.window);
    }

    public void updateBoilerTemperature() {
        FluidVesselBlockEntity be = getControllerBE();
        if (be == null)
            return;
        if (!be.boiler.isActive())
            return;
        be.boiler.needsHeatLevelUpdate = true;
    }

    public void sendDataImmediately() {
        syncCooldown = 0;
        queuedSync = false;
        sendData();
    }

    @Override
    public void sendData() {
        if (syncCooldown > 0) {
            queuedSync = true;
            return;
        }
        super.sendData();
        queuedSync = false;
        syncCooldown = SYNC_RATE;
    }

    public void setWindows(boolean window) {
        this.window = window;
        Axis axis = getAxis();
        for (int yOffset = 0; yOffset < width; yOffset++) {
            for (int lengthOffset = 0; lengthOffset < length; lengthOffset++) {
                for (int widthOffset = 0; widthOffset < width; widthOffset++) {

                    BlockPos pos = this.worldPosition.offset(
                            axis == Axis.X ? lengthOffset : widthOffset,
                            yOffset,
                            axis == Axis.Z ? lengthOffset : widthOffset
                    );
                    BlockState blockState = level.getBlockState(pos);
                    if (!isVessel(blockState))
                        continue;

                    Shape shape = Shape.PLAIN;
                    if (window) {
                        // SIZE 1: Every vessel has a window
                        if (width == 1) {
                            shape = Shape.WINDOW;
                        }
                        // SIZE 2: Every vessel has a corner window
                        else if (width == 2) {
                            shape = yOffset == 0
                                    ? widthOffset == 0
                                    ? Shape.WINDOW_TP
                                    : Shape.WINDOW_TN
                                    : widthOffset == 0
                                    ? Shape.WINDOW_BP
                                    : Shape.WINDOW_BN;
                        }
                        // SIZE 3: Vessels in the center have a window
                        else if (width == 3 && (lengthOffset == 0 || lengthOffset == length - 1) && widthOffset == 1) {
                            shape = yOffset == 0
                                    ? Shape.WINDOW_TOP
                                    : yOffset == 1
                                    ? Shape.WINDOW_MIDDLE
                                    : Shape.WINDOW_BOTTOM;
                        }
                    }

                    level.setBlock(pos, blockState.setValue(SHAPE, shape), 22);
                    level.getChunkSource()
                            .getLightEngine()
                            .checkBlock(pos);
                }
            }
        }
    }

    public void updateBoilerState() {
        if (!isController())
            return;

        boolean wasBoiler = boiler.isActive();
        boolean changed = boiler.evaluate(this);

        if (wasBoiler != boiler.isActive()) {
            if (boiler.isActive())
                setWindows(false);

            Axis axis = getAxis();
            for (int yOffset = 0; yOffset < width; yOffset++)
                for (int lengthOffset = 0; lengthOffset < length; lengthOffset++)
                    for (int widthOffset = 0; widthOffset < width; widthOffset++)
                        if (level.getBlockEntity(
                                worldPosition.offset(
                                        axis == Axis.X ? lengthOffset : widthOffset,
                                        yOffset,
                                        axis == Axis.Z ? lengthOffset : widthOffset
                                )) instanceof FluidVesselBlockEntity fbe)
                            fbe.refreshCapability();
        }

        if (changed) {
            notifyUpdate();
            boiler.checkPipeOrganAdvancement(this);
        }
    }

    @Override
    public void setController(BlockPos controller) {
        if (level.isClientSide && !isVirtual())
            return;
        if (controller.equals(this.controller))
            return;
        this.controller = controller;
        refreshCapability();
        setChanged();
        sendData();
    }

    private void refreshCapability() {
        LazyOptional<IFluidHandler> oldCap = fluidCapability;
        fluidCapability = LazyOptional.of(() -> handlerForCapability());
        oldCap.invalidate();
    }

    private IFluidHandler handlerForCapability() {
        return isController() ? boiler.isActive() ? boiler.createHandler() : vesselInventory
                : getControllerBE() != null ? getControllerBE().handlerForCapability() : new FluidTank(0);
    }

    @Override
    public BlockPos getController() {
        return isController() ? worldPosition : controller;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        if (isController()) {
            Axis axis = getAxis();
            return super.createRenderBoundingBox().expandTowards(
                    axis == Axis.X ? (length - 1) : (width - 1),
                    width - 1,
                    axis == Axis.Z ? (length - 1) : (width - 1)
            );
        } else
            return super.createRenderBoundingBox();
    }

    @Nullable
    public FluidVesselBlockEntity getOtherFluidVesselBlockEntity(Direction direction) {
        BlockEntity otherBE = level.getBlockEntity(worldPosition.relative(direction));
        if (otherBE instanceof FluidVesselBlockEntity)
            return (FluidVesselBlockEntity) otherBE;
        return null;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        FluidVesselBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null)
            return false;
        if (controllerBE.boiler.addToGoggleTooltip(tooltip, isPlayerSneaking, controllerBE.getTotalVesselSize()))
            return true;
        return containedFluidTooltip(tooltip, isPlayerSneaking,
                controllerBE.getCapability(ForgeCapabilities.FLUID_HANDLER));
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        BlockPos controllerBefore = controller;
        int prevWidth = width;
        int prevLength = length;
        int prevLum = luminosity;

        updateConnectivity = compound.contains("Uninitialized");
        luminosity = compound.getInt("Luminosity");
        controller = null;
        lastKnownPos = null;

        if (compound.contains("LastKnownPos"))
            lastKnownPos = NbtUtils.readBlockPos(compound.getCompound("LastKnownPos"));
        if (compound.contains("Controller"))
            controller = NbtUtils.readBlockPos(compound.getCompound("Controller"));

        if (isController()) {
            window = compound.getBoolean("Window");
            width = compound.getInt("Size");
            length = compound.getInt("Length");
            vesselInventory.setCapacity(getTotalVesselSize() * getCapacityMultiplier());
            vesselInventory.readFromNBT(compound.getCompound("VesselContent"));
            if (vesselInventory.getSpace() < 0)
                vesselInventory.drain(-vesselInventory.getSpace(), FluidAction.EXECUTE);
        }

        boiler.read(compound.getCompound("Boiler"), width * width * length);

        if (compound.contains("ForceFluidLevel") || fluidLevel == null)
            fluidLevel = LerpedFloat.linear()
                    .startWithValue(getFillState());

        if (!clientPacket)
            return;

        boolean changeOfController =
                controllerBefore == null ? controller != null : !controllerBefore.equals(controller);
        if (changeOfController || prevWidth != width || prevLength != length) {
            if (hasLevel())
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
            if (isController())
                vesselInventory.setCapacity(getCapacityMultiplier() * getTotalVesselSize());
            invalidateRenderBoundingBox();
        }
        if (isController()) {
            float fillState = getFillState();
            if (compound.contains("ForceFluidLevel") || fluidLevel == null)
                fluidLevel = LerpedFloat.linear()
                        .startWithValue(fillState);
            fluidLevel.chase(fillState, 0.5f, Chaser.EXP);
        }
        if (luminosity != prevLum && hasLevel())
            level.getChunkSource()
                    .getLightEngine()
                    .checkBlock(worldPosition);

        if (compound.contains("LazySync"))
            fluidLevel.chase(fluidLevel.getChaseTarget(), 0.125f, Chaser.EXP);
    }

    public float getFillState() {
        return (float) vesselInventory.getFluidAmount() / vesselInventory.getCapacity();
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        if (updateConnectivity)
            compound.putBoolean("Uninitialized", true);
        compound.put("Boiler", boiler.write());
        if (lastKnownPos != null)
            compound.put("LastKnownPos", NbtUtils.writeBlockPos(lastKnownPos));
        if (!isController())
            compound.put("Controller", NbtUtils.writeBlockPos(controller));
        if (isController()) {
            compound.putBoolean("Window", window);
            compound.put("VesselContent", vesselInventory.writeToNBT(new CompoundTag()));
            compound.putInt("Size", width);
            compound.putInt("Length", length);
        }
        compound.putInt("Luminosity", luminosity);
        super.write(compound, clientPacket);

        if (!clientPacket)
            return;
        if (forceFluidLevelUpdate)
            compound.putBoolean("ForceFluidLevel", true);
        if (queuedSync)
            compound.putBoolean("LazySync", true);
        forceFluidLevelUpdate = false;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (!fluidCapability.isPresent())
            refreshCapability();
        if (cap == ForgeCapabilities.FLUID_HANDLER)
            return fluidCapability.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        registerAwardables(behaviours, AllAdvancements.STEAM_ENGINE_MAXED, AllAdvancements.PIPE_ORGAN);
    }

    public IFluidTank getVesselInventory() {
        return vesselInventory;
    }

    public int getTotalVesselSize() {
        return width * width * length;
    }

    public static int getMaxSize() {
        return MAX_SIZE;
    }

    public static int getCapacityMultiplier() {
        return AllConfigs.server().fluids.fluidTankCapacity.get() * 1000;
    }

    public static int getMaxLength() {
        return AllConfigs.server().fluids.fluidTankMaxHeight.get();
    }

    public LerpedFloat getFluidLevel() {
        return fluidLevel;
    }

    public void setFluidLevel(LerpedFloat fluidLevel) {
        this.fluidLevel = fluidLevel;
    }

    @Override
    public void preventConnectivityUpdate() {
        updateConnectivity = false;
    }

    @Override
    public void notifyMultiUpdated() {
        BlockState state = this.getBlockState();
        if (isVessel(state)) { // safety
            Axis axis = getAxis();
            state = state.setValue(NEGATIVE, axis == Axis.X
                    ? getController().getX() == getBlockPos().getX()
                    : getController().getZ() == getBlockPos().getZ());
            state = state.setValue(POSITIVE, axis == Axis.X
                    ? getController().getX() + length - 1 == getBlockPos().getX()
                    : getController().getZ() + length - 1 == getBlockPos().getZ());
            level.setBlock(getBlockPos(), state, 6);
        }
        if (isController())
            setWindows(window);
        onFluidStackChanged(vesselInventory.getFluid());
        updateBoilerState();
        setChanged();
    }

    @Override
    public void setExtraData(@Nullable Object data) {
        if (data instanceof Boolean)
            window = (boolean) data;
    }

    @Override
    @Nullable
    public Object getExtraData() {
        return window;
    }

    @Override
    public Object modifyExtraData(Object data) {
        if (data instanceof Boolean windows) {
            windows |= window;
            return windows;
        }
        return data;
    }

    @Override
    public Axis getMainConnectionAxis() {
        return getAxis();
    }

    @Override
    public int getMaxLength(Axis longAxis, int width) {
        if (longAxis == Axis.Y) return getMaxWidth();
        return getMaxLength();
    }

    @Override
    public int getMaxWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return length;
    }

    @Override
    public void setHeight(int height) {
        this.length = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public boolean hasTank() {
        return true;
    }

    @Override
    public int getTankSize(int tank) {
        return getCapacityMultiplier();
    }

    @Override
    public void setTankSize(int tank, int blocks) {
        applyFluidVesselSize(blocks);
    }

    @Override
    public IFluidTank getTank(int tank) {
        return vesselInventory;
    }

    @Override
    public FluidStack getFluid(int tank) {
        return vesselInventory.getFluid()
                .copy();
    }
}
