package com.hlysine.create_connected.config;

import com.simibubi.create.foundation.config.ConfigBase;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCConfigs {

    public static void register(ModLoadingContext context) {
        CCConfigs.registerCommon();

        for (Map.Entry<ModConfig.Type, ConfigBase> pair : CCConfigs.CONFIGS.entrySet())
            context.registerConfig(pair.getKey(), pair.getValue().specification);
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        for (ConfigBase config : CCConfigs.CONFIGS.values())
            if (config.specification == event.getConfig()
                    .getSpec())
                config.onLoad();
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        for (ConfigBase config : CCConfigs.CONFIGS.values())
            if (config.specification == event.getConfig()
                    .getSpec())
                config.onReload();
    }

    @ApiStatus.Internal
    public static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);
    private static CServer server;

    public static CServer server() {
        return server;
    }

    public static ConfigBase byType(ModConfig.Type type) {
        return CONFIGS.get(type);
    }

    private static <T extends ConfigBase> T register(Supplier<T> factory, ModConfig.Type side) {
        Pair<T, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(builder -> {
            T config = factory.get();
            config.registerAll(builder);
            return config;
        });

        T config = specPair.getLeft();
        config.specification = specPair.getRight();
        CONFIGS.put(side, config);
        return config;
    }

    @ApiStatus.Internal
    public static void registerCommon() {
        server = register(CServer::new, ModConfig.Type.SERVER);
    }

    public static void onLoad(ModConfig modConfig) {
        for (ConfigBase config : CONFIGS.values())
            if (config.specification == modConfig
                    .getSpec())
                config.onLoad();
    }

    public static void onReload(ModConfig modConfig) {
        for (ConfigBase config : CONFIGS.values())
            if (config.specification == modConfig
                    .getSpec())
                config.onReload();
    }

    private static class TomlGroup {
        private final Map<String, TomlGroup> subgroups = new HashMap<>();
        private final Map<String, String> entries = new HashMap<>();
        private final String path;

        private static TomlGroup root() {
            return new TomlGroup("");
        }

        private TomlGroup(String path) {
            this.path = path;
        }

        public boolean isRoot() {
            return path.isEmpty();
        }

        public void add(String key, String value) {
            if (!isRoot())
                throw new NotImplementedException();

            String[] pieces = key.split("\\.");
            String subKey = pieces[pieces.length - 1];
            TomlGroup targetedGroup = this;
            for (int i = 0; i < pieces.length - 1; i++) {
                targetedGroup = targetedGroup.getOrCreateSubGroup(pieces[i]);
            }
            targetedGroup.entries.put(subKey, value);
        }

        private TomlGroup getOrCreateSubGroup(String subKey) {
            return subgroups.computeIfAbsent(subKey, (sk) -> new TomlGroup(path.isEmpty() ? sk : path + "." + sk));
        }

        private void write(StringBuilder b) {
            if (!isRoot()) {
                b.append("\n[").append(path).append("]");
            }

            for (Map.Entry<String, String> entry : entries.entrySet()) {
                b.append("\n").append(entry.getKey()).append(" = ").append(entry.getValue());
            }

            for (TomlGroup subGroup : subgroups.values()) {
                subGroup.write(b);
            }
        }

        private String write() {
            StringBuilder b = new StringBuilder();
            b.append("# Automatically written by a converter");
            write(b);
            b.append("\n");
            return b.toString();
        }
    }
}
