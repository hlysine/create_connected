package com.hlysine.create_connected.content;

import com.simibubi.create.foundation.utility.Couple;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class BlockStressDefaults {

    public static final Map<ResourceLocation, Double> DEFAULT_IMPACTS = new ConcurrentHashMap<>();
    public static final Map<ResourceLocation, Double> DEFAULT_CAPACITIES = new ConcurrentHashMap<>();
    public static final Map<ResourceLocation, Supplier<Couple<Integer>>> GENERATOR_SPEEDS = new ConcurrentHashMap<>();

    public static void setDefaultImpact(ResourceLocation blockId, double impact) {
        DEFAULT_IMPACTS.put(blockId, impact);
        com.simibubi.create.content.kinetics.BlockStressDefaults.setDefaultImpact(blockId, impact);
    }

    public static void setDefaultCapacity(ResourceLocation blockId, double capacity) {
        DEFAULT_CAPACITIES.put(blockId, capacity);
        com.simibubi.create.content.kinetics.BlockStressDefaults.setDefaultCapacity(blockId, capacity);
    }

    public static void setGeneratorSpeed(ResourceLocation blockId, Supplier<Couple<Integer>> provider) {
        GENERATOR_SPEEDS.put(blockId, provider);
        com.simibubi.create.content.kinetics.BlockStressDefaults.setGeneratorSpeed(blockId, provider);
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setNoImpact() {
        return setImpact(0);
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setImpact(double impact) {
        return b -> {
            setDefaultImpact(new ResourceLocation(b.getOwner()
                    .getModid(), b.getName()), impact);
            return b;
        };
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setCapacity(double capacity) {
        return b -> {
            setDefaultCapacity(new ResourceLocation(b.getOwner()
                    .getModid(), b.getName()), capacity);
            return b;
        };
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setGeneratorSpeed(
            Supplier<Couple<Integer>> provider) {
        return b -> {
            setGeneratorSpeed(new ResourceLocation(b.getOwner()
                    .getModid(), b.getName()), provider);
            return b;
        };
    }

}

