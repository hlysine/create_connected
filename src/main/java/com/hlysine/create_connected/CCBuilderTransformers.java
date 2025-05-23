package com.hlysine.create_connected;

import com.hlysine.create_connected.content.crossconnector.EncasedCrossConnectorBlock;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

import static com.simibubi.create.foundation.data.BlockStateGen.axisBlock;

public class CCBuilderTransformers {
    public static <B extends EncasedCrossConnectorBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> encasedCrossConnector(String casing,
                                                                                                                           Supplier<CTSpriteShiftEntry> casingShift) {
        return builder -> encasedBase(builder, CCBlocks.CROSS_CONNECTOR::get)
                .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(casingShift.get())))
                .onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, casingShift.get(),
                        (s, f) -> f.getAxis() == s.getValue(EncasedCrossConnectorBlock.AXIS))))
                .blockstate((c, p) -> axisBlock(c, p, blockState -> p.models()
                        .getExistingFile(p.modLoc("block/cross_connector/block_" + casing)), true))
                .item()
                .model(AssetLookup.customBlockItemModel("cross_connector", "item_" + casing))
                .build();
    }

    private static <B extends Block, P> BlockBuilder<B, P> encasedBase(BlockBuilder<B, P> b,
                                                                       Supplier<ItemLike> drop) {
        return b.initialProperties(SharedProperties::stone)
                .properties(BlockBehaviour.Properties::noOcclusion)
                .loot((p, lb) -> p.dropOther(lb, drop.get()));
    }
}
