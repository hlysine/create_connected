package com.hlysine.create_connected.content.fluidvessel;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.ModelFile;

import static com.hlysine.create_connected.content.fluidvessel.FluidVesselBlock.*;
import static net.minecraft.core.Direction.Axis;

public class FluidVesselGenerator extends SpecialBlockStateGen {

    private String prefix;

    public FluidVesselGenerator() {
        this("");
    }

    public FluidVesselGenerator(String prefix) {
        this.prefix = prefix;
    }

    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return 0;
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        Boolean positive = state.getValue(POSITIVE);
        Boolean negative = state.getValue(NEGATIVE);
        Shape shape = state.getValue(SHAPE);
        Axis axis = state.getValue(AXIS);

        if (positive && negative)
            shape = shape.nonSingleVariant();

        String shapeName = "middle";
        if (positive && negative)
            shapeName = "single";
        else if (positive)
            shapeName = "positive";
        else if (negative)
            shapeName = "negative";

        String modelName = (axis == Axis.X ? "x" : "z") +
                "_" + shapeName +
                (shape == Shape.PLAIN ? "" : "_" + shape.getSerializedName());

        if (!prefix.isEmpty())
            return prov.models()
                    .withExistingParent(prefix + modelName, prov.modLoc("block/fluid_vessel/block_" + modelName))
                    .texture("0", Create.asResource("block/" + prefix + "casing"))
                    .texture("1", Create.asResource("block/" + prefix + "fluid_tank"))
                    .texture("3", Create.asResource("block/" + prefix + "fluid_tank_window"))
                    .texture("4", Create.asResource("block/" + prefix + "casing"))
                    .texture("5", Create.asResource("block/" + prefix + "fluid_tank_window_single"))
                    .texture("6", prov.modLoc("block/" + prefix + "fluid_container_window"))
                    .texture("7", prov.modLoc("block/" + prefix + "fluid_container_window_single"))
                    .texture("particle", Create.asResource("block/" + prefix + "fluid_tank"));

        return AssetLookup.partialBaseModel(ctx, prov, modelName);
    }

}
