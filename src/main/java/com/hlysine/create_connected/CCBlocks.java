package com.hlysine.create_connected;

import com.hlysine.create_connected.config.FeatureToggle;
import com.hlysine.create_connected.content.WrenchableBlock;
import com.hlysine.create_connected.content.brake.BrakeBlock;
import com.hlysine.create_connected.content.brassgearbox.BrassGearboxBlock;
import com.hlysine.create_connected.content.centrifugalclutch.CentrifugalClutchBlock;
import com.hlysine.create_connected.content.chaincogwheel.ChainCogwheelBlock;
import com.hlysine.create_connected.content.copycat.*;
import com.hlysine.create_connected.content.freewheelclutch.FreewheelClutchBlock;
import com.hlysine.create_connected.content.invertedclutch.InvertedClutchBlock;
import com.hlysine.create_connected.content.invertedgearshift.InvertedGearshiftBlock;
import com.hlysine.create_connected.content.itemsilo.ItemSiloBlock;
import com.hlysine.create_connected.content.itemsilo.ItemSiloCTBehaviour;
import com.hlysine.create_connected.content.itemsilo.ItemSiloItem;
import com.hlysine.create_connected.content.linkedtransmitter.LinkedAnalogLeverBlock;
import com.hlysine.create_connected.content.linkedtransmitter.LinkedButtonBlock;
import com.hlysine.create_connected.content.linkedtransmitter.LinkedLeverBlock;
import com.hlysine.create_connected.content.linkedtransmitter.LinkedTransmitterItem;
import com.hlysine.create_connected.content.overstressclutch.OverstressClutchBlock;
import com.hlysine.create_connected.content.parallelgearbox.ParallelGearboxBlock;
import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlock;
import com.hlysine.create_connected.content.shearpin.ShearPinBlock;
import com.hlysine.create_connected.content.sixwaygearbox.SixWayGearboxBlock;
import com.hlysine.create_connected.datagen.CCBlockStateGen;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.kinetics.chainDrive.ChainDriveGenerator;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockModel;
import com.simibubi.create.foundation.data.*;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.simibubi.create.foundation.data.AssetLookup.partialBaseModel;
import static com.simibubi.create.foundation.data.BlockStateGen.axisBlock;
import static com.simibubi.create.foundation.data.CreateRegistrate.connectedTextures;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

@SuppressWarnings("removal")
public class CCBlocks {
    private static final CreateRegistrate REGISTRATE = CreateConnected.getRegistrate();

