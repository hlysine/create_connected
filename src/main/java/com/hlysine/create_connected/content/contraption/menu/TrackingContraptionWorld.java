package com.hlysine.create_connected.content.contraption.menu;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class TrackingContraptionWorld extends ContraptionWorld {
    public final Contraption contraption;
    public final BlockPos localPos;

    public TrackingContraptionWorld(Level world, Contraption contraption, BlockPos localPos) {
        super(world, contraption);
        this.contraption = contraption;
        this.localPos = localPos;
    }
}
