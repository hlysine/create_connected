package com.hlysine.create_connected.content.copycat.bytes;

import com.hlysine.create_connected.content.copycat.ISimpleCopycatModel;
import com.simibubi.create.content.decoration.copycat.CopycatModel;
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
import static com.hlysine.create_connected.content.copycat.bytes.CopycatByteBlock.*;

public class CopycatByteModel extends CopycatModel implements ISimpleCopycatModel {

    public CopycatByteModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    protected List<BakedQuad> getCroppedQuads(BlockState state, Direction side, RandomSource rand, BlockState material,
                                              ModelData wrappedData, RenderType renderType) {
        BakedModel model = getModelOf(material);
        List<BakedQuad> templateQuads = model.getQuads(material, side, rand, wrappedData, renderType);

        List<BakedQuad> quads = new ArrayList<>();

        for (CopycatByteBlock.Byte bite : allBytes){
            if (!state.getValue(byByte(bite))) continue;

            int offsetX = bite.x() ? 8 : 0;
            int offsetY = bite.y() ? 8 : 0;
            int offsetZ = bite.z() ? 8 : 0;

            assemblePiece(
                    templateQuads, quads, 0, false,
                    vec3(offsetX, offsetY, offsetZ),
                    aabb(4, 4, 4),
                    cull(UP | EAST | SOUTH)
            );
            assemblePiece(
                    templateQuads, quads, 0, false,
                    vec3(offsetX + 4, offsetY, offsetZ),
                    aabb(4, 4, 4).move(12, 0, 0),
                    cull(UP | WEST | SOUTH)
            );
            assemblePiece(
                    templateQuads, quads, 0, false,
                    vec3(offsetX, offsetY, offsetZ + 4),
                    aabb(4, 4, 4).move(0, 0, 12),
                    cull(UP | EAST | NORTH)
            );
            assemblePiece(
                    templateQuads, quads, 0, false,
                    vec3(offsetX + 4, offsetY, offsetZ + 4),
                    aabb(4, 4, 4).move(12, 0, 12),
                    cull(UP | WEST | NORTH)
            );
            assemblePiece(
                    templateQuads, quads, 0, false,
                    vec3(offsetX, offsetY + 4, offsetZ),
                    aabb(4, 4, 4).move(0, 12, 0),
                    cull(DOWN | EAST | SOUTH)
            );
            assemblePiece(
                    templateQuads, quads, 0, false,
                    vec3(offsetX + 4, offsetY + 4, offsetZ),
                    aabb(4, 4, 4).move(12, 12, 0),
                    cull(DOWN | WEST | SOUTH)
            );
            assemblePiece(
                    templateQuads, quads, 0, false,
                    vec3(offsetX, offsetY + 4, offsetZ + 4),
                    aabb(4, 4, 4).move(0, 12, 12),
                    cull(DOWN | EAST | NORTH)
            );
            assemblePiece(
                    templateQuads, quads, 0, false,
                    vec3(offsetX + 4, offsetY + 4, offsetZ + 4),
                    aabb(4, 4, 4).move(12, 12, 12),
                    cull(DOWN | WEST | NORTH)
            );
        }

        return quads;
    }

}
