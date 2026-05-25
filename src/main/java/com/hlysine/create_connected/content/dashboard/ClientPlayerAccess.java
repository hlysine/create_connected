package com.hlysine.create_connected.content.dashboard;

import net.createmod.catnip.annotations.ClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

@ClientOnly
public class ClientPlayerAccess {
    public static Player getPlayer() {
        return Minecraft.getInstance().player;
    }
}
