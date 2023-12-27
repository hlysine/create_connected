package com.hlysine.create_connected.content.sequencedpulsegenerator;

import com.hlysine.create_connected.content.sequencedpulsegenerator.instructions.EndInstruction;
import com.hlysine.create_connected.content.sequencedpulsegenerator.instructions.Instruction;
import com.hlysine.create_connected.content.sequencedpulsegenerator.instructions.InstructionResult;
import com.hlysine.create_connected.content.sequencedpulsegenerator.instructions.TimeInstruction;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Vector;
import java.util.function.Function;

import static com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlock.POWERING;
import static net.minecraft.world.level.block.DiodeBlock.POWERED;

public class SequencedPulseGeneratorBlockEntity extends SmartBlockEntity {

    public static final int INSTRUCTION_CAPACITY = 7;
    private static final int MAX_RECURSION_DEPTH = 10;

    static {
        Instruction.register(new TimeInstruction(10, 15));
        Instruction.register(new EndInstruction());
    }

    Vector<Instruction> instructions;
    int currentInstruction;
    int currentSignal;
    boolean poweredPreviously;
    boolean isPowered;

    public SequencedPulseGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        instructions = Instruction.createDefault();
        currentInstruction = -1;
        currentSignal = 0;
        poweredPreviously = false;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
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

    private void executeInstruction(Function<Instruction, Function<SequencedPulseGeneratorBlockEntity, InstructionResult>> instructionEvent) {
        executeInstruction(instructionEvent, 0);
    }

    private void executeInstruction(Function<Instruction, Function<SequencedPulseGeneratorBlockEntity, InstructionResult>> instructionEvent, int recursionDepth) {
        Instruction instruction = getCurrentInstruction();
        if (instruction == null) {
            currentInstruction = -1;
            return;
        }
        InstructionResult result = instructionEvent.apply(instruction).apply(this);
        int prevSignal = currentSignal;
        currentSignal = instruction.getSignal();
        if (prevSignal != currentSignal) {
            if (getBlockState().getValue(POWERING) == (currentSignal > 0)) {
                level.markAndNotifyBlock(getBlockPos(), level.getChunkAt(getBlockPos()), getBlockState(), getBlockState(), 3, 512);
            } else {
                level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(POWERING, currentSignal > 0));
            }
        }
        currentInstruction = result.getNextInstruction(currentInstruction);
        if (result.isImmediate() && recursionDepth < MAX_RECURSION_DEPTH)
            executeInstruction(instructionEvent, recursionDepth + 1);
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
        executeInstruction(i -> i::tick);
    }

    public void onRedstoneUpdate(boolean isPowered) {
        this.isPowered = isPowered;
        if (isPowered == poweredPreviously) return;
        if (!poweredPreviously && isPowered && !isIdle())
            executeInstruction(i -> i::onRisingEdge);
        if (poweredPreviously && !isPowered && !isIdle())
            executeInstruction(i -> i::onFallingEdge);
        poweredPreviously = isPowered;
        if (!isIdle() || !isPowered)
            return;
        if (!level.hasNeighborSignal(worldPosition)) {
            level.setBlock(worldPosition, getBlockState().setValue(POWERED, false), 3);
            return;
        }
        currentInstruction = 0;

        // copy instructions to reset states
        Vector<Instruction> newInstructions = new Vector<>(instructions.capacity());
        instructions.forEach(i -> newInstructions.add(i.copy()));
        instructions = newInstructions;

        executeInstruction(i -> i::onRisingEdge);
    }

    @Override
    protected void write(CompoundTag nbt, boolean clientPacket) {
        nbt.putInt("InstructionIndex", currentInstruction);
        nbt.putBoolean("PrevPowered", poweredPreviously);
        nbt.putInt("CurrentSignal", currentSignal);
        nbt.put("Instructions", Instruction.serializeAll(instructions));
        super.write(nbt, clientPacket);
    }

    @Override
    protected void read(CompoundTag nbt, boolean clientPacket) {
        currentInstruction = nbt.getInt("InstructionIndex");
        poweredPreviously = nbt.getBoolean("PrevPowered");
        currentSignal = nbt.getInt("CurrentSignal");
        ListTag list = nbt.getList("Instructions", Tag.TAG_COMPOUND);
        instructions = Instruction.deserializeAll(list);
        super.read(nbt, clientPacket);
    }
}
