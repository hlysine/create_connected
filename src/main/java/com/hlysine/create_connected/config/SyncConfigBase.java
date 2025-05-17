package com.hlysine.create_connected.config;

import com.hlysine.create_connected.CreateConnected;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public abstract class SyncConfigBase extends ConfigBase {

    public final CompoundTag getSyncConfig() {
        CompoundTag nbt = new CompoundTag();
        writeSyncConfig(nbt);
        if (children != null)
            for (ConfigBase child : children) {
                if (child instanceof SyncConfigBase syncChild) {
                    if (nbt.contains(child.getName()))
                        throw new RuntimeException("A sync config key starts with " + child.getName() + " but does not belong to the child");
                    nbt.put(child.getName(), syncChild.getSyncConfig());
                }
            }
        return nbt;
    }

    protected void writeSyncConfig(CompoundTag nbt) {
    }

    public final void setSyncConfig(CompoundTag config) {
        if (children != null)
            for (ConfigBase child : children) {
                if (child instanceof SyncConfigBase syncChild) {
                    CompoundTag nbt = config.getCompound(child.getName());
                    syncChild.readSyncConfig(nbt);
                }
            }
        readSyncConfig(config);
    }

    protected void readSyncConfig(CompoundTag nbt) {
    }

    @Override
    public void onLoad() {
        super.onLoad();
        syncToAllPlayers();
    }

    @Override
    public void onReload() {
        super.onReload();
        syncToAllPlayers();
    }

    public void syncToAllPlayers() {
        CreateConnected.LOGGER.debug("Sync Config: Sending server config to all players on reload");
        PacketDistributor.sendToAllPlayers(new SyncConfig(getSyncConfig()));
    }

    private void syncToPlayer(ServerPlayer player) {
        if (player == null) return;
        CreateConnected.LOGGER.debug("Sync Config: Sending server config to {}", player.getScoreboardName());
        PacketDistributor.sendToPlayer(player, new SyncConfig(getSyncConfig()));
    }

    protected void registerAsSyncRoot(final RegisterPayloadHandlersEvent event, final String version) {
        final PayloadRegistrar registrar = event.registrar(version);
        registrar.playBidirectional(
                SyncConfig.TYPE,
                SyncConfig.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        this::handleClientData,
                        this::handleServerData
                )
        );
    }

    public void handleServerData(final SyncConfig data, final IPayloadContext context) {
        // do nothing
    }

    public void handleClientData(final SyncConfig data, final IPayloadContext context) {
        this.setSyncConfig(data.nbt());
        CreateConnected.LOGGER.debug("Sync Config: Received and applied server config {}", data.nbt().toString());
    }

    public record SyncConfig(CompoundTag nbt) implements CustomPacketPayload {
        public static final Type<SyncConfig> TYPE = new Type<>(CreateConnected.asResource("sync_config"));

        public static final StreamCodec<ByteBuf, SyncConfig> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.COMPOUND_TAG,
                SyncConfig::nbt,
                SyncConfig::new
        );

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static abstract class SyncConfigTask implements ICustomConfigurationTask {
        public static final ConfigurationTask.Type TYPE = new Type(CreateConnected.asResource("sync_config_task"));
        private final ServerConfigurationPacketListener listener;

        public SyncConfigTask(ServerConfigurationPacketListener listener) {
            this.listener = listener;
        }

        protected abstract SyncConfigBase getSyncConfig();

        @Override
        public void run(final Consumer<CustomPacketPayload> sender) {
            final SyncConfig payload = new SyncConfig(getSyncConfig().getSyncConfig());
            sender.accept(payload);
            listener.finishCurrentTask(this.type());
        }

        @Override
        public @NotNull Type type() {
            return TYPE;
        }
    }
}
