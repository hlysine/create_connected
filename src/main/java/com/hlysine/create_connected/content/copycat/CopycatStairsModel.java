package com.hlysine.create_connected.content.copycat;

import com.simibubi.create.content.decoration.copycat.CopycatModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraftforge.client.model.data.ModelData;

import java.util.ArrayList;
import java.util.List;

import static com.hlysine.create_connected.content.copycat.ISimpleCopycatModel.MutableCullFace.*;

public class CopycatStairsModel extends CopycatModel implements ISimpleCopycatModel {

    public CopycatStairsModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    protected List<BakedQuad> getCroppedQuads(BlockState state, Direction side, RandomSource rand, BlockState material,
                                              ModelData wrappedData, RenderType renderType) {
        BakedModel model = getModelOf(material);
        List<BakedQuad> templateQuads = model.getQuads(material, side, rand, wrappedData, renderType);

        List<BakedQuad> quads = new ArrayList<>();

        int facing = (int) state.getValue(StairBlock.FACING).toYRot();
        boolean top = state.getValue(StairBlock.HALF) == Half.TOP;
        StairsShape shape = state.getValue(StairBlock.SHAPE);

        switch (shape) {
            case STRAIGHT -> {
                assemblePiece(templateQuads, quads,
                        aabb(16, 4, 8),
                        vec3(0, 0, 0),
                        cull(UP | SOUTH),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(16, 4, 8).move(0, 12, 0),
                        vec3(0, 4, 0),
                        cull(DOWN | SOUTH),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(16, 8, 8).move(0, 0, 8),
                        vec3(0, 0, 8),
                        cull(UP | NORTH),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(16, 8, 4).move(0, 8, 0),
                        vec3(0, 8, 8),
                        cull(DOWN | SOUTH),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(16, 8, 4).move(0, 8, 12),
                        vec3(0, 8, 12),
                        cull(DOWN | NORTH),
                        facing, top);
            }
            case INNER_LEFT -> {
                assemblePiece(templateQuads, quads,
                        aabb(8, 4, 8),
                        vec3(0, 0, 0),
                        cull(UP | SOUTH | EAST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(8, 4, 8).move(0, 12, 0),
                        vec3(0, 4, 0),
                        cull(DOWN | SOUTH | EAST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(16, 8, 8).move(0, 0, 8),
                        vec3(0, 0, 8),
                        cull(UP | NORTH),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(8, 8, 8).move(8, 8, 8),
                        vec3(8, 8, 8),
                        cull(DOWN | NORTH | WEST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(8, 8, 4).move(0, 8, 12),
                        vec3(0, 8, 12),
                        cull(DOWN | NORTH | EAST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(8, 8, 4).move(0, 8, 0),
                        vec3(0, 8, 8),
                        cull(DOWN | SOUTH | EAST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(4, 8, 8).move(12, 8, 0),
                        vec3(12, 8, 0),
                        cull(DOWN | SOUTH | WEST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(4, 8, 8).move(0, 8, 0),
                        vec3(8, 8, 0),
                        cull(DOWN | SOUTH | EAST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(8, 8, 8).move(8, 0, 0),
                        vec3(8, 0, 0),
                        cull(UP | SOUTH | WEST),
                        facing, top);
            }
            case INNER_RIGHT -> {
                assemblePiece(templateQuads, quads,
                        aabb(8, 4, 8).move(8, 0, 0),
                        vec3(8, 0, 0),
                        cull(UP | SOUTH | WEST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(8, 4, 8).move(8, 12, 0),
                        vec3(8, 4, 0),
                        cull(DOWN | SOUTH | WEST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(16, 8, 8).move(0, 0, 8),
                        vec3(0, 0, 8),
                        cull(UP | NORTH),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(8, 8, 8).move(0, 8, 8),
                        vec3(0, 8, 8),
                        cull(DOWN | NORTH | EAST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(8, 8, 4).move(8, 8, 12),
                        vec3(8, 8, 12),
                        cull(DOWN | NORTH | WEST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(8, 8, 4).move(8, 8, 0),
                        vec3(8, 8, 8),
                        cull(DOWN | SOUTH | WEST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(4, 8, 8).move(12, 8, 0),
                        vec3(4, 8, 0),
                        cull(DOWN | SOUTH | WEST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(4, 8, 8).move(0, 8, 0),
                        vec3(0, 8, 0),
                        cull(DOWN | SOUTH | EAST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(8, 8, 8).move(0, 0, 0),
                        vec3(0, 0, 0),
                        cull(UP | SOUTH | EAST),
                        facing, top);
            }
            case OUTER_LEFT -> {
                assemblePiece(templateQuads, quads,
                        aabb(8, 4, 16).move(0, 0, 0),
                        vec3(0, 0, 0),
                        cull(UP | EAST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(8, 4, 16).move(0, 12, 0),
                        vec3(0, 4, 0),
                        cull(DOWN | EAST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(8, 4, 8).move(8, 0, 0),
                        vec3(8, 0, 0),
                        cull(UP | SOUTH | WEST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(8, 4, 8).move(8, 12, 0),
                        vec3(8, 4, 0),
                        cull(DOWN | SOUTH | WEST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(8, 8, 8).move(8, 0, 8),
                        vec3(8, 0, 8),
                        cull(UP | NORTH | WEST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(4, 8, 4).move(12, 8, 12),
                        vec3(12, 8, 12),
                        cull(DOWN | NORTH | WEST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(4, 8, 4).move(0, 8, 12),
                        vec3(8, 8, 12),
                        cull(DOWN | NORTH | EAST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(4, 8, 4).move(12, 8, 0),
                        vec3(12, 8, 8),
                        cull(DOWN | SOUTH | WEST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(4, 8, 4).move(0, 8, 0),
                        vec3(8, 8, 8),
                        cull(DOWN | SOUTH | EAST),
                        facing, top);
            }
            case OUTER_RIGHT -> {
                assemblePiece(templateQuads, quads,
                        aabb(8, 4, 16).move(8, 0, 0),
                        vec3(8, 0, 0),
                        cull(UP | WEST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(8, 4, 16).move(8, 12, 0),
                        vec3(8, 4, 0),
                        cull(DOWN | WEST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(8, 4, 8).move(0, 0, 0),
                        vec3(0, 0, 0),
                        cull(UP | EAST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(8, 4, 8).move(0, 12, 0),
                        vec3(0, 4, 0),
                        cull(DOWN | EAST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(8, 8, 8).move(0, 0, 8),
                        vec3(0, 0, 8),
                        cull(UP | NORTH | EAST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(4, 8, 4).move(12, 8, 12),
                        vec3(4, 8, 12),
                        cull(DOWN | NORTH | WEST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(4, 8, 4).move(0, 8, 12),
                        vec3(0, 8, 12),
                        cull(DOWN | NORTH | EAST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(4, 8, 4).move(12, 8, 0),
                        vec3(4, 8, 8),
                        cull(DOWN | SOUTH | WEST),
                        facing, top);
                assemblePiece(templateQuads, quads,
                        aabb(4, 8, 4).move(0, 8, 0),
                        vec3(0, 8, 8),
                        cull(DOWN | SOUTH | EAST),
                        facing, top);
            }
        }

        return quads;
    }

}
