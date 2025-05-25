package com.hlysine.create_connected.content.redstonelinkwildcard;

import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import net.minecraft.world.item.Item;

public class RedstoneLinkWildcardItem extends Item implements ILinkWildcard {
    public RedstoneLinkWildcardItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean test(RedstoneLinkNetworkHandler.Frequency stack) {
        return true;
    }
}
