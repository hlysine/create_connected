package com.hlysine.create_connected.content.sequencedpulsegenerator;

import com.hlysine.create_connected.content.sequencedpulsegenerator.instructions.EndInstruction;
import com.hlysine.create_connected.content.sequencedpulsegenerator.instructions.Instruction;
import com.hlysine.create_connected.content.sequencedpulsegenerator.instructions.InstructionResult;
import com.hlysine.create_connected.content.sequencedpulsegenerator.instructions.TimeInstruction;
import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Vector;

import static com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlock.POWERING;
import static net.minecraft.world.level.block.DiodeBlock.POWERED;

public class SequencedPulseGeneratorBlockEntity extends SmartBlockEntity implements ClipboardCloneable {

    public static final int INSTRUCTION_CAPACITY = 5;

    static {
        Instruction.register(new TimeInstruction(10, 15));
        Instruction.register(new EndInstruction());
    }

    Vector<Instruction> instructions;
    int currentInstruction;
    int currentSignal;
    boolean poweredPreviously;

    public SequencedPulseGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        instructions = Instruction.createDefault();
        currentInstruction = -1;
        currentSignal = 0;
        poweredPreviously = false;
    }

    public boolean isIdle() {
        return currentInstruction < 0;
    }

    public int getCurrentSignal() {
        return currentSignal;
    }

    public Instruction getCurrentInstruction() {
        return currentInstruction >= 0 && currentInstruction < instructions.size() ? instructions.get(currentInstruction)
                : null;
    }

    private void handleResult(Instruction instruction, InstructionResult result) {
        int prevSignal = currentSignal;
        currentSignal = instruction.getSignal();
        if (prevSignal != currentSignal) {
            level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(POWERING, currentSignal > 0));
        }
        currentInstruction = result.getNextInstruction(currentInstruction);
    }

    @Override
    public void tick() {
        super.tick();

        if (isIdle())
            return;
        if (level.isClientSide)
            return;
        Instruction instruction = getCurrentInstruction();
        if (instruction == null) {
            currentInstruction = -1;
            return;
        }
        InstructionResult result = instruction.tick(this);
        handleResult(instruction, result);
    }

    public void onRedstoneUpdate(boolean isPowered) {
        if (isPowered == poweredPreviously) return;
        if (!poweredPreviously && isPowered && !isIdle())
            risingEdge();
        if (poweredPreviously && !isPowered && !isIdle())
            fallingEdge();
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
        risingEdge();
    }

    public void risingEdge() {
        Instruction instruction = getCurrentInstruction();
        if (instruction == null) {
            currentInstruction = -1;
            return;
        }
        InstructionResult result = instruction.onRisingEdge(this);
        handleResult(instruction, result);
    }

    public void fallingEdge() {
        Instruction instruction = getCurrentInstruction();
        if (instruction == null) {
            currentInstruction = -1;
            return;
        }
        InstructionResult result = instruction.onFallingEdge(this);
        handleResult(instruction, result);
    }

    @Override
    protected void write(CompoundTag nbt, boolean clientPacket) {
        nbt.putInt("InstructionIndex", currentInstruction);
        nbt.putBoolean("PrevPowered", poweredPreviously);
        nbt.putInt("CurrentSignal", currentSignal);
        Instruction instruction = getCurrentInstruction();
        if (instruction != null) {
            CompoundTag state = new CompoundTag();
            instruction.writeState(state);
            nbt.put("InstructionState", state);
        }
        ListTag list = new ListTag();
        instructions.forEach(i -> list.add(i.serializeParams()));
        nbt.put("Instructions", list);
        super.write(nbt, clientPacket);
    }

    @Override
    protected void read(CompoundTag nbt, boolean clientPacket) {
        currentInstruction = nbt.getInt("InstructionIndex");
        poweredPreviously = nbt.getBoolean("PrevPowered");
        currentSignal = nbt.getInt("CurrentSignal");
        ListTag list = nbt.getList("Instructions", Tag.TAG_COMPOUND);
        instructions = Instruction.deserializeAll(list);
        Instruction current = getCurrentInstruction();
        if (current != null) {
            current.readState(nbt.getCompound("InstructionState"));
        }
        super.read(nbt, clientPacket);
    }

    @Override
    public String getClipboardKey() {
        return "Block";
    }

    @Override
    public boolean writeToClipboard(CompoundTag tag, Direction side) {
        return true;  // TODO
    }

    @Override
    public boolean readFromClipboard(CompoundTag tag, Player player, Direction side, boolean simulate) {
        return true; // TODO
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }
}
