package com.hlysine.create_connected.content.fluidvessel;

import com.hlysine.create_connected.config.CServer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.decoration.steamWhistle.WhistleBlock;
import com.simibubi.create.content.decoration.steamWhistle.WhistleBlockEntity;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.BoilerHeaters;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.BlockStressValues;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlock;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;
import joptsimple.internal.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.minecraft.core.Direction.Axis;

public class BoilerData extends com.simibubi.create.content.fluids.tank.BoilerData {

    static final int SAMPLE_RATE = 5;

    private static final int waterSupplyPerLevel = 10;
    private static final float passiveEngineEfficiency = 1 / 8f;

    // pooled water supply
    int gatheredSupply;
    float[] supplyOverTime = new float[10];
    int ticksUntilNextSample;
    int currentIndex;
    int configLevelCap = 18;

    // display
    private int maxHeatForSize = 0;
    private int maxHeatForWater = 0;
    private int minValue = 0;
    private int maxValue = 0;

    @Override
    public void tick(FluidTankBlockEntity controller) {
        if (!isActive())
            return;

        configLevelCap = CServer.VesselMaxLevel.get();

        if (controller.getLevel().isClientSide) {
            gauge.tickChaser();
            float current = gauge.getValue(1);
            if (current > 1 && Create.RANDOM.nextFloat() < 1 / 2f)
                gauge.setValueNoUpdate(current + Math.min(-(current - 1) * Create.RANDOM.nextFloat(), 0));
            return;
        }
        if (needsHeatLevelUpdate && updateTemperature(controller))
            controller.notifyUpdate();
        ticksUntilNextSample--;
        if (ticksUntilNextSample > 0)
            return;
        int capacity = controller.getTankInventory().getCapacity();
        if (capacity == 0)
            return;

        ticksUntilNextSample = SAMPLE_RATE;
        supplyOverTime[currentIndex] = gatheredSupply / (float) SAMPLE_RATE;
        waterSupply = Math.max(waterSupply, supplyOverTime[currentIndex]);
        currentIndex = (currentIndex + 1) % supplyOverTime.length;
        gatheredSupply = 0;

        if (currentIndex == 0) {
            waterSupply = 0;
            for (float i : supplyOverTime)
                waterSupply = Math.max(i, waterSupply);
        }

        if (controller instanceof CreativeFluidVesselBlockEntity)
            waterSupply = waterSupplyPerLevel * 20;

        if (getActualHeat(controller.getTotalTankSize()) == 18)
            controller.award(AllAdvancements.STEAM_ENGINE_MAXED);

        controller.notifyUpdate();
    }

    @Override
    public int getTheoreticalHeatLevel() {
        return activeHeat;
    }

    @Override
    public int getMaxHeatLevelForBoilerSize(int boilerSize) {
        return (int) Math.min(18, boilerSize / 4);
    }

    @Override
    public int getMaxHeatLevelForWaterSupply() {
        return (int) Math.min(18, Mth.ceil(waterSupply) / waterSupplyPerLevel);
    }

    @Override
    public boolean isPassive() {
        return passiveHeat && maxHeatForSize > 0 && maxHeatForWater > 0;
    }

    @Override
    public boolean isPassive(int boilerSize) {
        calcMinMaxForSize(boilerSize);
        return isPassive();
    }

    @Override
    public float getEngineEfficiency(int boilerSize) {
        if (isPassive(boilerSize))
            return passiveEngineEfficiency / attachedEngines * CServer.VesselBoilerStressMultiplier.get().floatValue();
        if (activeHeat == 0)
            return 0;
        int actualHeat = getActualHeat(boilerSize);
        return (attachedEngines <= actualHeat ? 1 : (float) actualHeat / attachedEngines) * CServer.VesselBoilerStressMultiplier.get().floatValue();
    }

