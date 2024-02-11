package com.hlysine.create_connected.compat;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.CCItems;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.HashMap;
import java.util.Map;

public class CopycatsManager {
    public static Map<String, BlockEntry<?>> BLOCK_MAP = new HashMap<>();
    public static Map<String, ItemEntry<?>> ITEM_MAP = new HashMap<>();

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
        BlockEntry<?> result = BLOCK_MAP.get(RegisteredObjects.getKeyOrThrow(self).getPath());
        if (result != null) return result.get();
        return self;
    }

    public static Item convert(Item self) {
        ItemEntry<?> result = ITEM_MAP.get(RegisteredObjects.getKeyOrThrow(self).getPath());
        if (result != null) return result.get();
        BlockEntry<?> blockResult = BLOCK_MAP.get(RegisteredObjects.getKeyOrThrow(self).getPath());
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
        if (isFeatureEnabled(RegisteredObjects.getKeyOrThrow(block)))
            return convert(block);
        return block;
    }

    public static BlockState convertIfEnabled(BlockState state) {
        if (isFeatureEnabled(RegisteredObjects.getKeyOrThrow(state.getBlock())))
            return convert(state);
        return state;
    }

    public static ItemLike convertIfEnabled(ItemLike item) {
        if (isFeatureEnabled(RegisteredObjects.getKeyOrThrow(item.asItem())))
            return convert(item);
        return item;
    }

    public static boolean existsInCopycats(ResourceLocation key) {
        if (BLOCK_MAP.containsKey(key.getPath())) return true;
        if (ITEM_MAP.containsKey(key.getPath())) return true;
        return false;
    }

    public static boolean isFeatureEnabled(ResourceLocation key) {
        if (!existsInCopycats(key))
            return false;
        return com.copycatsplus.copycats.config.FeatureToggle.isEnabled(Mods.COPYCATS.rl(key.getPath()));
    }
}
