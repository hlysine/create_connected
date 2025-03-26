package com.hlysine.create_connected.content.copycat.fence;

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
import static com.hlysine.create_connected.content.copycat.fence.CopycatFenceBlock.byDirection;

public class CopycatFenceModel extends CopycatModel implements ISimpleCopycatModel {

    public CopycatFenceModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    protected List<BakedQuad> getCroppedQuads(BlockState state, Direction side, RandomSource rand, BlockState material,
                                              ModelData wrappedData, RenderType renderType) {
        BakedModel model = getModelOf(material);
        List<BakedQuad> templateQuads = model.getQuads(material, side, rand, wrappedData, renderType);

        List<BakedQuad> quads = new ArrayList<>();

        for (Direction direction : Iterate.horizontalDirections) {
            assemblePiece(templateQuads, quads, (int) direction.toYRot(), false,
                    vec3(6, 0, 6),
                    aabb(2, 16, 2),
                    cull(SOUTH | EAST)
            );
        }

        for (Direction direction : Iterate.horizontalDirections) {
            if (!state.getValue(byDirection(direction))) continue;

            int rot = (int) direction.toYRot();
            assemblePiece(templateQuads, quads, rot, false,
                    vec3(7, 6, 10),
                    aabb(1, 1, 6),
                    cull(UP | NORTH | EAST)
            );
            assemblePiece(templateQuads, quads, rot, false,
                    vec3(8, 6, 10),
                    aabb(1, 1, 6).move(15, 0, 0),
                    cull(UP | NORTH | WEST)
            );
            assemblePiece(templateQuads, quads, rot, false,
                    vec3(7, 7, 10),
                    aabb(1, 2, 6).move(0, 14, 0),
                    cull(DOWN | NORTH | EAST)
            );
            assemblePiece(templateQuads, quads, rot, false,
                    vec3(8, 7, 10),
                    aabb(1, 2, 6).move(15, 14, 0),
                    cull(DOWN | NORTH | WEST)
            );

            assemblePiece(templateQuads, quads, rot, false,
                    vec3(7, 12, 10),
                    aabb(1, 1, 6),
                    cull(UP | NORTH | EAST)
            );
            assemblePiece(templateQuads, quads, rot, false,
                    vec3(8, 12, 10),
                    aabb(1, 1, 6).move(15, 0, 0),
                    cull(UP | NORTH | WEST)
            );
            assemblePiece(templateQuads, quads, rot, false,
                    vec3(7, 13, 10),
                    aabb(1, 2, 6).move(0, 14, 0),
                    cull(DOWN | NORTH | EAST)
            );
            assemblePiece(templateQuads, quads, rot, false,
                    vec3(8, 13, 10),
                    aabb(1, 2, 6).move(15, 14, 0),
                    cull(DOWN | NORTH | WEST)
            );
        }

        return quads;
    }

}
