package com.hlysine.create_connected.content.copycat;

import com.simibubi.create.content.decoration.copycat.CopycatModel;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import java.util.ArrayList;
import java.util.List;

import static com.hlysine.create_connected.content.copycat.CopycatBoardBlock.byDirection;

public class CopycatBoardModel extends CopycatModel implements ISimpleCopycatModel {

    public CopycatBoardModel(BakedModel originalModel) {
        super(originalModel);
    }

    private static final float EPSILON = 0.01f;

    @Override
    protected List<BakedQuad> getCroppedQuads(BlockState state, Direction side, RandomSource rand, BlockState material,
                                              ModelData wrappedData, RenderType renderType) {
        BakedModel model = getModelOf(material);
        List<BakedQuad> templateQuads = model.getQuads(material, side, rand, wrappedData, renderType);

        List<BakedQuad> quads = new ArrayList<>();

        for (Direction direction : Iterate.directions) {
            if (state.getValue(byDirection(direction.getOpposite())))
                if (direction.getAxis().isVertical()) {
                    assemblePiece(templateQuads, quads,
                            aabb(16, 1, 16),
                            vec3(0, 0, 0),
                            cull(0),
                            0, direction == Direction.DOWN);
                } else {
                    assemblePiece(templateQuads, quads,
                            aabb(16, 16, 1),
                            vec3(2 * EPSILON, direction.getAxis() == Direction.Axis.Z ? EPSILON : -EPSILON, EPSILON),
                            cull(0),
                            (int) direction.toYRot(), false);
                }
        }

        return quads;
    }

}
