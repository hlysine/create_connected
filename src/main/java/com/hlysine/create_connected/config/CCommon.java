package com.hlysine.create_connected.config;

public class CCommon extends SyncConfigBase {
    private static final String VERSION = "1.0.0";

    @Override
    public String getName() {
        return "common";
    }

    public final ConfigBool migrateCopycatsOnBlockUpdate = b(true, "migrateCopycatsOnBlockUpdate", Comments.migrateCopycatsOnBlockUpdate);

    public final ConfigBool migrateCopycatsOnInitialize = b(true, "migrateCopycatsOnInitialize", Comments.migrateCopycatsOnInitialize);

    public final CFeatures toggle = nested(0, CFeatures::new, Comments.toggle);

    public final CFeatureCategories categories = nested(0, CFeatureCategories::new, Comments.categories);

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

    private static class Comments {
        static String toggle = "Enable/disable features. Values on server override clients";
        static String categories = "Enable/disable categories of features. Disabling a category hides all related features. Values on server override clients";
        static String migrateCopycatsOnBlockUpdate = "Migrate copycats to Create: Copycats+ when they receive a block update";
        static String migrateCopycatsOnInitialize = "Migrate copycats to Create: Copycats+ when their block entities are initialized";
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
