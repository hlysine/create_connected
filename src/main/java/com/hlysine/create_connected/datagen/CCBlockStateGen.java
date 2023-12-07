package com.hlysine.create_connected.datagen;

import com.hlysine.create_connected.content.brassgearbox.BrassGearboxBlock;
import com.mojang.datafixers.util.Function4;
import com.simibubi.create.foundation.utility.Iterate;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.Vector;

import static com.simibubi.create.foundation.data.BlockStateGen.axisBlock;

public class CCBlockStateGen {

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

            axisBlock(c, p, state -> modelFunc.apply(
                    state.getValue(BrassGearboxBlock.FACE_1_FLIPPED),
                    state.getValue(BrassGearboxBlock.FACE_2_FLIPPED),
                    state.getValue(BrassGearboxBlock.FACE_3_FLIPPED),
                    state.getValue(BrassGearboxBlock.FACE_4_FLIPPED)
            ));
        };
    }
}