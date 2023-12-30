package com.hlysine.create_connected;

import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CCShapes {
    public static final VoxelShaper CASING_8PX = shape(0, 0, 0, 16, 8, 16).forAxis();
    public static final VoxelShaper CASING_8PX_TOP = shape(0, 8, 0, 16, 16, 16).forAxis();
    public static final VoxelShaper CASING_8PX_CENTERED = shape(4, 0, 4, 12, 16, 12).forAxis();
    public static final VoxelShaper CASING_8PX_VERTICAL = shape(0, 0, 0, 8, 16, 8).forHorizontal(Direction.NORTH);
    public static final VoxelShaper WALL_LINKED_MODULE = shape(11, 2, 0, 16, 14, 1).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper FLOOR_LINKED_MODULE = shape(0, 0, 2, 5, 1, 14).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper CEILING_LINKED_MODULE = shape(0, 15, 2, 5, 16, 14).forHorizontal(Direction.SOUTH);

    private static AllShapes.Builder shape(VoxelShape shape) {
        return new AllShapes.Builder(shape);
    }

    private static AllShapes.Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return shape(cuboid(x1, y1, z1, x2, y2, z2));
    }

    private static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box(x1, y1, z1, x2, y2, z2);
    }
}
