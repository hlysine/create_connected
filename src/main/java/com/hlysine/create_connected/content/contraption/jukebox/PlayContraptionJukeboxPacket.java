package com.hlysine.create_connected.content.contraption.jukebox;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.network.NetworkEvent;

public class PlayContraptionJukeboxPacket extends SimplePacketBase {

    protected int contraptionId;
    protected BlockPos contraptionPos;
    protected BlockPos worldPos;
    protected int recordId;
    protected boolean play;
    protected ResourceLocation level;

    public PlayContraptionJukeboxPacket(FriendlyByteBuf buffer) {
        level = buffer.readResourceLocation();
        contraptionId = buffer.readInt();
        contraptionPos = buffer.readBlockPos();
        worldPos = buffer.readBlockPos();
        recordId = buffer.readInt();
        play = buffer.readBoolean();
    }

    public PlayContraptionJukeboxPacket(ResourceLocation level, int contraptionId, BlockPos contraptionPos, BlockPos worldPos, int recordId, boolean play) {
        this.level = level;
        this.contraptionId = contraptionId;
        this.contraptionPos = contraptionPos;
        this.worldPos = worldPos;
        this.recordId = recordId;
        this.play = play;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(level);
        buffer.writeInt(contraptionId);
        buffer.writeBlockPos(contraptionPos);
        buffer.writeBlockPos(worldPos);
        buffer.writeInt(recordId);
        buffer.writeBoolean(play);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ClientLevel world = Minecraft.getInstance().level;
            if (world == null || !world.dimension().location().equals(level))
                return;
            if (!world.isLoaded(worldPos))
                return;
            Entity entity = world.getEntity(contraptionId);
            if (!(entity instanceof AbstractContraptionEntity contraptionEntity))
                return;
            if (play) {
                Item item = Item.byId(recordId);
                if (!(item instanceof RecordItem recordItem))
                    return;
                ContraptionMusicManager.playContraptionMusic(
                        recordItem.getSound(),
                        contraptionEntity,
                        contraptionPos,
                        worldPos,
                        recordItem
                );
            } else {
                ContraptionMusicManager.playContraptionMusic(
                        null,
                        contraptionEntity,
                        contraptionPos,
                        worldPos,
                        null
                );
            }
        });
        return true;
    }
}
