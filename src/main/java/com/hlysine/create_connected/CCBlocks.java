package com.hlysine.create_connected;

import com.hlysine.create_connected.content.overstressclutch.OverstressClutchBlock;
import com.hlysine.create_connected.content.parallelgearbox.ParallelGearboxBlock;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.Locale;
import java.util.function.Function;

import static com.simibubi.create.foundation.data.AssetLookup.partialBaseModel;
import static com.simibubi.create.foundation.data.BlockStateGen.axisBlock;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;

public class CCBlocks {
    private static final CreateRegistrate REGISTRATE = CreateConnected.getRegistrate();

    public static final BlockEntry<ParallelGearboxBlock> PARALLEL_GEARBOX = REGISTRATE.block("parallel_gearbox", ParallelGearboxBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .transform(BlockStressDefaults.setNoImpact())
            .transform(axeOrPickaxe())
            .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(AllSpriteShifts.ANDESITE_CASING)))
            .onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, AllSpriteShifts.ANDESITE_CASING,
                    (s, f) -> f.getAxis() == s.getValue(ParallelGearboxBlock.AXIS))))
            .blockstate((c, p) -> axisBlock(c, p, $ -> partialBaseModel(c, p), false))
            .item()
            .transform(customItemModel())
            .register();


    public static final BlockEntry<OverstressClutchBlock> OVERSTRESS_CLUTCH = REGISTRATE.block("overstress_clutch", OverstressClutchBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(BlockStressDefaults.setNoImpact())
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> BlockStateGen.axisBlock(c, p,
                    forBoolean(c, state -> state.getValue(OverstressClutchBlock.STATE) == OverstressClutchBlock.ClutchState.UNCOUPLED, "uncoupled", p)
            ))
            .item()
            .transform(customItemModel())
            .register();

    public static void register() {
    }

    public static Function<BlockState, ModelFile> forBoolean(DataGenContext<?, ?> ctx,
                                                             Function<BlockState, Boolean> condition,
                                                             String key,
                                                             RegistrateBlockstateProvider prov) {
        return state -> condition.apply(state) ? partialBaseModel(ctx, prov, key)
                : partialBaseModel(ctx, prov);
    }
}
