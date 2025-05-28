package com.hlysine.create_connected.content.redstonelinkwildcard;

import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import net.createmod.catnip.data.Couple;

public interface ILinkWildcard {
    boolean test(RedstoneLinkNetworkHandler.Frequency stack);
}
