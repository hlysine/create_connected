package com.hlysine.create_connected.content.copycat;

import com.simibubi.create.content.decoration.copycat.CopycatModel;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraftforge.client.model.data.ModelData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hlysine.create_connected.content.copycat.CopycatWallBlock.byDirection;
import static com.hlysine.create_connected.content.copycat.ISimpleCopycatModel.MutableCullFace.*;

public class CopycatWallModel extends CopycatModel implements ISimpleCopycatModel {

    public CopycatWallModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    protected List<BakedQuad> getCroppedQuads(BlockState state, Direction side, RandomSource rand, BlockState material,
                                              ModelData wrappedData, RenderType renderType) {
        BakedModel model = getModelOf(material);
        List<BakedQuad> templateQuads = model.getQuads(material, side, rand, wrappedData, renderType);

        List<BakedQuad> quads = new ArrayList<>();

        boolean pole = state.getValue(WallBlock.UP);
        if (pole) {
            // Assemble piece by piece if the central pole exists

            // Assemble the central pole
            for (Direction direction : Iterate.horizontalDirections) {
                assemblePiece(templateQuads, quads,
                        aabb(4, 16, 4),
                        vec3(4, 0, 4),
                        cull(SOUTH | EAST),
                        (int) direction.toYRot(), false);
            }

            // Assemble the sides
            for (Direction direction : Iterate.horizontalDirections) {
                int rot = (int) direction.toYRot();
                switch (state.getValue(byDirection(direction))) {
                    case NONE -> {
                        continue;
                    }
                    case LOW -> {
                        assemblePiece(templateQuads, quads,
                                aabb(3, 7, 4),
                                vec3(5, 0, 12),
                                cull(UP | NORTH | EAST),
                                rot, false);
                        assemblePiece(templateQuads, quads,
                                aabb(3, 7, 4).move(13, 0, 0),
                                vec3(8, 0, 12),
                                cull(UP | NORTH | WEST),
                                rot, false);
                        assemblePiece(templateQuads, quads,
                                aabb(3, 7, 4).move(0, 9, 0),
                                vec3(5, 7, 12),
                                cull(DOWN | NORTH | EAST),
                                rot, false);
                        assemblePiece(templateQuads, quads,
                                aabb(3, 7, 4).move(13, 9, 0),
                                vec3(8, 7, 12),
                                cull(DOWN | NORTH | WEST),
                                rot, false);
                    }
                    case TALL -> {
                        assemblePiece(templateQuads, quads,
                                aabb(3, 16, 4),
                                vec3(5, 0, 12),
                                cull(NORTH | EAST),
                                rot, false);
                        assemblePiece(templateQuads, quads,
                                aabb(3, 16, 4).move(13, 0, 0),
                                vec3(8, 0, 12),
                                cull(NORTH | WEST),
                                rot, false);
                    }
                }
            }
        } else {
            // Use special logic if the central pole does not exist

            boolean tall = false;
            Map<Direction, WallSide> sides = new HashMap<>();
            for (Direction direction : Iterate.horizontalDirections) {
                WallSide wall = state.getValue(byDirection(direction));
                sides.put(direction, wall);
                if (wall == WallSide.TALL) tall = true;
            }

            // Special case: A straight panel
            if (sides.get(Direction.SOUTH) == sides.get(Direction.NORTH) &&
                    sides.get(Direction.EAST) == sides.get(Direction.WEST) &&
                    (sides.get(Direction.NORTH) == WallSide.NONE || sides.get(Direction.EAST) == WallSide.NONE)) {
                int rot = sides.get(Direction.SOUTH) == WallSide.NONE ? 90 : 0;

                if (!tall) {
                    assemblePiece(templateQuads, quads,
                            aabb(3, 7, 16),
                            vec3(5, 0, 0),
                            cull(UP | EAST),
                            rot, false);
                    assemblePiece(templateQuads, quads,
                            aabb(3, 7, 16).move(13, 0, 0),
                            vec3(8, 0, 0),
                            cull(UP | WEST),
                            rot, false);
                    assemblePiece(templateQuads, quads,
                            aabb(3, 7, 16).move(0, 9, 0),
                            vec3(5, 7, 0),
                            cull(DOWN | EAST),
                            rot, false);
                    assemblePiece(templateQuads, quads,
                            aabb(3, 7, 16).move(13, 9, 0),
                            vec3(8, 7, 0),
                            cull(DOWN | WEST),
                            rot, false);
                } else {
                    assemblePiece(templateQuads, quads,
                            aabb(3, 16, 16).move(0, 0, 0),
                            vec3(5, 0, 0),
                            cull(EAST),
                            rot, false);
                    assemblePiece(templateQuads, quads,
                            aabb(3, 16, 16).move(13, 0, 0),
                            vec3(8, 0, 0),
                            cull(WEST),
                            rot, false);
                }

                return quads;
            }

            Direction extendSide = sides.entrySet().stream().filter(s -> s.getValue() == WallSide.TALL).findFirst()
                    .or(() -> sides.entrySet().stream().filter(s -> s.getValue() == WallSide.LOW).findFirst())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            // Assemble the sides
            // One side will extend to the center
            if (extendSide != null)
                for (Direction direction : Iterate.horizontalDirections) {
                    int rot = (int) direction.toYRot();
                    boolean extend = extendSide == direction;
                    boolean cullEnd = !extend || (sides.get(direction.getOpposite()) == sides.get(direction));

                    switch (sides.get(direction)) {
                        case NONE -> {
                            continue;
                        }
                        case LOW -> {
                            assemblePiece(templateQuads, quads,
                                    aabb(3, 7, extend ? 11 : 5).move(0, 0, extend ? 5 : 11),
                                    vec3(5, 0, extend ? 5 : 11),
                                    cull(UP | (cullEnd ? NORTH : 0) | EAST),
                                    rot, false);
                            assemblePiece(templateQuads, quads,
                                    aabb(3, 7, extend ? 11 : 5).move(13, 0, extend ? 5 : 11),
                                    vec3(8, 0, extend ? 5 : 11),
                                    cull(UP | (cullEnd ? NORTH : 0) | WEST),
                                    rot, false);
                            assemblePiece(templateQuads, quads,
                                    aabb(3, 7, extend ? 11 : 5).move(0, 9, extend ? 5 : 11),
                                    vec3(5, 7, extend ? 5 : 11),
                                    cull(DOWN | (cullEnd ? NORTH : 0) | EAST),
                                    rot, false);
                            assemblePiece(templateQuads, quads,
                                    aabb(3, 7, extend ? 11 : 5).move(13, 9, extend ? 5 : 11),
                                    vec3(8, 7, extend ? 5 : 11),
                                    cull(DOWN | (cullEnd ? NORTH : 0) | WEST),
                                    rot, false);
                        }
                        case TALL -> {
                            assemblePiece(templateQuads, quads,
                                    aabb(3, 16, extend ? 11 : 5).move(0, 0, extend ? 5 : 11),
                                    vec3(5, 0, extend ? 5 : 11),
                                    cull((cullEnd ? NORTH : 0) | EAST),
                                    rot, false);
                            assemblePiece(templateQuads, quads,
                                    aabb(3, 16, extend ? 11 : 5).move(13, 0, extend ? 5 : 11),
                                    vec3(8, 0, extend ? 5 : 11),
                                    cull((cullEnd ? NORTH : 0) | WEST),
                                    rot, false);
                        }
                    }
                }
        }

        return quads;
    }

}