    private int getActualHeat(int boilerSize) {
        int forBoilerSize = getMaxHeatLevelForBoilerSize(boilerSize);
        int forWaterSupply = getMaxHeatLevelForWaterSupply();
        int actualHeat = Math.min(Math.min(activeHeat, Math.min(forWaterSupply, forBoilerSize)), configLevelCap);
        return actualHeat;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking, int boilerSize) {
        if (!isActive())
            return false;

        Component indent = Components.literal(IHaveGoggleInformation.spacing);
        Component indent2 = Components.literal(IHaveGoggleInformation.spacing + " ");

        calcMinMaxForSize(boilerSize);

        if (configLevelCap < 18)
            tooltip.add(indent.plainCopy().append(
                    Lang.translateDirect("boiler.status", getHeatLevelTextComponent().withStyle(ChatFormatting.GREEN).append(Component.literal(" / " + configLevelCap).withStyle(ChatFormatting.GRAY)))
            ));
        else
            tooltip.add(indent.plainCopy().append(
                    Lang.translateDirect("boiler.status", getHeatLevelTextComponent().withStyle(ChatFormatting.GREEN))
            ));
        tooltip.add(indent2.plainCopy()
                .append(getSizeComponent(true, false)));
        tooltip.add(indent2.plainCopy()
                .append(getWaterComponent(true, false)));
        tooltip.add(indent2.plainCopy()
                .append(getHeatComponent(true, false)));

        if (attachedEngines == 0)
            return true;

        int boilerLevel = Math.min(Math.min(activeHeat, Math.min(maxHeatForWater, maxHeatForSize)), configLevelCap);
        double totalSU = getEngineEfficiency(boilerSize) * 16 * Math.max(boilerLevel, attachedEngines)
                * BlockStressValues.getCapacity(AllBlocks.STEAM_ENGINE.get());

        tooltip.add(Components.immutableEmpty());

        if (attachedEngines > 0 && maxHeatForSize > 0 && maxHeatForWater == 0 && (passiveHeat ? 1 : activeHeat) > 0) {
            Lang.translate("boiler.water_input_rate")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
            Lang.number(waterSupply)
                    .style(ChatFormatting.BLUE)
                    .add(Lang.translate("generic.unit.millibuckets"))
                    .add(Lang.text(" / ")
                            .style(ChatFormatting.GRAY))
                    .add(Lang.translate("boiler.per_tick", Lang.number(waterSupplyPerLevel)
                                    .add(Lang.translate("generic.unit.millibuckets")))
                            .style(ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 1);
            return true;
        }

        Lang.translate("tooltip.capacityProvided")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);

        Lang.number(totalSU)
                .translate("generic.unit.stress")
                .style(ChatFormatting.AQUA)
                .space()
                .add((attachedEngines == 1 ? Lang.translate("boiler.via_one_engine")
                        : Lang.translate("boiler.via_engines", attachedEngines)).style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);

        return true;
    }

    @Override
    public void calcMinMaxForSize(int boilerSize) {
        maxHeatForSize = getMaxHeatLevelForBoilerSize(boilerSize);
        maxHeatForWater = getMaxHeatLevelForWaterSupply();

        minValue = Math.min(configLevelCap, Math.min(passiveHeat ? 1 : activeHeat, Math.min(maxHeatForWater, maxHeatForSize)));
        maxValue = Math.max(passiveHeat ? 1 : activeHeat, Math.max(maxHeatForWater, maxHeatForSize));
    }

    @Override
    @NotNull
    public MutableComponent getHeatLevelTextComponent() {
        int boilerLevel = Math.min(Math.min(activeHeat, Math.min(maxHeatForWater, maxHeatForSize)), configLevelCap);

        return isPassive() ? Lang.translateDirect("boiler.passive")
                : (boilerLevel == 0 ? Lang.translateDirect("boiler.idle")
                : boilerLevel == 18 ? Lang.translateDirect("boiler.max_lvl")
                : Lang.translateDirect("boiler.lvl", String.valueOf(boilerLevel)));
    }

    @Override
    public MutableComponent getSizeComponent(boolean forGoggles, boolean useBlocksAsBars, ChatFormatting... styles) {
        return componentHelper("size", maxHeatForSize, forGoggles, useBlocksAsBars, styles);
    }

    @Override
    public MutableComponent getWaterComponent(boolean forGoggles, boolean useBlocksAsBars, ChatFormatting... styles) {
        return componentHelper("water", maxHeatForWater, forGoggles, useBlocksAsBars, styles);
    }

    @Override
    public MutableComponent getHeatComponent(boolean forGoggles, boolean useBlocksAsBars, ChatFormatting... styles) {
        return componentHelper("heat", passiveHeat ? 1 : activeHeat, forGoggles, useBlocksAsBars, styles);
    }

