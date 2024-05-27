package com.hlysine.create_connected.compat;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.CCItems;
import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.config.CCConfigs;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.*;

public class CopycatsManager {
    public static Map<String, BlockEntry<?>> BLOCK_MAP = new HashMap<>();
    public static Map<String, ItemEntry<?>> ITEM_MAP = new HashMap<>();

    public static final Map<Level, Set<BlockPos>> migrationQueue = Collections.synchronizedMap(new WeakHashMap<>());

    static {
        BLOCK_MAP.put(CCBlocks.COPYCAT_BLOCK.getKey().location().getPath(), com.copycatsplus.copycats.CCBlocks.COPYCAT_BLOCK);
        BLOCK_MAP.put(CCBlocks.COPYCAT_SLAB.getKey().location().getPath(), com.copycatsplus.copycats.CCBlocks.COPYCAT_SLAB);
        BLOCK_MAP.put(CCBlocks.COPYCAT_BEAM.getKey().location().getPath(), com.copycatsplus.copycats.CCBlocks.COPYCAT_BEAM);
        BLOCK_MAP.put(CCBlocks.COPYCAT_VERTICAL_STEP.getKey().location().getPath(), com.copycatsplus.copycats.CCBlocks.COPYCAT_VERTICAL_STEP);
        BLOCK_MAP.put(CCBlocks.COPYCAT_STAIRS.getKey().location().getPath(), com.copycatsplus.copycats.CCBlocks.COPYCAT_STAIRS);
        BLOCK_MAP.put(CCBlocks.COPYCAT_FENCE.getKey().location().getPath(), com.copycatsplus.copycats.CCBlocks.COPYCAT_FENCE);
        BLOCK_MAP.put(CCBlocks.COPYCAT_FENCE_GATE.getKey().location().getPath(), com.copycatsplus.copycats.CCBlocks.COPYCAT_FENCE_GATE);
        BLOCK_MAP.put(CCBlocks.COPYCAT_WALL.getKey().location().getPath(), com.copycatsplus.copycats.CCBlocks.COPYCAT_WALL);
        BLOCK_MAP.put(CCBlocks.COPYCAT_BOARD.getKey().location().getPath(), com.copycatsplus.copycats.CCBlocks.COPYCAT_BOARD);
        ITEM_MAP.put(CCItems.COPYCAT_BOX.getKey().location().getPath(), com.copycatsplus.copycats.CCItems.COPYCAT_BOX);
        ITEM_MAP.put(CCItems.COPYCAT_CATWALK.getKey().location().getPath(), com.copycatsplus.copycats.CCItems.COPYCAT_CATWALK);
    }

    public static Block convert(Block self) {
        ResourceLocation key = RegisteredObjects.getKeyOrThrow(self);
        if (!validateNamespace(key)) return self;
        BlockEntry<?> result = BLOCK_MAP.get(key.getPath());
        if (result != null) return result.get();
        return self;
    }

    public static Item convert(Item self) {
        ResourceLocation key = RegisteredObjects.getKeyOrThrow(self);
        if (!validateNamespace(key)) return self;
        ItemEntry<?> result = ITEM_MAP.get(key.getPath());
        if (result != null) return result.get();
        BlockEntry<?> blockResult = BLOCK_MAP.get(key.getPath());
        if (blockResult != null) return blockResult.asItem();
        return self;
    }

    public static ItemLike convert(ItemLike self) {
        return convert(self.asItem());
    }

    public static BlockState convert(BlockState state) {
        Block converted = convert(state.getBlock());
        if (state.getBlock() == converted) return state;
        BlockState newState = converted.defaultBlockState();
        for (Property<?> property : state.getProperties()) {
            newState = copyProperty(state, newState, property);
        }
        return newState;
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState from, BlockState to, Property<T> property) {
        return to.setValue(property, from.getValue(property));
    }

    public static Block convertIfEnabled(Block block) {
        ResourceLocation key = RegisteredObjects.getKeyOrThrow(block);
        if (!validateNamespace(key)) return block;
        if (isFeatureEnabled(key))
            return convert(block);
        return block;
    }

    public static BlockState convertIfEnabled(BlockState state) {
        ResourceLocation key = RegisteredObjects.getKeyOrThrow(state.getBlock());
        if (!validateNamespace(key)) return state;
        if (isFeatureEnabled(key))
            return convert(state);
        return state;
    }

    public static ItemLike convertIfEnabled(ItemLike item) {
        ResourceLocation key = RegisteredObjects.getKeyOrThrow(item.asItem());
        if (!validateNamespace(key)) return item;
        if (isFeatureEnabled(key))
            return convert(item);
        return item;
    }

    private static boolean validateNamespace(ResourceLocation key) {
        return key.getNamespace().equals(CreateConnected.MODID) || key.getNamespace().equals(Mods.COPYCATS.id());
    }

    public static boolean existsInCopycats(ResourceLocation key) {
        if (!validateNamespace(key)) return false;
        if (BLOCK_MAP.containsKey(key.getPath())) return true;
        if (ITEM_MAP.containsKey(key.getPath())) return true;
        return false;
    }

    public static boolean isFeatureEnabled(ResourceLocation key) {
        if (!existsInCopycats(key))
            return false;
        return com.copycatsplus.copycats.config.FeatureToggle.isEnabled(Mods.COPYCATS.rl(key.getPath()));
    }

    public static void enqueueMigration(Level level, BlockPos pos) {
        migrationQueue
                .computeIfAbsent(level, $ -> Collections.synchronizedSet(new LinkedHashSet<>()))
                .add(pos);
    }

    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.haveTime() && event.side == LogicalSide.SERVER) {
            if (!CCConfigs.common().migrateCopycatsOnInitialize.get()) {
                migrationQueue.clear();
                return;
            }
            Level level = event.level;
            synchronized (migrationQueue) {
                if (migrationQueue.containsKey(level)) {
                    Set<BlockPos> list = migrationQueue.get(level);
                    synchronized (list) {
                        if (list.size() > 0)
                            CreateConnected.LOGGER.debug("Copycats: Migrated " + list.size() + " copycats in " + level.dimension().location());
                        for (Iterator<BlockPos> iterator = list.iterator(); iterator.hasNext(); ) {
                            BlockPos pos = iterator.next();
                            if (!level.isLoaded(pos)) {
                                iterator.remove();
                                continue;
                            }
                            BlockState state = level.getBlockState(pos);
                            BlockState converted = CopycatsManager.convert(state);
                            if (!converted.is(state.getBlock())) {
                                level.setBlock(pos, converted, 2 | 16 | 32);
                            }
                            iterator.remove();
                        }
                    }
                }
            }
        }
    }
}