    public static final BlockEntry<ChainCogwheelBlock> ENCASED_CHAIN_COGWHEEL =
            REGISTRATE.block("encased_chain_cogwheel", ChainCogwheelBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                    .addLayer(() -> RenderType::cutoutMipped)
                    .transform(BlockStressDefaults.setNoImpact())
                    .transform(FeatureToggle.register())
                    .transform(axeOrPickaxe())
                    .blockstate((c, p) -> new ChainDriveGenerator((state, suffix) -> p.models()
                            .getExistingFile(p.modLoc("block/" + c.getName() + "/" + suffix))).generate(c, p))
                    .item()
                    .transform(customItemModel())
                    .register();

    public static final BlockEntry<ParallelGearboxBlock> PARALLEL_GEARBOX = REGISTRATE.block("parallel_gearbox", ParallelGearboxBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .transform(BlockStressDefaults.setNoImpact())
            .transform(FeatureToggle.register())
            .transform(axeOrPickaxe())
            .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(AllSpriteShifts.ANDESITE_CASING)))
            .onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, AllSpriteShifts.ANDESITE_CASING,
                    (s, f) -> f.getAxis() == s.getValue(ParallelGearboxBlock.AXIS))))
            .blockstate((c, p) -> axisBlock(c, p, $ -> partialBaseModel(c, p), false))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<SixWayGearboxBlock> SIX_WAY_GEARBOX = REGISTRATE.block("six_way_gearbox", SixWayGearboxBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(BlockStressDefaults.setNoImpact())
            .transform(FeatureToggle.register())
            .transform(axeOrPickaxe())
            .lang("6-way Gearbox")
            .blockstate((c, p) -> axisBlock(c, p, $ -> partialBaseModel(c, p), false))
            .item()
            .transform(customItemModel())
            .register();


    public static final BlockEntry<OverstressClutchBlock> OVERSTRESS_CLUTCH = REGISTRATE.block("overstress_clutch", OverstressClutchBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(BlockStressDefaults.setNoImpact())
            .transform(FeatureToggle.register())
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> BlockStateGen.axisBlock(c, p,
                    forBoolean(c, state -> state.getValue(OverstressClutchBlock.STATE) == OverstressClutchBlock.ClutchState.UNCOUPLED, "uncoupled", p)
            ))
            .item()
            .transform(customItemModel())
            .register();


    public static final BlockEntry<ShearPinBlock> SHEAR_PIN = REGISTRATE.block("shear_pin", ShearPinBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.METAL).forceSolidOn())
            .transform(BlockStressDefaults.setNoImpact())
            .transform(FeatureToggle.register())
            .transform(pickaxeOnly())
            .blockstate(BlockStateGen.axisBlockProvider(false))
            .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
            .simpleItem()
            .register();

    public static final BlockEntry<InvertedClutchBlock> INVERTED_CLUTCH = REGISTRATE.block("inverted_clutch", InvertedClutchBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(BlockStressDefaults.setNoImpact())
            .transform(FeatureToggle.register())
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> BlockStateGen.axisBlock(c, p, AssetLookup.forPowered(c, p)))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<InvertedGearshiftBlock> INVERTED_GEARSHIFT = REGISTRATE.block("inverted_gearshift", InvertedGearshiftBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(BlockStressDefaults.setNoImpact())
            .transform(FeatureToggle.register())
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> BlockStateGen.axisBlock(c, p, AssetLookup.forPowered(c, p)))
            .item()
            .transform(customItemModel())
            .register();


    public static final BlockEntry<CentrifugalClutchBlock> CENTRIFUGAL_CLUTCH = REGISTRATE.block("centrifugal_clutch", CentrifugalClutchBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(BlockStressDefaults.setNoImpact())
            .transform(FeatureToggle.register())
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> p.directionalBlock(c.get(), forBoolean(c, state -> state.getValue(CentrifugalClutchBlock.UNCOUPLED), "uncoupled", p)))
            .item()
            .transform(customItemModel())
            .register();


    public static final BlockEntry<FreewheelClutchBlock> FREEWHEEL_CLUTCH = REGISTRATE.block("freewheel_clutch", FreewheelClutchBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(BlockStressDefaults.setNoImpact())
            .transform(FeatureToggle.register())
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> p.directionalBlock(c.get(), forBoolean(c, state -> state.getValue(FreewheelClutchBlock.UNCOUPLED), "uncoupled", p)))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<BrassGearboxBlock> BRASS_GEARBOX = REGISTRATE.block("brass_gearbox", BrassGearboxBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .transform(BlockStressDefaults.setNoImpact())
            .transform(FeatureToggle.register())
            .transform(axeOrPickaxe())
            .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(AllSpriteShifts.BRASS_CASING)))
            .onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, AllSpriteShifts.BRASS_CASING,
                    (s, f) -> f.getAxis() == s.getValue(BrassGearboxBlock.AXIS))))
            .blockstate(CCBlockStateGen.brassGearbox())
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<BrakeBlock> BRAKE = REGISTRATE.block("brake", BrakeBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(BlockStressDefaults.setNoImpact()) // active stress is a separate config
            .transform(FeatureToggle.register())
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> BlockStateGen.axisBlock(c, p, AssetLookup.forPowered(c, p)))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<SequencedPulseGeneratorBlock> SEQUENCED_PULSE_GENERATOR =
            REGISTRATE.block("sequenced_pulse_generator", SequencedPulseGeneratorBlock::new)
                    .initialProperties(() -> Blocks.REPEATER)
                    .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                    .blockstate(CCBlockStateGen.sequencedPulseGenerator())
                    .transform(FeatureToggle.register())
                    .addLayer(() -> RenderType::cutoutMipped)
                    .simpleItem()
                    .register();

    public static final Map<BlockSetType, BlockEntry<LinkedButtonBlock>> LINKED_BUTTONS = new HashMap<>();

    static {
        BlockSetType.values().forEach(type -> {
            Block button = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(type.name() + "_button"));
            if (button == null) return;
            if (!(button instanceof ButtonBlock buttonBlock))
                return;
            LINKED_BUTTONS.put(type, REGISTRATE
                    .block("linked_" + type.name() + "_button", properties -> new LinkedButtonBlock(properties, buttonBlock))
                    .initialProperties(() -> buttonBlock)
                    .transform(LinkedTransmitterItem.register())
                    .blockstate(CCBlockStateGen.linkedButton(
                            new ResourceLocation("block/" + type.name() + "_button"),
                            new ResourceLocation("block/" + type.name() + "_button_pressed")
                    ))
                    .register());
        });
    }

    public static final BlockEntry<LinkedLeverBlock> LINKED_LEVER = REGISTRATE
            .block("linked_lever", properties -> new LinkedLeverBlock(properties, (LeverBlock) Blocks.LEVER))
            .initialProperties(() -> Blocks.LEVER)
            .transform(LinkedTransmitterItem.register())
            .blockstate(CCBlockStateGen.linkedLever(
                    new ResourceLocation("block/lever"),
                    new ResourceLocation("block/lever_on")
            ))
            .register();

    public static final BlockEntry<LinkedAnalogLeverBlock> LINKED_ANALOG_LEVER = REGISTRATE
            .block("linked_analog_lever", properties -> new LinkedAnalogLeverBlock(properties, AllBlocks.ANALOG_LEVER.get()))
            .initialProperties(AllBlocks.ANALOG_LEVER)
            .transform(LinkedTransmitterItem.register())
            .blockstate(CCBlockStateGen.linkedLever(
                    Create.asResource("block/analog_lever/block"),
                    Create.asResource("block/analog_lever/block")
            ))
            .register();

    public static final BlockEntry<WrenchableBlock> EMPTY_FAN_CATALYST = REGISTRATE.block("empty_fan_catalyst", WrenchableBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p
                    .mapColor(MapColor.TERRACOTTA_YELLOW)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isRedstoneConductor((state, level, pos) -> false)
            )
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(pickaxeOnly())
            .transform(FeatureToggle.register())
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
            .tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag)
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<WrenchableBlock> FAN_BLASTING_CATALYST = REGISTRATE.block("fan_blasting_catalyst", WrenchableBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p
                    .mapColor(MapColor.TERRACOTTA_YELLOW)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .lightLevel(s -> 10)
                    .isRedstoneConductor((state, level, pos) -> false)
            )
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(pickaxeOnly())
            .transform(FeatureToggle.registerDependent(CCBlocks.EMPTY_FAN_CATALYST))
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
            .tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag)
            .tag(AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_BLASTING.tag)
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<WrenchableBlock> FAN_SMOKING_CATALYST = REGISTRATE.block("fan_smoking_catalyst", WrenchableBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p
                    .mapColor(MapColor.TERRACOTTA_YELLOW)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .lightLevel(s -> 10)
                    .isRedstoneConductor((state, level, pos) -> false)
            )
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(pickaxeOnly())
            .transform(FeatureToggle.registerDependent(CCBlocks.EMPTY_FAN_CATALYST))
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
            .tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag)
            .tag(AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_SMOKING.tag)
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<WrenchableBlock> FAN_SPLASHING_CATALYST = REGISTRATE.block("fan_splashing_catalyst", WrenchableBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p
                    .mapColor(MapColor.TERRACOTTA_YELLOW)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isRedstoneConductor((state, level, pos) -> false)
            )
            .transform(pickaxeOnly())
            .transform(FeatureToggle.registerDependent(CCBlocks.EMPTY_FAN_CATALYST))
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
            .color(() -> CCColorHandlers::waterBlockTint)
            .lang("Fan Washing Catalyst")
            .tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag)
            .tag(AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_SPLASHING.tag)
            .item()
            .color(() -> CCColorHandlers::waterItemTint)
            .transform(customItemModel())
            .register();

    public static final BlockEntry<WrenchableBlock> FAN_HAUNTING_CATALYST = REGISTRATE.block("fan_haunting_catalyst", WrenchableBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p
                    .mapColor(MapColor.TERRACOTTA_YELLOW)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .lightLevel(s -> 5)
                    .isRedstoneConductor((state, level, pos) -> false)
            )
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(pickaxeOnly())
            .transform(FeatureToggle.registerDependent(CCBlocks.EMPTY_FAN_CATALYST))
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
            .tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag)
            .tag(AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_HAUNTING.tag)
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<ItemSiloBlock> ITEM_SILO = REGISTRATE.block("item_silo", ItemSiloBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_BLUE).sound(SoundType.NETHERITE_BLOCK)
                    .explosionResistance(1200))
            .transform(pickaxeOnly())
            .transform(FeatureToggle.register())
            .blockstate((c, p) -> p.getVariantBuilder(c.get())
                    .forAllStates(s -> ConfiguredModel.builder()
                            .modelFile(AssetLookup.standardModel(c, p))
                            .build()))
            .onRegister(connectedTextures(ItemSiloCTBehaviour::new))
            .item(ItemSiloItem::new)
            .build()
            .register();

    public static final BlockEntry<CopycatSlabBlock> COPYCAT_SLAB =
            REGISTRATE.block("copycat_slab", CopycatSlabBlock::new)
                    .transform(BuilderTransformers.copycat())
                    .transform(FeatureToggle.register())
                    .onRegister(CreateRegistrate.blockModel(() -> CopycatSlabModel::new))
                    .item()
                    .transform(customItemModel("copycat_base", "slab"))
                    .register();

    public static final BlockEntry<CopycatBlockBlock> COPYCAT_BLOCK =
            REGISTRATE.block("copycat_block", CopycatBlockBlock::new)
                    .transform(BuilderTransformers.copycat())
                    .transform(FeatureToggle.register())
                    .onRegister(CreateRegistrate.blockModel(() -> CopycatBlockModel::new))
                    .item()
                    .transform(customItemModel("copycat_base", "block"))
                    .register();

    public static final BlockEntry<CopycatBeamBlock> COPYCAT_BEAM =
            REGISTRATE.block("copycat_beam", CopycatBeamBlock::new)
                    .transform(BuilderTransformers.copycat())
                    .transform(FeatureToggle.register())
                    .onRegister(CreateRegistrate.blockModel(() -> CopycatBeamModel::new))
                    .item()
                    .transform(customItemModel("copycat_base", "beam"))
                    .register();

    public static final BlockEntry<CopycatVerticalStepBlock> COPYCAT_VERTICAL_STEP =
            REGISTRATE.block("copycat_vertical_step", CopycatVerticalStepBlock::new)
                    .transform(BuilderTransformers.copycat())
                    .transform(FeatureToggle.register())
                    .onRegister(CreateRegistrate.blockModel(() -> CopycatVerticalStepModel::new))
                    .item()
                    .transform(customItemModel("copycat_base", "vertical_step"))
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
