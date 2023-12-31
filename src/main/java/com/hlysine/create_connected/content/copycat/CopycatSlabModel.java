package com.hlysine.create_connected.content.copycat;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.content.decoration.copycat.CopycatModel;
import com.simibubi.create.foundation.model.BakedModelHelper;
import com.simibubi.create.foundation.model.BakedQuadHelper;
import com.simibubi.create.foundation.utility.Iterate;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;

public class CopycatSlabModel extends CopycatModel {

    protected static final AABB CUBE_AABB = new AABB(BlockPos.ZERO);

    public CopycatSlabModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    protected List<BakedQuad> getCroppedQuads(BlockState state, Direction side, RandomSource rand, BlockState material,
                                              ModelData wrappedData, RenderType renderType) {
        Direction facing = state.getOptionalValue(CopycatSlabBlock.SLAB_TYPE).isPresent() ? CopycatSlabBlock.getApparentDirection(state) : Direction.UP;

        BakedModel model = getModelOf(material);
        List<BakedQuad> templateQuads = model.getQuads(material, side, rand, wrappedData, renderType);

        List<BakedQuad> quads = new ArrayList<>();

        // 2 pieces
        for (boolean front : Iterate.trueAndFalse) {
            assemblePiece(facing, templateQuads, quads, front, false);
        }

        // 2 more pieces for double slabs
        if (state.getOptionalValue(CopycatSlabBlock.SLAB_TYPE).orElse(SlabType.BOTTOM) == SlabType.DOUBLE) {
            for (boolean front : Iterate.trueAndFalse) {
                assemblePiece(facing, templateQuads, quads, front, true);
            }
        }

        return quads;
    }

    private static void assemblePiece(Direction facing, List<BakedQuad> templateQuads, List<BakedQuad> quads, boolean front, boolean topSlab) {
        int size = templateQuads.size();
        Vec3 normal = Vec3.atLowerCornerOf(facing.getNormal());
        Vec3 normalScaled12 = normal.scale(12 / 16f);
        Vec3 normalScaledN8 = topSlab ? normal.scale((front ? 0 : -8) / 16f) : normal.scale((front ? 8 : 0) / 16f);
        float contract = 12;
        AABB bb = CUBE_AABB.contract(normal.x * contract / 16, normal.y * contract / 16, normal.z * contract / 16);
        if (!front)
            bb = bb.move(normalScaled12);

        for (int i = 0; i < size; i++) {
            BakedQuad quad = templateQuads.get(i);
            Direction direction = quad.getDirection();

            if (front && direction == facing)
                continue;
            if (!front && direction == facing.getOpposite())
                continue;

            quads.add(BakedQuadHelper.cloneWithCustomGeometry(quad,
                    BakedModelHelper.cropAndMove(quad.getVertices(), quad.getSprite(), bb, normalScaledN8)));
        }
    }
}
