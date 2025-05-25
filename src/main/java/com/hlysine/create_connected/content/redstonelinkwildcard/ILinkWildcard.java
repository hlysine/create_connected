package com.hlysine.create_connected.content.redstonelinkwildcard;

import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import net.createmod.catnip.data.Couple;

public interface ILinkWildcard {
    boolean test(RedstoneLinkNetworkHandler.Frequency stack);

    default boolean isKeyValid(Couple<RedstoneLinkNetworkHandler.Frequency> key) {
        return !key.both(f -> f.getStack().getItem() instanceof ILinkWildcard); // disallow wildcards in both slots because it activates all frequencies
    }
}
