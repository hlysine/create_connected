package com.hlysine.create_connected.content.copycat.fencegate;

import com.hlysine.create_connected.content.copycat.ISimpleCopycatModel;
import com.simibubi.create.content.decoration.copycat.CopycatModel;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import java.util.ArrayList;
import java.util.List;

import static com.hlysine.create_connected.content.copycat.ISimpleCopycatModel.MutableCullFace.*;
import static net.minecraft.world.level.block.FenceGateBlock.IN_WALL;
import static net.minecraft.world.level.block.FenceGateBlock.OPEN;
import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class CopycatFenceGateModel extends CopycatModel implements ISimpleCopycatModel {

    public CopycatFenceGateModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    protected List<BakedQuad> getCroppedQuads(BlockState state, Direction side, RandomSource rand, BlockState material,
                                              ModelData wrappedData, RenderType renderType) {
        BakedModel model = getModelOf(material);
        List<BakedQuad> templateQuads = model.getQuads(material, side, rand, wrappedData, renderType);

        List<BakedQuad> quads = new ArrayList<>();

        int offsetWall = state.getValue(IN_WALL) ? -3 : 0;
        int rot = (int) state.getValue(FACING).toYRot();

        // Assemble the poles
        for (boolean eastSide : Iterate.falseAndTrue) {
            int offsetX = eastSide ? 14 : 0;
            assemblePiece(
                    templateQuads, quads, rot, false,
                    vec3(offsetX, 5 + offsetWall, 7),
                    aabb(1, 6, 1),
                    cull(UP | SOUTH | EAST)
            );
            assemblePiece(
                    templateQuads, quads, rot, false,
                    vec3(offsetX + 1, 5 + offsetWall, 7),
                    aabb(1, 6, 1).move(15, 0, 0),
                    cull(UP | SOUTH | WEST)
            );
            assemblePiece(
                    templateQuads, quads, rot, false,
                    vec3(offsetX, 5 + offsetWall, 8),
                    aabb(1, 6, 1).move(0, 0, 15),
                    cull(UP | NORTH | EAST)
            );
            assemblePiece(
                    templateQuads, quads, rot, false,
                    vec3(offsetX + 1, 5 + offsetWall, 8),
                    aabb(1, 6, 1).move(15, 0, 15),
                    cull(UP | NORTH | WEST)
            );
            assemblePiece(
                    templateQuads, quads, rot, false,
                    vec3(offsetX, 11 + offsetWall, 7),
                    aabb(1, 5, 1).move(0, 11, 0),
                    cull(DOWN | SOUTH | EAST)
            );
            assemblePiece(
                    templateQuads, quads, rot, false,
                    vec3(offsetX + 1, 11 + offsetWall, 7),
                    aabb(1, 5, 1).move(15, 11, 0),
                    cull(DOWN | SOUTH | WEST)
            );
            assemblePiece(
                    templateQuads, quads, rot, false,
                    vec3(offsetX, 11 + offsetWall, 8),
                    aabb(1, 5, 1).move(0, 11, 15),
                    cull(DOWN | NORTH | EAST)
            );
            assemblePiece(
                    templateQuads, quads, rot, false,
                    vec3(offsetX + 1, 11 + offsetWall, 8),
                    aabb(1, 5, 1).move(15, 11, 15),
                    cull(DOWN | NORTH | WEST)
            );
        }

        if (state.getValue(OPEN)) {
            for (boolean eastDoor : Iterate.falseAndTrue) {
                for (boolean eastSide : Iterate.falseAndTrue) {
                    int offsetX = (eastDoor ? 14 : 0) + (eastSide ? 1 : 0);
                    assemblePiece(
                            templateQuads, quads, rot, false,
                            vec3(offsetX, 12 + offsetWall, 9),
                            aabb(1, 3, 6).move(eastSide ? 15 : 0, 13, 10),
                            cull(NORTH |  (eastSide ? WEST : EAST))
                    );
                    assemblePiece(
                            templateQuads, quads, rot, false,
                            vec3(offsetX, 9 + offsetWall, 13),
                            aabb(1, 3, 2).move(eastSide ? 15 : 0, 7, 14),
                            cull(UP | DOWN | (eastSide ? WEST : EAST))
                    );
                    assemblePiece(
                            templateQuads, quads, rot, false,
                            vec3(offsetX, 6 + offsetWall, 9),
                            aabb(1, 3, 6).move(eastSide ? 15 : 0, 0, 10),
                            cull(NORTH | (eastSide ? WEST : EAST))
                    );
                }
            }
        } else {
            for (boolean southSide : Iterate.falseAndTrue) {
                int rot2 = rot + (southSide ? 180 : 0);
                assemblePiece(
                        templateQuads, quads, rot2, false,
                        vec3(8, 12 + offsetWall, 7),
                        aabb(6, 3, 1).move(0, 13, 0),
                        cull(SOUTH | EAST | WEST)
                );
                assemblePiece(
                        templateQuads, quads, rot2, false,
                        vec3(8, 9 + offsetWall, 7),
                        aabb(2, 3, 1).move(0, 7, 0),
                        cull(UP | DOWN | SOUTH | WEST)
                );
                assemblePiece(
                        templateQuads, quads, rot2, false,
                        vec3(8, 6 + offsetWall, 7),
                        aabb(6, 3, 1),
                        cull(SOUTH | EAST | WEST)
                );
                assemblePiece(
                        templateQuads, quads, rot2, false,
                        vec3(2, 12 + offsetWall, 7),
                        aabb(6, 3, 1).move(10, 13, 0),
                        cull(SOUTH | EAST | WEST)
                );
                assemblePiece(
                        templateQuads, quads, rot2, false,
                        vec3(6, 9 + offsetWall, 7),
                        aabb(2, 3, 1).move(14, 7, 0),
                        cull(UP | DOWN | SOUTH | EAST)
                );
                assemblePiece(
                        templateQuads, quads, rot2, false,
                        vec3(2, 6 + offsetWall, 7),
                        aabb(6, 3, 1).move(10, 0, 0),
                        cull(SOUTH | EAST | WEST)
                );
            }
        }

        return quads;
    }

}
