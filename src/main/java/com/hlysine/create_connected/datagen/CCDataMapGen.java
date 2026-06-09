package com.hlysine.create_connected.datagen;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.compat.Mods;
import com.hlysine.create_connected.registries.CCBlocks;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateDataMapProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import plus.dragons.createdragonsplus.common.registry.CDPDataMaps;

public class CCDataMapGen {
    public static void addGenerators() {
        CreateConnected.getRegistrate().addDataGenerator(ProviderType.DATA_MAP, CCDataMapGen::genDataMaps);
    }

    private static void genDataMaps(RegistrateDataMapProvider prov) {
        CCBlocks.FAN_DYEING_CATALYSTS.forEach((color, block) -> {
            prov.builder(CDPDataMaps.BLOCK_FAN_COLORING_CATALYSTS)
                    .add(
                            block.getId(),
                            ResourceLocation.withDefaultNamespace(color.getSerializedName()),
                            false,
                            new ModLoadedCondition(Mods.DRAGONS_PLUS.id())
                    );
        });
    }
}
