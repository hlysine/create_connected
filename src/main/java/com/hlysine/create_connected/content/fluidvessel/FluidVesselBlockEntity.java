package com.hlysine.create_connected.content.fluidvessel;

import com.hlysine.create_connected.CCBlockEntityTypes;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;
import java.util.List;

import static com.hlysine.create_connected.content.fluidvessel.FluidVesselBlock.*;
import static net.minecraft.core.Direction.Axis;

public class FluidVesselBlockEntity extends FluidTankBlockEntity implements IHaveGoggleInformation, IMultiBlockEntityContainer.Fluid {

    private static final int MAX_SIZE = 3;
    private static final int SYNC_RATE = 8;

    protected WindowType windowType;

    // For rendering purposes only
    private LerpedFloat fluidLevel;

    public FluidVesselBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        windowType = WindowType.SIDE_WIDE;
        boiler = new BoilerData();
        refreshCapability();
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                CCBlockEntityTypes.FLUID_VESSEL.get(),
                (be, context) -> {
                    if (be.fluidCapability == null)
                        be.refreshCapability();
                    return be.fluidCapability;
                }
        );
    }

    @Override
    protected SmartFluidTank createInventory() {
        return new SmartFluidTank(getCapacityMultiplier(), this::onFluidStackChanged);
    }

    @Override
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
        if (fluidLevel != null)
            fluidLevel.tickChaser();
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

    private void onPositionChanged() {
        removeController(true);
        lastKnownPos = worldPosition;
    }

    @Override
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

            for (int lengthOffset = 0; lengthOffset < height; lengthOffset++) {
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
            fluidLevel.chase(getFillState(), .5f, LerpedFloat.Chaser.EXP);
        }
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

    @Override
    public void removeController(boolean keepFluids) {
        if (level.isClientSide)
            return;
        updateConnectivity = true;
        if (!keepFluids)
            applyFluidTankSize(1);
        controller = null;
        width = 1;
        height = 1;
        boiler.clear();
        onFluidStackChanged(tankInventory.getFluid());

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

    public boolean isWindowTypeAllowed(WindowType type) {
        return switch (type) {
            case SIDE_WIDE -> true;
            case SIDE_NARROW_ENDS -> height >= 2;
            case SIDE_NARROW_THIRDS -> height >= 3;
            case SIDE_HORIZONTAL -> width > 2 && width % 2 == 1;
        };
    }

    @Override
    public void toggleWindows() {
        FluidVesselBlockEntity be = getControllerBE();
        if (be == null)
            return;
        if (be.boiler.isActive())
            return;
        if (!be.window) {
            be.setWindowType(WindowType.SIDE_WIDE);
            be.setWindows(true);
        } else {
            WindowType[] types = WindowType.values();
            if (be.windowType.ordinal() >= types.length - 1) {
                be.setWindows(false);
                return;
            }
            WindowType nextType = types[be.windowType.ordinal() + 1];
            while (!be.isWindowTypeAllowed(nextType)) {
                if (nextType.ordinal() >= types.length - 1) {
                    be.setWindows(false);
                    return;
                }
                nextType = types[nextType.ordinal() + 1];
            }
            be.setWindowType(nextType);
            be.setWindows(true);
        }
    }

    public WindowType getWindowType() {
        return windowType;
    }

    public void setWindowType(WindowType windowType) {
        this.windowType = windowType;
    }

    @Override
    public void setWindows(boolean window) {
        this.window = window;
        Axis axis = getAxis();
        for (int yOffset = 0; yOffset < width; yOffset++) {
            for (int lengthOffset = 0; lengthOffset < height; lengthOffset++) {
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
                    if (window)
                        if (windowType == WindowType.SIDE_HORIZONTAL) {
                            if (yOffset == width / 2) {
                                shape = Shape.WINDOW;
                            }
                        } else if (windowType == WindowType.SIDE_WIDE || height <= 1) {
                            if ((widthOffset == 0 || widthOffset == width - 1)) {
                                if (width == 1)
                                    shape = Shape.WINDOW;
                                else if (yOffset == 0)
                                    shape = Shape.WINDOW_TOP;
                                else if (yOffset == width - 1)
                                    shape = Shape.WINDOW_BOTTOM;
                                else
                                    shape = Shape.WINDOW_MIDDLE;
                            }
                        } else if (windowType == WindowType.SIDE_NARROW_ENDS || windowType == WindowType.SIDE_NARROW_THIRDS) {
                            int windowOffset = windowType == WindowType.SIDE_NARROW_ENDS ? 0 : Math.max(1, height / 3 - 1);
                            if ((lengthOffset == windowOffset || lengthOffset == height - 1 - windowOffset) && (widthOffset == 0 || widthOffset == width - 1)) {
                                if (width == 1)
                                    shape = Shape.WINDOW_SINGLE;
                                else if (yOffset == 0)
                                    shape = Shape.WINDOW_TOP_SINGLE;
                                else if (yOffset == width - 1)
                                    shape = Shape.WINDOW_BOTTOM_SINGLE;
                                else
                                    shape = Shape.WINDOW_MIDDLE_SINGLE;
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

    @Override
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
                for (int lengthOffset = 0; lengthOffset < height; lengthOffset++)
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
        fluidCapability = handlerForCapability();
        invalidateCapabilities();
    }

    private IFluidHandler handlerForCapability() {
        return isController() ? (boiler.isActive() ? boiler.createHandler() : tankInventory)
                : ((getControllerBE() != null) ? getControllerBE().handlerForCapability() : new FluidTank(0));
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
                    axis == Axis.X ? (height - 1) : (width - 1),
                    width - 1,
                    axis == Axis.Z ? (height - 1) : (width - 1)
            );
        } else
            return super.createRenderBoundingBox();
    }

    @Override
    @Nullable
    public FluidVesselBlockEntity getOtherFluidTankBlockEntity(Direction direction) {
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
        if (controllerBE.boiler.addToGoggleTooltip(tooltip, isPlayerSneaking, controllerBE.getTotalTankSize()))
            return true;
        return containedFluidTooltip(tooltip, isPlayerSneaking,
                level.getCapability(Capabilities.FluidHandler.BLOCK, controllerBE.getBlockPos(), null));
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);

        BlockPos controllerBefore = controller;
        int prevWidth = width;
        int prevLength = height;
        int prevLum = luminosity;

        updateConnectivity = compound.contains("Uninitialized");
        luminosity = compound.getInt("Luminosity");

        lastKnownPos = null;
        if (compound.contains("LastKnownPos"))
            lastKnownPos = NBTHelper.readBlockPos(compound, "LastKnownPos");

        controller = null;
        if (compound.contains("Controller"))
            controller = NBTHelper.readBlockPos(compound, "Controller");

        if (isController()) {
            window = compound.getBoolean("Window");
            windowType = NBTHelper.readEnum(compound, "WindowType", WindowType.class);
            width = compound.getInt("Size");
            height = compound.getInt("Height");
            tankInventory.setCapacity(getTotalTankSize() * getCapacityMultiplier());

            tankInventory.readFromNBT(registries, compound.getCompound("TankContent"));
            if (tankInventory.getSpace() < 0)
                tankInventory.drain(-tankInventory.getSpace(), FluidAction.EXECUTE);
        }

        boiler.read(compound.getCompound("Boiler"), width * width * height);

        if (compound.contains("ForceFluidLevel") || fluidLevel == null)
            fluidLevel = LerpedFloat.linear()
                    .startWithValue(getFillState());

        updateCapability = true;

        if (!clientPacket)
            return;

        boolean changeOfController =
                controllerBefore == null ? controller != null : !controllerBefore.equals(controller);
        if (changeOfController || prevWidth != width || prevLength != height) {
            if (hasLevel())
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
            if (isController())
                tankInventory.setCapacity(getCapacityMultiplier() * getTotalTankSize());
            invalidateRenderBoundingBox();
        }
        if (isController()) {
            float fillState = getFillState();
            if (compound.contains("ForceFluidLevel") || fluidLevel == null)
                fluidLevel = LerpedFloat.linear()
                        .startWithValue(fillState);
            fluidLevel.chase(fillState, 0.5f, LerpedFloat.Chaser.EXP);
        }
        if (luminosity != prevLum && hasLevel())
            level.getChunkSource()
                    .getLightEngine()
                    .checkBlock(worldPosition);

        if (compound.contains("LazySync"))
            fluidLevel.chase(fluidLevel.getChaseTarget(), 0.125f, LerpedFloat.Chaser.EXP);
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (updateConnectivity)
            compound.putBoolean("Uninitialized", true);
        compound.put("Boiler", boiler.write());
        if (lastKnownPos != null)
            compound.put("LastKnownPos", NbtUtils.writeBlockPos(lastKnownPos));
        if (!isController())
            compound.put("Controller", NbtUtils.writeBlockPos(controller));
        if (isController()) {
            compound.putBoolean("Window", window);
            NBTHelper.writeEnum(compound, "WindowType", windowType);
            compound.put("TankContent", tankInventory.writeToNBT(registries, new CompoundTag()));
            compound.putInt("Size", width);
            compound.putInt("Height", height);
        }
        compound.putInt("Luminosity", luminosity);
        super.write(compound, registries, clientPacket);

        if (!clientPacket)
            return;
        if (forceFluidLevelUpdate)
            compound.putBoolean("ForceFluidLevel", true);
        if (queuedSync)
            compound.putBoolean("LazySync", true);
        forceFluidLevelUpdate = false;
    }

    public static int getMaxSize() {
        return MAX_SIZE;
    }

    public static int getCapacityMultiplier() {
        return AllConfigs.server().fluids.fluidTankCapacity.get() * 1000;
    }

    public static int getMaxHeight() {
        return AllConfigs.server().fluids.fluidTankMaxHeight.get();
    }

    @Override
    public LerpedFloat getFluidLevel() {
        return fluidLevel;
    }

    @Override
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
                    ? getController().getX() + height - 1 == getBlockPos().getX()
                    : getController().getZ() + height - 1 == getBlockPos().getZ());
            level.setBlock(getBlockPos(), state, 6);
        }
        if (isController())
            setWindows(window);
        onFluidStackChanged(tankInventory.getFluid());
        updateBoilerState();
        setChanged();
    }

    @Override
    public void setExtraData(@Nullable Object data) {
        if (data == null) {
            window = false;
            windowType = WindowType.SIDE_WIDE;
        } else if (data instanceof WindowType type) {
            window = true;
            windowType = type;
        }
    }

    @Override
    @Nullable
    public Object getExtraData() {
        return window ? windowType : null;
    }

    @Override
    public Object modifyExtraData(Object data) {
        if (data == null || (data instanceof WindowType)) {
            if (data != null && !window) return data;
            if (window) return windowType;
            return null;
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
        return getMaxHeight();
    }

    @Override
    public int getMaxWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
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
    public int getTankSize(int tank) {
        return getCapacityMultiplier();
    }

    @Override
    public void setTankSize(int tank, int blocks) {
        applyFluidTankSize(blocks);
    }

    public boolean hasWindow() {
        return window;
    }

    public int getLuminosity() {
        return luminosity;
    }

}
