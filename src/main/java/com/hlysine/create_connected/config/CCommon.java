package com.hlysine.create_connected.config;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public class CCommon extends SyncConfigBase {
    private static final String VERSION = "1.0.0";

    @Nullable
    private SyncedValues syncedValues = null;

    @Override
    public String getName() {
        return "common";
    }

    private final ConfigInt schematicsNestingDepth = i(5, 0, 20, "schematicsNestingDepth", Comments.schematicsNestingDepth);

    public int getSchematicsNestingDepth() {
        if (syncedValues != null) return syncedValues.schematicsNestingDepth();
        try {
            return schematicsNestingDepth.get();
        } catch (IllegalStateException $) {
            return 0;
        }
    }

    public final CFeatures toggle = nested(0, CFeatures::new, Comments.toggle);

    public void register() {
        registerAsSyncRoot(
                VERSION,
                SyncConfig.class,
                SyncConfig::encode,
                SyncConfig::new,
                SyncConfig::handle,
                SyncConfig::new
        );
    }

    @Override
    protected void readSyncConfig(CompoundTag nbt) {
        syncedValues = new SyncedValues(nbt.getInt(schematicsNestingDepth.getName()));
    }

    @Override
    protected void writeSyncConfig(CompoundTag nbt) {
        nbt.putInt(schematicsNestingDepth.getName(), schematicsNestingDepth.get());
    }

    private static class Comments {
        static String toggle = "Enable/disable features. Values on server override clients.";
        static String schematicsNestingDepth = "Number of sub-folders allowed for schematics. Value on server overrides clients.";
    }

    private record SyncedValues(int schematicsNestingDepth) {
    }

    private class SyncConfig extends SyncConfigBase.SyncConfig {

        protected SyncConfig(FriendlyByteBuf buf) {
            this(decode(buf));
        }

        protected SyncConfig(CompoundTag nbt) {
            super(nbt);
        }

        @Override
        protected SyncConfigBase configInstance() {
            return CCommon.this;
        }
    }
}
