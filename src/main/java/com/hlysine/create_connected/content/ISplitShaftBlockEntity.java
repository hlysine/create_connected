package com.hlysine.create_connected.content;

import net.minecraft.core.Direction;

/**
 * Interface version of {@link com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity}.
 * Allows the rotation propagator to consider different speeds for different directions.
 */
public interface ISplitShaftBlockEntity {
    float getRotationSpeedModifier(Direction face);
}
