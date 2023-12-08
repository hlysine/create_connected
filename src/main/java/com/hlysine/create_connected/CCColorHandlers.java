package com.hlysine.create_connected;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.BiomeColors;

public class CCColorHandlers {

    public static BlockColor waterTint() {
        return (state, level, pos, tintIndex) ->
                level != null && pos != null ? BiomeColors.getAverageWaterColor(level, pos) : -1;
    }
}
