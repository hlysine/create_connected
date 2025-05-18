package com.hlysine.create_connected.datagen;

import com.hlysine.create_connected.content.brassgearbox.BrassGearboxBlock;
import com.hlysine.create_connected.content.linkedtransmitter.LinkedTransmitterBlock;
import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlock;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import org.apache.commons.lang3.function.TriFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.function.Function;

public class CCBlockStateGen {

    public static <B extends Block & LinkedTransmitterBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> linkedButton(ResourceLocation buttonOff, ResourceLocation buttonOn) {
        return (DataGenContext<Block, B> c, RegistrateBlockstateProvider p) -> {
            linkedTransmitter(
                    p, c.get(),
                    state -> state.getValue(BlockStateProperties.POWERED)
                            ? p.models().getExistingFile(buttonOn)
                            : p.models().getExistingFile(buttonOff),
                    state -> state.getValue(BlockStateProperties.POWERED)
                            ? p.models().getExistingFile(p.modLoc("block/linked_transmitter/block_powered" +
                            (state.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL ? "_vertical" : "") +
                            (state.getValue(BlockStateProperties.LOCKED) ? "_locked" : "")))
                            : p.models().getExistingFile(p.modLoc("block/linked_transmitter/block" +
                            (state.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL ? "_vertical" : "") +
                            (state.getValue(BlockStateProperties.LOCKED) ? "_locked" : ""))),
                    state -> state.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL
            );
        };
    }

    public static <B extends Block & LinkedTransmitterBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> linkedLever(ResourceLocation leverOff, ResourceLocation leverOn) {
        return (DataGenContext<Block, B> c, RegistrateBlockstateProvider p) -> {
            linkedTransmitter(
                    p, c.get(),
                    state -> state.getValue(BlockStateProperties.POWERED)
                            ? p.models().getExistingFile(leverOff)
                            : p.models().getExistingFile(leverOn),
                    state -> state.getValue(BlockStateProperties.POWERED)
                            ? p.models().getExistingFile(p.modLoc("block/linked_transmitter/block_powered" +
                            (state.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL ? "_vertical" : "") +
                            (state.getValue(BlockStateProperties.LOCKED) ? "_locked" : "")))
                            : p.models().getExistingFile(p.modLoc("block/linked_transmitter/block" +
                            (state.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL ? "_vertical" : "") +
                            (state.getValue(BlockStateProperties.LOCKED) ? "_locked" : ""))),
                    state -> false
            );
        };
    }

