package com.hlysine.create_connected;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;

public class CCSpriteShifts {

    public static final Map<WoodType, CTSpriteShiftEntry> WOODEN_WINDOWS = new IdentityHashMap<>();

    static {
        WoodType[] supportedWoodTypes = new WoodType[]{WoodType.CHERRY};
        Arrays.stream(supportedWoodTypes)
                .forEach(woodType -> WOODEN_WINDOWS.put(woodType, getCT(AllCTTypes.VERTICAL, woodType.name() + "_window", woodType.name() + "_window")));
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(type, CreateConnected.asResource("block/" + blockTextureName),
                CreateConnected.asResource("block/" + connectedTextureName + "_connected"));
    }
}
