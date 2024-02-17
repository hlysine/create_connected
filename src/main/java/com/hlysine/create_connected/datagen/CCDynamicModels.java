package com.hlysine.create_connected.datagen;

import com.google.gson.JsonElement;
import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.content.linkedtransmitter.LinkedTransmitterBlock;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.Variant;
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

import java.util.*;
import java.util.function.Function;

public class CCDynamicModels {
    private static final BlockModelDefinition.Context modelDefinitionContext = new BlockModelDefinition.Context();
    private static final Set<String> poweredButtonKeywords = Set.of("powered", "pressed", "_on");

    public static <T extends Block & LinkedTransmitterBlock, P, S extends BlockBuilder<T, P>> NonNullUnaryOperator<S> linkedTransmitter(ResourceLocation button) {
        return register((block, blockStates) -> {
                    ResourceLocation blockStateLoc = new ResourceLocation(button.getNamespace(), "blockstates/" + button.getPath() + ".json");
                    List<ModelBakery.LoadedJson> loadedJsons = blockStates.get(blockStateLoc);
                    if (loadedJsons == null || loadedJsons.size() == 0) return noModel();
                    BlockModelDefinition definition = BlockModelDefinition.fromJsonElement(modelDefinitionContext, loadedJsons.get(0).data());
                    Set<ResourceLocation> models = new HashSet<>();
                    for (MultiVariant multiVariant : definition.getMultiVariants()) {
                        for (Variant variant : multiVariant.getVariants()) {
                            models.add(variant.getModelLocation());
                        }
                    }
                    if (definition.getMultiPart() != null)
                        for (MultiVariant multiVariant : definition.getMultiPart().getMultiVariants()) {
                            for (Variant variant : multiVariant.getVariants()) {
                                models.add(variant.getModelLocation());
                            }
                        }
                    CreateConnected.LOGGER.debug("Generating dynamic block states for " + button);
                    for (ResourceLocation model : models) {
                        CreateConnected.LOGGER.debug(model.toString());
                    }
                    if (models.size() != 2) {
                        CreateConnected.LOGGER.warn("Found " + models.size() + " models when generating dynamic block states for " + button + ". Generated model may look weird.");
                    }
                    Optional<ResourceLocation> poweredModel = models.stream()
                            .filter(s -> poweredButtonKeywords.stream().anyMatch(s.getPath()::contains)).findAny();
                    Optional<ResourceLocation> unpoweredModel = models.stream()
                            .filter(s -> poweredButtonKeywords.stream().noneMatch(s.getPath()::contains)).findAny();
                    if (poweredModel.isEmpty() || unpoweredModel.isEmpty()) return noModel();
                    return linkedTransmitter(
                            block,
                            state -> state.getValue(BlockStateProperties.POWERED)
                                    ? existingModel(poweredModel.get())
                                    : existingModel(unpoweredModel.get()),
                            state -> state.getValue(BlockStateProperties.POWERED)
                                    ? existingModel(modLoc("block/linked_transmitter/block_powered" +
                                    (state.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL ? "_vertical" : "") +
                                    (state.getValue(BlockStateProperties.LOCKED) ? "_locked" : "")))
                                    : existingModel(modLoc("block/linked_transmitter/block" +
                                    (state.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL ? "_vertical" : "") +
                                    (state.getValue(BlockStateProperties.LOCKED) ? "_locked" : ""))),
                            state -> state.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL
                    );
                }
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
            JsonElement json = entry.getValue().apply(blockStates);
            if (json == null) {
                CreateConnected.LOGGER.debug("Dynamic block state is null for " + resourceKey);
                continue;
            }
            blockStates.put(
                    resourceKey,
                    Collections.singletonList(new ModelBakery.LoadedJson("mod_resources", json))
            );
        }
    }

    private static <T extends Block & LinkedTransmitterBlock, P, S extends BlockBuilder<T, P>> NonNullUnaryOperator<S> register(DynamicModelGen modelGen) {
        return b -> {
            b.blockstate((c, p) -> {
            });
            b.onRegister(block -> DYNAMIC_MODELS.put(b.get().getId(), blockStates -> modelGen.generate(block, blockStates).toJson()));
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

    private static IGeneratedBlockState noModel() {
        return () -> null;
    }

    @FunctionalInterface
    public interface DynamicModel extends Function<Map<ResourceLocation, List<ModelBakery.LoadedJson>>, JsonElement> {
    }

    @FunctionalInterface
    public interface DynamicModelGen {
        IGeneratedBlockState generate(Block block, Map<ResourceLocation, List<ModelBakery.LoadedJson>> blockStates);
    }
}