    private MutableComponent componentHelper(String label, int level, boolean forGoggles, boolean useBlocksAsBars,
                                             ChatFormatting... styles) {
        MutableComponent base = useBlocksAsBars ? blockComponent(level) : barComponent(level);

        if (!forGoggles)
            return base;

        ChatFormatting style1 = styles.length >= 1 ? styles[0] : ChatFormatting.GRAY;
        ChatFormatting style2 = styles.length >= 2 ? styles[1] : ChatFormatting.DARK_GRAY;

        return Lang.translateDirect("boiler." + label)
                .withStyle(style1)
                .append(Lang.translateDirect("boiler." + label + "_dots")
                        .withStyle(style2))
                .append(base);
    }

    private MutableComponent blockComponent(int level) {
        return Components.literal(
                "" + "\u2588".repeat(minValue) + "\u2592".repeat(level - minValue) + "\u2591".repeat(maxValue - level));
    }

    private MutableComponent barComponent(int level) {
        return Components.empty()
                .append(bars(Math.max(0, minValue - 1), ChatFormatting.DARK_GREEN))
                .append(bars(minValue > 0 ? 1 : 0, ChatFormatting.GREEN))
                .append(bars(Math.max(0, level - minValue), ChatFormatting.DARK_GREEN))
                .append(bars(Math.max(0, maxValue - level), ChatFormatting.DARK_RED))
                .append(bars(Math.max(0, Math.min(18 - maxValue, ((maxValue / 5 + 1) * 5) - maxValue)),
                        ChatFormatting.DARK_GRAY));

    }

    private MutableComponent bars(int level, ChatFormatting format) {
        return Components.literal(Strings.repeat('|', level))
                .withStyle(format);
    }

    @Override
    public boolean evaluate(FluidTankBlockEntity base) {
        if (!(base instanceof FluidVesselBlockEntity controller))
            return super.evaluate(base);

        BlockPos controllerPos = controller.getBlockPos();
        Level level = controller.getLevel();
        configLevelCap = CServer.VesselMaxLevel.get();
        int prevEngines = attachedEngines;
        int prevWhistles = attachedWhistles;
        attachedEngines = 0;
        attachedWhistles = 0;

        Axis axis = controller.getAxis();
        for (int yOffset = 0; yOffset < controller.getWidth(); yOffset++) {
            for (int lengthOffset = 0; lengthOffset < controller.getHeight(); lengthOffset++) {
                for (int widthOffset = 0; widthOffset < controller.getWidth(); widthOffset++) {

                    BlockPos pos = controllerPos.offset(
                            axis == Axis.X ? lengthOffset : widthOffset,
                            yOffset,
                            axis == Axis.Z ? lengthOffset : widthOffset
                    );
                    BlockState blockState = level.getBlockState(pos);
                    if (!FluidVesselBlock.isVessel(blockState))
                        continue;
                    for (Direction d : Iterate.directions) {
                        BlockPos attachedPos = pos.relative(d);
                        BlockState attachedState = level.getBlockState(attachedPos);
                        if (AllBlocks.STEAM_ENGINE.has(attachedState) && SteamEngineBlock.getFacing(attachedState) == d)
                            attachedEngines++;
                        if (AllBlocks.STEAM_WHISTLE.has(attachedState)
                                && WhistleBlock.getAttachedDirection(attachedState)
                                .getOpposite() == d)
                            attachedWhistles++;
                    }
                }
            }
        }

        needsHeatLevelUpdate = true;
        return prevEngines != attachedEngines || prevWhistles != attachedWhistles;
    }

    @Override
    public void checkPipeOrganAdvancement(FluidTankBlockEntity base) {
        if (!(base instanceof FluidVesselBlockEntity controller)) {
            super.checkPipeOrganAdvancement(base);
            return;
        }

        if (!controller.getBehaviour(AdvancementBehaviour.TYPE)
                .isOwnerPresent())
            return;

        BlockPos controllerPos = controller.getBlockPos();
        Level level = controller.getLevel();
        Set<Integer> whistlePitches = new HashSet<>();

        Axis axis = controller.getAxis();
        for (int yOffset = 0; yOffset < controller.getWidth(); yOffset++) {
            for (int lengthOffset = 0; lengthOffset < controller.getHeight(); lengthOffset++) {
                for (int widthOffset = 0; widthOffset < controller.getWidth(); widthOffset++) {

                    BlockPos pos = controllerPos.offset(
                            axis == Axis.X ? lengthOffset : widthOffset,
                            yOffset,
                            axis == Axis.Z ? lengthOffset : widthOffset
                    );
                    BlockState blockState = level.getBlockState(pos);
                    if (!FluidVesselBlock.isVessel(blockState))
                        continue;
                    for (Direction d : Iterate.directions) {
                        BlockPos attachedPos = pos.relative(d);
                        BlockState attachedState = level.getBlockState(attachedPos);
                        if (AllBlocks.STEAM_WHISTLE.has(attachedState)
                                && WhistleBlock.getAttachedDirection(attachedState)
                                .getOpposite() == d) {
                            if (level.getBlockEntity(attachedPos) instanceof WhistleBlockEntity wbe)
                                whistlePitches.add(wbe.getPitchId());
                        }
                    }
                }
            }
        }

        if (whistlePitches.size() >= 12)
            controller.award(AllAdvancements.PIPE_ORGAN);
    }

