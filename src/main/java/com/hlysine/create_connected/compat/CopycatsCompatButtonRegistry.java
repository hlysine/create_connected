package com.hlysine.create_connected.compat;

import com.copycatsplus.copycats.content.copycat.button.CopycatButtonModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.neoforge.CopycatModelCoreImpl;
import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.content.linkedtransmitter.*;
import com.hlysine.create_connected.datagen.CCBlockStateGen;
import com.hlysine.create_connected.registries.PreciseItemUseOverrides;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

import static com.copycatsplus.copycats.CCBlocks.onClient;

public class CopycatsCompatButtonRegistry {
    private static final CreateRegistrate REGISTRATE = CreateConnected.getRegistrate();

    public static final BlockEntry<LinkedCopycatButtonBlock> WOODEN_BUTTON = REGISTRATE
            .block("linked_copycat_wooden_button", properties -> new LinkedCopycatButtonBlock(properties, com.copycatsplus.copycats.CCBlocks.COPYCAT_WOODEN_BUTTON))
            .initialProperties(() -> Blocks.OAK_BUTTON)
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(LinkedTransmitterItem.register())
            .onRegister(onClient(() -> createBlockModel(CopycatButtonModelCore::new)))
            .onRegister(PreciseItemUseOverrides::addBlock)
            .blockstate(CCBlockStateGen.linkedLeverNoPower(
                    // no button blockstates from copycat+
                    ResourceLocation.withDefaultNamespace("block/air")
            ))
            .asOptional()
            .register();

    public static final BlockEntry<LinkedCopycatButtonBlock> STONE_BUTTON = REGISTRATE
            .block("linked_copycat_stone_button", properties -> new LinkedCopycatButtonBlock(properties, com.copycatsplus.copycats.CCBlocks.COPYCAT_STONE_BUTTON))
            .initialProperties(() -> Blocks.STONE_BUTTON)
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(LinkedTransmitterItem.register())
            .onRegister(onClient(() -> createBlockModel(CopycatButtonModelCore::new)))
            .onRegister(PreciseItemUseOverrides::addBlock)
            .blockstate(CCBlockStateGen.linkedLeverNoPower(
                    // no button blockstates from copycat+
                    ResourceLocation.withDefaultNamespace("block/air")
            ))
            .asOptional()
            .register();


    public static final BlockEntityEntry<LinkedCopycatButtonBlockEntity> LINKED_TRANSMITTER = REGISTRATE
            .blockEntity("linked_copycat_button_transmitter", LinkedCopycatButtonBlockEntity::new)
            .validBlock(CopycatsCompatButtonRegistry.WOODEN_BUTTON)
            .validBlock(CopycatsCompatButtonRegistry.STONE_BUTTON)
            .renderer(() -> SmartBlockEntityRenderer::new)
            .register();

    public static void register() {
    }

    private static @NotNull <Model extends CopycatModelCore> NonNullConsumer<? super Block> createBlockModel(Supplier<Model> model) {
        return CreateRegistrate.blockModel(() -> linkModel ->
                new CombinedBakedModel(CopycatModelCoreImpl.createModel(
                        linkModel, model.get()), linkModel));
    }

    private static class CombinedBakedModel extends BakedModelWrapper<BakedModel> {
        private final BakedModel linkModel;

        public CombinedBakedModel(BakedModel baseModel, BakedModel linkModel) {
            super(baseModel);
            this.linkModel = linkModel;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
            List<BakedQuad> baseQuads = super.getQuads(state, side, rand);
            List<BakedQuad> linkQuads = this.linkModel.getQuads(state, side, rand);
            baseQuads.addAll(linkQuads);

            return baseQuads;
        }

        @Override
        public @NotNull List<BakedQuad> getQuads(BlockState state, Direction side, @NotNull RandomSource rand, @NotNull ModelData data, RenderType renderType) {
            List<BakedQuad> baseQuads = super.getQuads(state, side, rand, data, renderType);
            List<BakedQuad> linkQuads = this.linkModel.getQuads(state, side, rand, data, renderType);
            baseQuads.addAll(linkQuads);

            return baseQuads;
        }
    }
}
