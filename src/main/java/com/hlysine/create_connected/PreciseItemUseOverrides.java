package com.hlysine.create_connected;

import com.simibubi.create.foundation.block.ItemUseOverrides;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.Set;

public class PreciseItemUseOverrides {

    public static final Set<ResourceLocation> OVERRIDES = new HashSet<>();

    public static void addBlock(Block block) {
        OVERRIDES.add(RegisteredObjects.getKeyOrThrow(block));
        ItemUseOverrides.addBlock(block);
    }
}
