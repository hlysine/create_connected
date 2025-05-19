package com.hlysine.create_connected;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class CCColorHandlers {

    public static BlockColor waterBlockTint() {
        return (state, level, pos, tintIndex) ->
                level != null && pos != null ? BiomeColors.getAverageWaterColor(level, pos) : -1;
    }

    private static final FluidStack waterStack = new FluidStack(Fluids.WATER, 1);

    public static ItemColor waterItemTint() {
        return (stack, tintIndex) -> IClientFluidTypeExtensions.of(Fluids.WATER).getTintColor(waterStack); // default water color
    }
}