    @Override
    public boolean updateTemperature(FluidTankBlockEntity base) {
        if (!(base instanceof FluidVesselBlockEntity controller))
            return super.evaluate(base);

        BlockPos controllerPos = controller.getBlockPos();
        Level level = controller.getLevel();
        needsHeatLevelUpdate = false;

        boolean prevPassive = passiveHeat;
        int prevActive = activeHeat;
        passiveHeat = false;
        activeHeat = 0;

        Axis axis = controller.getAxis();
        for (int lengthOffset = 0; lengthOffset < controller.getHeight(); lengthOffset++) {
            for (int widthOffset = 0; widthOffset < controller.getWidth(); widthOffset++) {
                BlockPos pos = controllerPos.offset(
                        axis == Axis.X ? lengthOffset : widthOffset,
                        -1,
                        axis == Axis.Z ? lengthOffset : widthOffset
                );
                BlockState blockState = level.getBlockState(pos);
                float heat = BoilerHeaters.getActiveHeat(level, pos, blockState);
                if (heat == 0) {
                    passiveHeat = true;
                } else if (heat > 0) {
                    activeHeat += heat;
                }
            }
        }

        activeHeat = Math.max(0, Math.min(18, (int) Math.floor(activeHeat * CServer.VesselHeatMultiplier.get().floatValue())));
        passiveHeat &= activeHeat == 0;

        return prevActive != activeHeat || prevPassive != passiveHeat;
    }

    @Override
    public boolean isActive() {
        return attachedEngines > 0 || attachedWhistles > 0;
    }

    @Override
    public void clear() {
        waterSupply = 0;
        activeHeat = 0;
        passiveHeat = false;
        attachedEngines = 0;
        Arrays.fill(supplyOverTime, 0);
    }

    @Override
    public CompoundTag write() {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("Supply", waterSupply);
        nbt.putInt("ActiveHeat", activeHeat);
        nbt.putBoolean("PassiveHeat", passiveHeat);
        nbt.putInt("Engines", attachedEngines);
        nbt.putInt("Whistles", attachedWhistles);
        nbt.putBoolean("Update", needsHeatLevelUpdate);
        return nbt;
    }

    @Override
    public void read(CompoundTag nbt, int boilerSize) {
        waterSupply = nbt.getFloat("Supply");
        activeHeat = nbt.getInt("ActiveHeat");
        passiveHeat = nbt.getBoolean("PassiveHeat");
        attachedEngines = nbt.getInt("Engines");
        attachedWhistles = nbt.getInt("Whistles");
        needsHeatLevelUpdate = nbt.getBoolean("Update");
        Arrays.fill(supplyOverTime, (int) waterSupply);

        int forBoilerSize = getMaxHeatLevelForBoilerSize(boilerSize);
        int forWaterSupply = getMaxHeatLevelForWaterSupply();
        int actualHeat = Math.min(activeHeat, Math.min(forWaterSupply, forBoilerSize));
        float target = isPassive(boilerSize) ? 1 / 8f : forBoilerSize == 0 ? 0 : actualHeat / (forBoilerSize * 1f);
        gauge.chase(target, 0.125f, Chaser.EXP);
    }

    @Override
    public BoilerFluidHandler createHandler() {
        return new BoilerFluidHandler();
    }

    public class BoilerFluidHandler extends com.simibubi.create.content.fluids.tank.BoilerData.BoilerFluidHandler {

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (!isFluidValid(0, resource))
                return 0;
            int amount = resource.getAmount();
            if (action.execute())
                gatheredSupply += amount;
            return amount;
        }

    }

}
