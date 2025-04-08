package com.hlysine.create_connected.content.shearpin;

import com.hlysine.create_connected.CCPartialModels;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;

public class ShearPinVisual extends SingleAxisRotatingVisual<ShearPinBlockEntity> {
    public ShearPinVisual(VisualizationContext context, ShearPinBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Models.partial(CCPartialModels.SHEAR_PIN));
    }
}
