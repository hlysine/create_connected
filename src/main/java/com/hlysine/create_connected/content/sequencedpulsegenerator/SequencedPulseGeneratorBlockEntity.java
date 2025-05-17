package com.hlysine.create_connected.content.sequencedpulsegenerator;

import com.hlysine.create_connected.content.sequencedpulsegenerator.instructions.*;
import com.hlysine.create_connected.datagen.advancements.AdvancementBehaviour;
import com.hlysine.create_connected.datagen.advancements.CCAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Vector;
import java.util.function.Function;

import static com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlock.POWERING;
import static net.minecraft.world.level.block.DiodeBlock.POWERED;

public class SequencedPulseGeneratorBlockEntity extends SmartBlockEntity {

    public static final int INSTRUCTION_CAPACITY = 7;
    private static final int MAX_RECURSION_DEPTH = 10;
    private static final float PARTICLE_DENSITY = 0.2f;

    static {
        Instruction.register(new OutputInstruction(10, 15));
        Instruction.register(new WaitForInstruction(1, 0));
        Instruction.register(new LoopForInstruction(3));
        Instruction.register(new LoopIfInstruction(1));
        Instruction.register(new LoopInstruction());
        Instruction.register(new EndInstruction());
    }

    Vector<Instruction> instructions;
    int currentInstruction;
    int currentSignal;
    boolean poweredPreviously;
    boolean isPowered;
    int infiniteLoopCounter;

    public SequencedPulseGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        instructions = Instruction.createDefault();
        currentInstruction = -1;
        currentSignal = 0;
        poweredPreviously = false;
        infiniteLoopCounter = 0;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        AdvancementBehaviour.registerAwardables(this, behaviours, CCAdvancements.PULSE_GEN_INFINITE_LOOP);
    }

    public boolean isIdle() {
        return currentInstruction < 0;
    }

    public int getCurrentSignal() {
        return currentSignal;
    }

    public boolean isPoweredPreviously() {
        return poweredPreviously;
    }

    /**
     * More reliable than checking block state because that may not be updated yet
     */
    public boolean isPowered() {
        return isPowered;
    }

    public Instruction getCurrentInstruction() {
        return currentInstruction >= 0 && currentInstruction < instructions.size()
                ? instructions.get(currentInstruction)
                : null;
    }

    private void executeInstruction(Function<Instruction, Function<SequencedPulseGeneratorBlockEntity, InstructionResult>> instructionEvent, boolean allowImmediate) {
        executeInstruction(instructionEvent, allowImmediate, 0);
    }

    private void applySignal() {
        if (getBlockState().getValue(POWERING) != (currentSignal > 0)) {
            level.setBlock(getBlockPos(), getBlockState().setValue(POWERING, currentSignal > 0), 2);
        }
        ((SequencedPulseGeneratorBlock) getBlockState().getBlock()).updateNeighborsInFront(level, getBlockPos(), getBlockState());
    }

    private void executeInstruction(Function<Instruction, Function<SequencedPulseGeneratorBlockEntity, InstructionResult>> instructionEvent, boolean allowImmediate, int recursionDepth) {
        Instruction instruction = getCurrentInstruction();
        if (instruction == null) {
            currentInstruction = -1;
            if (currentSignal != 0) {
                currentSignal = 0;
                applySignal();
            }
            return;
        }
        InstructionResult result = instructionEvent.apply(instruction).apply(this);
        int prevSignal = currentSignal;
        currentSignal = instruction.getSignal();
        if (prevSignal != currentSignal) {
            applySignal();
        }
        currentInstruction = result.getNextInstruction(currentInstruction);
        if (result.isImmediate() && allowImmediate) {
            if (recursionDepth < MAX_RECURSION_DEPTH) {
                executeInstruction(instructionEvent, true, recursionDepth + 1);
            } else {
                infiniteLoopCounter++;
                if (level.getRandom().nextFloat() < PARTICLE_DENSITY) {
                    Vec3 loc = Vec3.atBottomCenterOf(getBlockPos());
                    ((ServerLevel) level).sendParticles(ParticleTypes.SMOKE, loc.x, loc.y, loc.z, 2, 0.1, 0, 0.1, 0.01);
                }
                if (!level.isClientSide() && infiniteLoopCounter > 101) {
                    infiniteLoopCounter = 0;
                    AdvancementBehaviour.tryAward(this, CCAdvancements.PULSE_GEN_INFINITE_LOOP);
                }
            }
        } else {
            infiniteLoopCounter = 0;
        }
        if (recursionDepth == 0) {
            notifyUpdate();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (isIdle())
            return;
        if (level.isClientSide)
            return;

        isPowered = getBlockState().getValue(POWERED);
        executeInstruction(i -> i::tick, true);
    }

    private void resetAllInstructions() {
        Vector<Instruction> newInstructions = new Vector<>(instructions.capacity());
        instructions.forEach(i -> newInstructions.add(i.copy()));
        instructions = newInstructions;
    }

    public void onRedstoneUpdate(boolean isPowered) {
        this.isPowered = isPowered;
        if (isPowered == poweredPreviously) return;
        if (!poweredPreviously && isPowered && !isIdle())
            executeInstruction(i -> i::onRisingEdge, false);
        if (poweredPreviously && !isPowered && !isIdle())
            executeInstruction(i -> i::onFallingEdge, false);
        poweredPreviously = isPowered;
        if (!isIdle() || !isPowered)
            return;
        if (!level.hasNeighborSignal(worldPosition)) {
            level.setBlock(worldPosition, getBlockState().setValue(POWERED, false), 3);
            return;
        }
        currentInstruction = 0;

        resetAllInstructions();

        executeInstruction(i -> i::tick, true);
    }

    public void reset() {
        resetAllInstructions();
        currentInstruction = -1;
        infiniteLoopCounter = 0;
        currentSignal = 0;
        applySignal();
        notifyUpdate();
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        tag.putInt("InstructionIndex", currentInstruction);
        tag.putBoolean("PrevPowered", poweredPreviously);
        tag.putInt("CurrentSignal", currentSignal);
        tag.put("Instructions", Instruction.serializeAll(instructions));
        super.write(tag, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        currentInstruction = tag.getInt("InstructionIndex");
        poweredPreviously = tag.getBoolean("PrevPowered");
        currentSignal = tag.getInt("CurrentSignal");
        ListTag list = tag.getList("Instructions", Tag.TAG_COMPOUND);
        instructions = Instruction.deserializeAll(list);
        super.read(tag, registries, clientPacket);
    }
}