    public static void linkedTransmitter(RegistrateBlockstateProvider prov, Block block, NonNullFunction<BlockState, ModelFile> baseModel, NonNullFunction<BlockState, ModelFile> moduleModel, NonNullFunction<BlockState, Boolean> uvLock) {
        MultiPartBlockStateBuilder builder = prov.getMultipartBuilder(block);

        for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            AttachFace face = state.getValue(BlockStateProperties.ATTACH_FACE);
            boolean powered = state.getValue(BlockStateProperties.POWERED);
            boolean locked = state.getValue(BlockStateProperties.LOCKED);
            int xRot = face == AttachFace.FLOOR ? 0 : (face == AttachFace.WALL ? 90 : 180);
            int yRot = (int) (face == AttachFace.CEILING ? facing : facing.getOpposite()).toYRot();
            if (!locked)
                builder.part()
                        .modelFile(baseModel.apply(state))
                        .rotationX(xRot)
                        .rotationY(yRot)
                        .uvLock(uvLock.apply(state))
                        .addModel()
                        .condition(BlockStateProperties.HORIZONTAL_FACING, facing)
                        .condition(BlockStateProperties.ATTACH_FACE, face)
                        .condition(BlockStateProperties.POWERED, powered)
                        .end();
            builder.part()
                    .modelFile(moduleModel.apply(state))
                    .rotationX(xRot)
                    .rotationY(yRot + (face == AttachFace.FLOOR ? 180 : 0))
                    .uvLock(false)
                    .addModel()
                    .condition(BlockStateProperties.HORIZONTAL_FACING, facing)
                    .condition(BlockStateProperties.ATTACH_FACE, face)
                    .condition(BlockStateProperties.POWERED, powered)
                    .condition(BlockStateProperties.LOCKED, locked)
                    .end();
        }
    }

    public static <B extends SequencedPulseGeneratorBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> sequencedPulseGenerator() {
        return (c, p) -> {
            Map<Boolean, ResourceLocation> baseOff = new HashMap<>();
            baseOff.put(false, p.modLoc("block/" + c.getName() + "_off"));
            baseOff.put(true, p.modLoc("block/" + c.getName() + "_off_reset"));
            Map<Boolean, ResourceLocation> baseOn = new HashMap<>();
            baseOn.put(false, p.modLoc("block/" + c.getName() + "_on"));
            baseOn.put(true, p.modLoc("block/" + c.getName() + "_on_reset"));
            ResourceLocation torchOff = ResourceLocation.withDefaultNamespace("block/redstone_torch_off");
            ResourceLocation torchOn = ResourceLocation.withDefaultNamespace("block/redstone_torch");

            Vector<ModelFile> models = new Vector<>(4);
            for (boolean isPowered : Iterate.falseAndTrue)
                for (boolean isPowering : Iterate.falseAndTrue)
                    for (boolean isSidePowered : Iterate.falseAndTrue)
                        models.add(p.models()
                                .withExistingParent(
                                        c.getName()
                                                + (isPowered ? "_powered" : "")
                                                + (isPowering ? "_powering" : "")
                                                + (isSidePowered ? "_reset" : ""),
                                        p.modLoc("block/" + c.getName())
                                )
                                .texture("1_top", isPowered ? baseOn.get(isSidePowered) : baseOff.get(isSidePowered))
                                .texture("torch", isPowering ? torchOn : torchOff)
                        );
            TriFunction<Boolean, Boolean, Boolean, ModelFile> modelFunc = (f1, f2, f3) -> models.get((f1 ? 4 : 0) + (f2 ? 2 : 0) + (f3 ? 1 : 0));

            p.horizontalBlock(c.get(), state -> modelFunc.apply(
                    state.getValue(SequencedPulseGeneratorBlock.POWERED),
                    state.getValue(SequencedPulseGeneratorBlock.POWERING),
                    state.getValue(SequencedPulseGeneratorBlock.POWERED_SIDE)
            ));
        };
    }

    public static <B extends BrassGearboxBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> brassGearbox() {
        return (c, p) -> {
            ResourceLocation sideTop = p.modLoc("block/" + c.getName() + "_top");
            ResourceLocation sideBottom = p.modLoc("block/" + c.getName() + "_bottom");

            Vector<ModelFile> models = new Vector<>(16);
            for (Direction.Axis axis : Iterate.axes)
                for (boolean isFace1Flipped : Iterate.falseAndTrue)
                    for (boolean isFace2Flipped : Iterate.falseAndTrue)
                        for (boolean isFace3Flipped : Iterate.falseAndTrue)
                            for (boolean isFace4Flipped : Iterate.falseAndTrue)
                                models.add(p.models()
                                        .withExistingParent(
                                                c.getName()
                                                        + "_" + axis.getName()
                                                        + (isFace1Flipped ? "_1" : "")
                                                        + (isFace2Flipped ? "_2" : "")
                                                        + (isFace3Flipped ? "_3" : "")
                                                        + (isFace4Flipped ? "_4" : ""),
                                                p.modLoc("block/brass_gearbox/block_" + axis.getName())
                                        )
                                        .texture("1", isFace1Flipped ? sideTop : sideBottom)
                                        .texture("2", isFace2Flipped ? sideTop : sideBottom)
                                        .texture("3", isFace3Flipped ? sideTop : sideBottom)
                                        .texture("4", isFace4Flipped ? sideTop : sideBottom)
                                );
            Function<BlockState, ModelFile> modelFunc = (state) -> {
                Direction.Axis axis = state.getValue(BrassGearboxBlock.AXIS);
                boolean f1 = state.getValue(BrassGearboxBlock.FACE_1_FLIPPED);
                boolean f2 = state.getValue(BrassGearboxBlock.FACE_2_FLIPPED);
                boolean f3 = state.getValue(BrassGearboxBlock.FACE_3_FLIPPED);
                boolean f4 = state.getValue(BrassGearboxBlock.FACE_4_FLIPPED);
                return models.get(axis.ordinal() * 16 + (f1 ? 8 : 0) + (f2 ? 4 : 0) + (f3 ? 2 : 0) + (f4 ? 1 : 0));
            };

            p.getVariantBuilder(c.getEntry())
                    .forAllStatesExcept(
                            state -> ConfiguredModel.builder()
                                    .modelFile(modelFunc.apply(state))
                                    .uvLock(false)
                                    .build(),
                            BlockStateProperties.WATERLOGGED
                    );
        };
    }

    public static <B extends RotatedPillarKineticBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> axisBlock() {
        return (c, p) -> {
            Vector<ModelFile> models = new Vector<>(16);
            for (Direction.Axis axis : Iterate.axes)
                models.add(p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/block_" + axis.getName())));
            Function<BlockState, ModelFile> modelFunc = (state) -> {
                Direction.Axis axis = state.getValue(BrassGearboxBlock.AXIS);
                return models.get(axis.ordinal());
            };
            p.getVariantBuilder(c.getEntry())
                    .forAllStatesExcept(
                            state -> ConfiguredModel.builder()
                                    .modelFile(modelFunc.apply(state))
                                    .uvLock(false)
                                    .build(),
                            BlockStateProperties.WATERLOGGED
                    );
        };
    }
}
