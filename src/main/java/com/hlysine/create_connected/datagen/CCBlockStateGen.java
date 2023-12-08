package com.hlysine.create_connected.datagen;

import com.hlysine.create_connected.content.brassgearbox.BrassGearboxBlock;
import com.hlysine.create_connected.content.fancatalyst.FanCatalystBlock;
import com.mojang.datafixers.util.Function4;
import com.simibubi.create.foundation.utility.Iterate;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.Vector;
import java.util.function.Function;

import static com.simibubi.create.foundation.data.BlockStateGen.simpleBlock;

public class CCBlockStateGen {

    public static <B extends FanCatalystBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> fanCatalyst() {
        return (c, p) -> {
            ModelFile modelNone = p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/block"));
            ModelFile modelWater = p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/block_water"));
            ModelFile modelLava = p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/block_lava"));
            ModelFile modelFire = p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/block_fire"));
            ModelFile modelSoulFire = p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/block_soul_fire"));
            Function<FanCatalystBlock.CatalystContent, ModelFile> modelFunc = (content) -> switch (content) {
                case NONE -> modelNone;
                case WATER -> modelWater;
                case LAVA -> modelLava;
                case FIRE -> modelFire;
                case SOUL_FIRE -> modelSoulFire;
            };

            simpleBlock(c, p, state -> modelFunc.apply(state.getValue(FanCatalystBlock.CONTENT)));
        };
    }

    public static <B extends BrassGearboxBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> brassGearbox() {
        return (c, p) -> {
            ResourceLocation sideTop = p.modLoc("block/" + c.getName() + "_top");
            ResourceLocation sideBottom = p.modLoc("block/" + c.getName() + "_bottom");

            Vector<ModelFile> models = new Vector<>(16);
            for (boolean isFace1Flipped : Iterate.falseAndTrue)
                for (boolean isFace2Flipped : Iterate.falseAndTrue)
                    for (boolean isFace3Flipped : Iterate.falseAndTrue)
                        for (boolean isFace4Flipped : Iterate.falseAndTrue)
                            models.add(p.models()
                                    .withExistingParent(
                                            c.getName()
                                                    + (isFace1Flipped ? "_1" : "")
                                                    + (isFace2Flipped ? "_2" : "")
                                                    + (isFace3Flipped ? "_3" : "")
                                                    + (isFace4Flipped ? "_4" : ""),
                                            p.modLoc("block/brass_gearbox/block")
                                    )
                                    .texture("1", isFace1Flipped ? sideTop : sideBottom)
                                    .texture("2", isFace2Flipped ? sideTop : sideBottom)
                                    .texture("3", isFace3Flipped ? sideTop : sideBottom)
                                    .texture("4", isFace4Flipped ? sideTop : sideBottom)
                            );
            Function4<Boolean, Boolean, Boolean, Boolean, ModelFile> modelFunc = (f1, f2, f3, f4) -> models.get((f1 ? 8 : 0) + (f2 ? 4 : 0) + (f3 ? 2 : 0) + (f4 ? 1 : 0));

            noZRotationAxisBlock(c, p, state -> modelFunc.apply(
                    state.getValue(BrassGearboxBlock.FACE_1_FLIPPED),
                    state.getValue(BrassGearboxBlock.FACE_2_FLIPPED),
                    state.getValue(BrassGearboxBlock.FACE_3_FLIPPED),
                    state.getValue(BrassGearboxBlock.FACE_4_FLIPPED)
            ));
        };
    }

    public static <T extends Block> void noZRotationAxisBlock(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                              Function<BlockState, ModelFile> modelFunc) {
        noZRotationAxisBlock(ctx, prov, modelFunc, false);
    }

    public static <T extends Block> void noZRotationAxisBlock(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                              Function<BlockState, ModelFile> modelFunc, boolean uvLock) {
        prov.getVariantBuilder(ctx.getEntry())
                .forAllStatesExcept(state -> {
                    Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
                    return ConfiguredModel.builder()
                            .modelFile(modelFunc.apply(state))
                            .uvLock(uvLock)
                            .rotationX(axis == Direction.Axis.Y ? 0 : 90)
                            .rotationY(axis == Direction.Axis.X ? 90 : 0)
                            .build();
                }, BlockStateProperties.WATERLOGGED);
    }
}
