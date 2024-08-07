package com.hlysine.create_connected.datagen;

import com.hlysine.create_connected.CCSpriteShifts;
import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.config.FeatureCategory;
import com.hlysine.create_connected.config.FeatureToggle;
import com.simibubi.create.Create;
import com.simibubi.create.content.decoration.palettes.ConnectedGlassPaneBlock;
import com.simibubi.create.content.decoration.palettes.GlassPaneBlock;
import com.simibubi.create.content.decoration.palettes.WindowBlock;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.GlassPaneCTBehaviour;
import com.simibubi.create.foundation.block.connected.HorizontalCTBehaviour;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.Tags;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.simibubi.create.foundation.data.CreateRegistrate.connectedTextures;

public class CCWindowGen {

    public static BlockEntry<WindowBlock> woodenWindowBlock(WoodType woodType,
                                                            Block planksBlock,
                                                            Supplier<Supplier<RenderType>> renderType,
                                                            boolean translucent) {
        String woodName = woodType.name();
        String name = woodName + "_window";
        NonNullFunction<String, ResourceLocation> end_texture =
                $ -> new ResourceLocation("block/" + woodName + "_planks");
        NonNullFunction<String, ResourceLocation> side_texture = n -> CreateConnected.asResource("block/" + n);
        return windowBlock(name, () -> planksBlock, () -> CCSpriteShifts.WOODEN_WINDOWS.get(woodType), renderType,
                translucent, end_texture, side_texture, planksBlock::defaultMapColor);
    }

    private static BlockEntry<WindowBlock> windowBlock(String name,
                                                       Supplier<? extends ItemLike> ingredient,
                                                       Supplier<CTSpriteShiftEntry> ct,
                                                       Supplier<Supplier<RenderType>> renderType,
                                                       boolean translucent,
                                                       NonNullFunction<String, ResourceLocation> endTexture,
                                                       NonNullFunction<String, ResourceLocation> sideTexture,
                                                       Supplier<MapColor> color) {
        return CreateConnected.getRegistrate().block(name, p -> new WindowBlock(p, translucent))
                .onRegister(connectedTextures(() -> new HorizontalCTBehaviour(ct.get())))
                .addLayer(renderType)
                .initialProperties(() -> Blocks.GLASS)
                .properties(p -> p.isValidSpawn(CCWindowGen::never)
                        .isRedstoneConductor(CCWindowGen::never)
                        .isSuffocating(CCWindowGen::never)
                        .isViewBlocking(CCWindowGen::never)
                        .mapColor(color.get()))
                .loot(RegistrateBlockLootTables::dropWhenSilkTouch)
                .transform(FeatureToggle.register(FeatureCategory.PALETTE))
                .blockstate((c, p) -> p.simpleBlock(c.get(), p.models()
                        .cubeColumn(c.getName(), sideTexture.apply(c.getName()), endTexture.apply(c.getName()))))
                .tag(BlockTags.IMPERMEABLE)
                .simpleItem()
                .register();
    }

    public static BlockEntry<ConnectedGlassPaneBlock> woodenWindowPane(WoodType woodType,
                                                                       BlockEntry<? extends Block> parent,
                                                                       Supplier<Supplier<RenderType>> renderType) {
        String woodName = woodType.name();
        String name = woodName + "_window";
        ResourceLocation topTexture = new ResourceLocation("block/" + woodName + "_planks");
        ResourceLocation sideTexture = CreateConnected.asResource("block/" + name);
        return connectedGlassPane(name, parent, () -> CCSpriteShifts.WOODEN_WINDOWS.get(woodType), sideTexture,
                sideTexture, topTexture, renderType);
    }

    private static BlockEntry<ConnectedGlassPaneBlock> connectedGlassPane(String name,
                                                                          BlockEntry<? extends Block> parent,
                                                                          Supplier<CTSpriteShiftEntry> ctshift,
                                                                          ResourceLocation sideTexture,
                                                                          ResourceLocation itemSideTexture,
                                                                          ResourceLocation topTexture,
                                                                          Supplier<Supplier<RenderType>> renderType) {
        NonNullConsumer<? super ConnectedGlassPaneBlock> connectedTextures =
                connectedTextures(() -> new GlassPaneCTBehaviour(ctshift.get()));
        String CGPparents = "block/connected_glass_pane/";
        String prefix = name + "_pane_";

        Function<RegistrateBlockstateProvider, ModelFile> post =
                getPaneModelProvider(CGPparents, prefix, "post", sideTexture, topTexture),
                side = getPaneModelProvider(CGPparents, prefix, "side", sideTexture, topTexture),
                sideAlt = getPaneModelProvider(CGPparents, prefix, "side_alt", sideTexture, topTexture),
                noSide = getPaneModelProvider(CGPparents, prefix, "noside", sideTexture, topTexture),
                noSideAlt = getPaneModelProvider(CGPparents, prefix, "noside_alt", sideTexture, topTexture);

        NonNullBiConsumer<DataGenContext<Block, ConnectedGlassPaneBlock>, RegistrateBlockstateProvider> stateProvider =
                (c, p) -> p.paneBlock(c.get(), post.apply(p), side.apply(p), sideAlt.apply(p), noSide.apply(p),
                        noSideAlt.apply(p));

        return glassPane(name, parent, itemSideTexture, topTexture, ConnectedGlassPaneBlock::new, renderType,
                connectedTextures, stateProvider);
    }

    private static <G extends GlassPaneBlock> BlockEntry<G> glassPane(String name,
                                                                      BlockEntry<? extends Block> parent,
                                                                      ResourceLocation sideTexture,
                                                                      ResourceLocation topTexture,
                                                                      NonNullFunction<BlockBehaviour.Properties, G> factory,
                                                                      Supplier<Supplier<RenderType>> renderType,
                                                                      NonNullConsumer<? super G> connectedTextures,
                                                                      NonNullBiConsumer<DataGenContext<Block, G>, RegistrateBlockstateProvider> stateProvider) {
        name += "_pane";

        return CreateConnected.getRegistrate().block(name, factory)
                .onRegister(connectedTextures)
                .addLayer(renderType)
                .initialProperties(() -> Blocks.GLASS_PANE)
                .properties(p -> p.mapColor(parent.get()
                        .defaultMapColor()))
                .blockstate(stateProvider)
                .transform(FeatureToggle.registerDependent(parent))
                .tag(Tags.Blocks.GLASS_PANES)
                .loot(RegistrateBlockLootTables::dropWhenSilkTouch)
                .item()
                .tag(Tags.Items.GLASS_PANES)
                .model((c, p) -> p.withExistingParent(c.getName(), Create.asResource("item/pane"))
                        .texture("pane", sideTexture)
                        .texture("edge", topTexture))
                .build()
                .register();
    }

    private static Function<RegistrateBlockstateProvider, ModelFile> getPaneModelProvider(String CGPparents,
                                                                                          String prefix,
                                                                                          String partial,
                                                                                          ResourceLocation sideTexture,
                                                                                          ResourceLocation topTexture) {
        return p -> p.models()
                .withExistingParent(prefix + partial, Create.asResource(CGPparents + partial))
                .texture("pane", sideTexture)
                .texture("edge", topTexture);
    }

    private static boolean never(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    private static Boolean never(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> entityType) {
        return false;
    }
}
