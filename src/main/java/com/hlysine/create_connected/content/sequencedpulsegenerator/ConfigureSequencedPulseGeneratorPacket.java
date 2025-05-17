package com.hlysine.create_connected.content.sequencedpulsegenerator;

import com.hlysine.create_connected.CCPackets;
import com.hlysine.create_connected.content.sequencedpulsegenerator.instructions.Instruction;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorConnectionPacket;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ConfigureSequencedPulseGeneratorPacket extends BlockEntityConfigurationPacket<SequencedPulseGeneratorBlockEntity> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigureSequencedPulseGeneratorPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.pos,
            ByteBufCodecs.TAG, packet -> packet.instructions,
            ConfigureSequencedPulseGeneratorPacket::new
    );

    private final ListTag instructions;

    public ConfigureSequencedPulseGeneratorPacket(BlockPos pos, Tag instructions) {
        super(pos);
        this.instructions = (ListTag) instructions;
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CCPackets.CONFIGURE_SEQUENCER;
    }

    @Override
    protected int maxRange() {
        return 16;
    }

    @Override
    protected void applySettings(ServerPlayer player, SequencedPulseGeneratorBlockEntity be) {
        be.currentInstruction = -1;
        be.instructions = Instruction.deserializeAll(instructions);
        be.sendData();
    }
}
