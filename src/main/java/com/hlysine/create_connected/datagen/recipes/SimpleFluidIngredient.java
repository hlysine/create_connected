package com.hlysine.create_connected.datagen.recipes;

import com.google.common.hash.HashCode;
import com.hlysine.create_connected.compat.Mods;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.mixin.accessor.MappedRegistryAccessor;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

public class SimpleFluidIngredient extends FluidIngredient {

	/*
	"ingredients": [
		{
            "type": "neoforge:single",
            "fluid": "create_shimmer:shimmer"
		}
	]
	 */

    private static final MapCodec<SimpleFluidIngredient> INTERNAL_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("fluid").forGetter(i -> i.mod.rl(i.id))
    ).apply(instance, (fluid) -> {
        for (Mods mod : Mods.values()) {
            if (mod.id().equals(fluid.getNamespace())) {
                return new SimpleFluidIngredient(mod, fluid.getPath());
            }
        }
        throw new AssertionError("ID " + fluid.getNamespace() + " doesn't correspond to any compat mod." +
                " SimpleFluidIngredient is not meant for deserialization anyway");
    }));

    private static final MapCodec<SimpleFluidIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            INTERNAL_CODEC.codec().listOf().fieldOf("ingredients").forGetter(List::of)
    ).apply(instance, list -> {
        assert list.size() == 1 : "SimpleFluidIngredient should only be serialized as a single-element list, and shouldn't be deserialized anyway";
        return list.getFirst();
    }));
    private static final FluidIngredientType<?> INGREDIENT_TYPE = new FluidIngredientType<>(CODEC);

    private final Mods mod;
    private final String id;

    public SimpleFluidIngredient(Mods mod, String id) {
        this.mod = mod;
        this.id = id;
    }

    @Override
    public boolean test(@NotNull FluidStack stack) {
        return stack.getFluidHolder().getKey().location().equals(mod.rl(id));
    }

    @Override
    public @NotNull Stream<FluidStack> generateStacks() {
        return Stream.empty();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    private static boolean didRegistryInjection = false;

    @Override
    public @NotNull FluidIngredientType<?> getType() {
        if (!didRegistryInjection) {
            // Need to do some registry injection to get the Registry#byNameCodec to encode the right type for this
            // getResourceKey and getId
            // byValue and toId
            // Holder.Reference: key
            if (NeoForgeRegistries.FLUID_INGREDIENT_TYPES instanceof MappedRegistryAccessor<?> mra) {
                @SuppressWarnings("unchecked")
                MappedRegistryAccessor<FluidIngredientType<?>> mra$ = (MappedRegistryAccessor<FluidIngredientType<?>>) mra;

                IngredientType<?> baseType = NeoForgeMod.COMPOUND_INGREDIENT_TYPE.get();

                int wrappedId = mra$.getToId().getOrDefault(baseType, -1);
                ResourceKey<FluidIngredientType<?>> wrappedKey = NeoForgeMod.COMPOUND_FLUID_INGREDIENT_TYPE.getKey();

                mra$.getToId().put(INGREDIENT_TYPE, wrappedId);
                //noinspection DataFlowIssue - it is ok to pass null as the owner, because this is only being used for serialization
                mra$.getByValue().put(INGREDIENT_TYPE, Holder.Reference.createStandAlone(null, wrappedKey));

				/*
				{
					"type": "neoforge:compound",
					"ingredients": [
						{
							"fluid": "mod:compat_item"
						}

					]
				}
				 */

                didRegistryInjection = true;
            } else {
                throw new AssertionError("SimpleFluidIngredient will not be able to" +
                        " serialize without injecting into a registry. Expected" +
                        " NeoForgeRegistries.FLUID_INGREDIENT_TYPES to be of class MappedRegistry, is of class " +
                        NeoForgeRegistries.INGREDIENT_TYPES.getClass()
                );
            }
        }
        return INGREDIENT_TYPE;
    }

    public int hashCode() {
        return HashCode.fromString(mod.id() + ":" + id).asInt();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            if (obj instanceof SimpleFluidIngredient other) {
                return other.id.equals(this.id) && other.mod == this.mod;
            }
            return false;
        }
    }
}

