package com.hlysine.create_connected.content.sequencedpulsegenerator;

import com.hlysine.create_connected.content.sequencedpulsegenerator.instructions.Instruction;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

public class ConfigureSequencedPulseGeneratorPacket extends BlockEntityConfigurationPacket<SequencedPulseGeneratorBlockEntity> {

    private ListTag instructions;

    public ConfigureSequencedPulseGeneratorPacket(BlockPos pos, ListTag instructions) {
        super(pos);
        this.instructions = instructions;
    }

    public ConfigureSequencedPulseGeneratorPacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        instructions = buffer.readNbt().getList("data", Tag.TAG_COMPOUND);
    }

    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        CompoundTag tag = new CompoundTag();
        tag.put("data", instructions);
        buffer.writeNbt(tag);
    }

    @Override
    protected void applySettings(SequencedPulseGeneratorBlockEntity be) {
        be.currentInstruction = -1;
        be.instructions = Instruction.deserializeAll(instructions);
        be.sendData();
    }
}
