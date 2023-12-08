package com.hlysine.create_connected;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.BiomeColors;

public class CCColorHandlers {

    public static BlockColor waterBlockTint() {
        return (state, level, pos, tintIndex) ->
                level != null && pos != null ? BiomeColors.getAverageWaterColor(level, pos) : -1;
    }

    public static ItemColor waterItemTint() {
        return (stack, tintIndex) -> 4159204; // default water color
    }
}
