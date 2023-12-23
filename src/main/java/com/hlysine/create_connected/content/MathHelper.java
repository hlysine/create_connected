package com.hlysine.create_connected.content;

import net.minecraft.core.Direction;

/**
 * Feature parity for functions that only exist in 1.20+
 */
public class MathHelper {
    public static Direction DirectionFromDelta(int pX, int pY, int pZ) {
        if (pX == 0) {
            if (pY == 0) {
                if (pZ > 0) {
                    return Direction.SOUTH;
                }

                if (pZ < 0) {
                    return Direction.NORTH;
                }
            } else if (pZ == 0) {
                if (pY > 0) {
                    return Direction.UP;
                }

                return Direction.DOWN;
            }
        } else if (pY == 0 && pZ == 0) {
            if (pX > 0) {
                return Direction.EAST;
            }

            return Direction.WEST;
        }

        return null;
    }
}
