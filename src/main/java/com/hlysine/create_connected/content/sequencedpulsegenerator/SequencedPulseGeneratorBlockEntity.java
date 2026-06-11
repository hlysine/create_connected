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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Vector;

import static com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlock.POWERING;
import static net.minecraft.world.level.block.DiodeBlock.POWERED;

public class SequencedPulseGeneratorBlockEntity extends SmartBlockEntity {

    public static final int INSTRUCTION_CAPACITY = 7;
    private static final int MAX_RECURSION_DEPTH = 10;
    private static final float PARTICLE_DENSITY = 0.2f;

    static {
        Instruction.register(new OutputInstruction(10, 15));
        Instruction.register(new TransformInstruction(2, 15));
        Instruction.register(new WaitForInstruction(1, 0));
        Instruction.register(new WaitForMinInstruction(8, 0));
        Instruction.register(new WaitForMaxInstruction(7, 0));
        Instruction.register(new WaitForExactInstruction(7, 0));
        Instruction.register(new LoopForInstruction(3));
        Instruction.register(new LoopIfInstruction(1));
        Instruction.register(new LoopIfMinInstruction(8));
        Instruction.register(new LoopIfMaxInstruction(7));
        Instruction.register(new LoopIfExactInstruction(7));
        Instruction.register(new LoopInstruction());
        Instruction.register(new EndInstruction());
    }

    Vector<Instruction> instructions;
    int currentInstruction;
    int currentSignal;
    int previousInput;
    int currentInput;
    int infiniteLoopCounter;

    public SequencedPulseGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        instructions = Instruction.createDefault();
        currentInstruction = -1;
        currentSignal = 0;
        previousInput = 0;
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

    public int getPreviousInput() {
        return previousInput;
    }

    /**
     * More reliable than checking block state because that may not be updated yet
     */
    public int getCurrentInput() {
        return currentInput;
    }

    public Instruction getCurrentInstruction() {
        return currentInstruction >= 0 && currentInstruction < instructions.size()
                ? instructions.get(currentInstruction)
                : null;
    }

    private void applySignal() {
        level.setBlock(getBlockPos(), getBlockState().setValue(POWERING, currentSignal > 0), Block.UPDATE_ALL);
        level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
        level.updateNeighborsAt(this.worldPosition.relative(this.getBlockState().getValue(SequencedPulseGeneratorBlock.FACING).getOpposite()), this.getBlockState().getBlock());
    }

    private void executeInstruction(boolean allowImmediate, int recursionDepth) {
        Instruction instruction = getCurrentInstruction();
        if (instruction == null) {
            currentInstruction = -1;
            if (currentSignal != 0) {
                currentSignal = 0;
                applySignal();
            }
            return;
        }
        InstructionResult result = instruction.tick(this);
        int prevSignal = currentSignal;
        currentSignal = instruction.transformOutput(this, instruction.getSignal());
        if (prevSignal != currentSignal) {
            applySignal();
        }
        currentInstruction = result.getNextInstruction(currentInstruction);
        if (result.isImmediate() && allowImmediate) {
            if (recursionDepth < MAX_RECURSION_DEPTH) {
                executeInstruction(true, recursionDepth + 1);
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

        executeInstruction(true, 0);
        previousInput = currentInput;
    }

    private void resetAllInstructions() {
        Vector<Instruction> newInstructions = new Vector<>(instructions.capacity());
        instructions.forEach(i -> newInstructions.add(i.copy()));
        instructions = newInstructions;
    }

    public void onRedstoneUpdate(int input) {
        this.currentInput = input;
        if (currentInput == previousInput) return;
        if (!isIdle() || currentInput == 0) {
            previousInput = currentInput;
            return;
        }
        if (!level.hasNeighborSignal(worldPosition)) {
            level.setBlock(worldPosition, getBlockState().setValue(POWERED, false), 3);
            previousInput = currentInput;
            return;
        }
        currentInstruction = 0;
        resetAllInstructions();
        executeInstruction(true, 0);
        previousInput = currentInput;
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
        tag.putInt("PrevInput", previousInput);
        tag.putInt("CurrentInput", currentInput);
        tag.putInt("CurrentSignal", currentSignal);
        tag.put("Instructions", Instruction.serializeAll(instructions));
        super.write(tag, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        currentInstruction = tag.getInt("InstructionIndex");
        previousInput = tag.getInt("PrevInput");
        currentInput = tag.getInt("CurrentInput");
        currentSignal = tag.getInt("CurrentSignal");
        ListTag list = tag.getList("Instructions", Tag.TAG_COMPOUND);
        instructions = Instruction.deserializeAll(list);
        super.read(tag, registries, clientPacket);
    }
}
