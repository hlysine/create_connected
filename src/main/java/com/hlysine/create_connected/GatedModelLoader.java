package com.hlysine.create_connected;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class GatedModelLoader implements IGeometryLoader<GatedModelLoader.BlockGeometry> {
    public static final GatedModelLoader INSTANCE = new GatedModelLoader();
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(CreateConnected.MODID, "gated_model_loader");

    private GatedModelLoader() {}

    @Override
    public @NotNull BlockGeometry read(
            JsonObject jsonObject,
            @NotNull JsonDeserializationContext jsonDeserializationContext
    ) throws JsonParseException {
        JsonElement requiredId = jsonObject.get("required_modid");
        String modId = null;
        BlockModel model;

        try {
            modId = requiredId.getAsString();
        } catch (NullPointerException | JsonParseException | IllegalStateException ignored) {}

        if (modId == null || ModList.get().isLoaded(modId)) {
            BlockModel.Deserializer blockReader = new BlockModel.Deserializer();
            model = blockReader.deserialize(jsonObject, BlockModel.class, jsonDeserializationContext);
        } else {
            // fall back to empty model. same as air model.
            model = new BlockModel(
                    null,
                    List.of(),
                    Maps.newHashMap(),
                    null,
                    null,
                    ItemTransforms.NO_TRANSFORMS, List.of()
            );
        }

        return new BlockGeometry(model);
    }

    public static class BlockGeometry implements IUnbakedGeometry<BlockGeometry> {
        final BlockModel blockModel;

        public BlockGeometry(BlockModel blockModel) {
            this.blockModel = blockModel;
        }

        @Override
        public @NotNull BakedModel bake(IGeometryBakingContext iGeometryBakingContext, ModelBaker modelBaker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides itemOverrides) {
            return this.blockModel.bake(modelBaker, spriteGetter, modelState);
        }

        @Override
        public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
            this.blockModel.resolveParents(modelGetter);
        }
    }
}
