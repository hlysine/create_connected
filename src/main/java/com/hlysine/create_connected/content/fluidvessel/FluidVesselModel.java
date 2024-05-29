package com.hlysine.create_connected.content.fluidvessel;

import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.foundation.block.connected.CTModel;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import java.util.*;

import static net.minecraft.core.Direction.Axis;
import static net.minecraftforge.client.model.data.ModelDataMap.Builder;

public class FluidVesselModel extends CTModel {

    protected static final ModelProperty<CullData> CULL_PROPERTY = new ModelProperty<>();

    public static FluidVesselModel standard(BakedModel originalModel) {
        return new FluidVesselModel(originalModel, AllSpriteShifts.FLUID_TANK, AllSpriteShifts.FLUID_TANK_TOP,
                AllSpriteShifts.FLUID_TANK_INNER);
    }

    public static FluidVesselModel creative(BakedModel originalModel) {
        return new FluidVesselModel(originalModel, AllSpriteShifts.CREATIVE_FLUID_TANK, AllSpriteShifts.CREATIVE_CASING,
                AllSpriteShifts.CREATIVE_CASING);
    }

    private FluidVesselModel(BakedModel originalModel, CTSpriteShiftEntry side, CTSpriteShiftEntry top,
                             CTSpriteShiftEntry inner) {
        super(originalModel, new FluidVesselCTBehaviour(side, top, inner));
    }

    @Override
    protected void gatherModelData(Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state,
                                   IModelData blockEntityData) {
        super.gatherModelData(builder, world, pos, state, blockEntityData);
        CullData cullData = new CullData();
        Axis axis = state.getValue(FluidVesselBlock.AXIS);
        for (Direction d : Iterate.directions) {
            if (d.getAxis() == axis)
                continue;
            cullData.setCulled(d, ConnectivityHandler.isConnected(world, pos, pos.relative(d)));
        }
        builder.withInitial(CULL_PROPERTY, cullData);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
        if (side != null)
            return Collections.emptyList();

        List<BakedQuad> quads = new ArrayList<>();
        for (Direction d : Iterate.directions) {
            if (extraData.hasProperty(CULL_PROPERTY) && extraData.getData(CULL_PROPERTY)
                    .isCulled(d))
                continue;
            quads.addAll(super.getQuads(state, d, rand, extraData));
        }
        quads.addAll(super.getQuads(state, null, rand, extraData));
        return quads;
    }

    protected static class CullData {
        boolean[] culledFaces;

        public CullData() {
            culledFaces = new boolean[6];
            Arrays.fill(culledFaces, false);
        }

        void setCulled(Direction face, boolean cull) {
            culledFaces[face.get3DDataValue()] = cull;
        }

        boolean isCulled(Direction face) {
            return culledFaces[face.get3DDataValue()];
        }
    }

}
