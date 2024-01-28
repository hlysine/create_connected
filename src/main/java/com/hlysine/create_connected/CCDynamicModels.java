package com.hlysine.create_connected;

import com.google.gson.JsonElement;
import com.hlysine.create_connected.content.linkedtransmitter.LinkedTransmitterBlock;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.IGeneratedBlockState;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CCDynamicModels {

    public static <T extends Block & LinkedTransmitterBlock, P, S extends BlockBuilder<T, P>> NonNullUnaryOperator<S> linkedTransmitter(ResourceLocation buttonOff, ResourceLocation buttonOn) {
        return register(block ->
                linkedTransmitter(
                        block,
                        state -> state.getValue(BlockStateProperties.POWERED)
                                ? existingModel(buttonOn)
                                : existingModel(buttonOff),
                        state -> state.getValue(BlockStateProperties.POWERED)
                                ? existingModel(modLoc("block/linked_transmitter/block_powered" +
                                (state.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL ? "_vertical" : "") +
                                (state.getValue(BlockStateProperties.LOCKED) ? "_locked" : "")))
                                : existingModel(modLoc("block/linked_transmitter/block" +
                                (state.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL ? "_vertical" : "") +
                                (state.getValue(BlockStateProperties.LOCKED) ? "_locked" : ""))),
                        state -> state.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL
                )
        );
    }

    private static IGeneratedBlockState linkedTransmitter(Block block, NonNullFunction<BlockState, ModelFile> baseModel, NonNullFunction<BlockState, ModelFile> moduleModel, NonNullFunction<BlockState, Boolean> uvLock) {
        MultiPartBlockStateBuilder builder = multiPartBuilder(block);

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

        return builder;
    }

    private static final Map<ResourceLocation, DynamicModel> DYNAMIC_MODELS = new HashMap<>();

    public static void onLoadBlockStates(Map<ResourceLocation, List<ModelBakery.LoadedJson>> blockStates) {
        for (Map.Entry<ResourceLocation, DynamicModel> entry : DYNAMIC_MODELS.entrySet()) {
            ResourceLocation resourceKey = new ResourceLocation(entry.getKey().getNamespace(), "blockstates/" + entry.getKey().getPath() + ".json");
            if (blockStates.containsKey(resourceKey)) {
                CreateConnected.LOGGER.debug("Existing block state found for " + resourceKey);
                continue;
            }
            CreateConnected.LOGGER.debug("Adding dynamic block state for " + resourceKey);
            blockStates.put(
                    resourceKey,
                    Collections.singletonList(new ModelBakery.LoadedJson("mod_resources", entry.getValue().get()))
            );
        }
    }

    private static <T extends Block & LinkedTransmitterBlock, P, S extends BlockBuilder<T, P>> NonNullUnaryOperator<S> register(DynamicModelGen modelGen) {
        return b -> {
            b.blockstate((c, p) -> {
            });
            b.onRegister(block -> DYNAMIC_MODELS.put(b.get().getId(), () -> modelGen.generate(block).toJson()));
            return b;
        };
    }

    private static ModelFile.UncheckedModelFile existingModel(ResourceLocation location) {
        return new ModelFile.UncheckedModelFile(location);
    }

    private static MultiPartBlockStateBuilder multiPartBuilder(Block block) {
        return new MultiPartBlockStateBuilder(block);
    }

    private static ResourceLocation modLoc(String path) {
        return CreateConnected.asResource(path);
    }

    @FunctionalInterface
    public interface DynamicModel extends Supplier<JsonElement> {
    }

    @FunctionalInterface
    public interface DynamicModelGen {
        IGeneratedBlockState generate(Block block);
    }
}
