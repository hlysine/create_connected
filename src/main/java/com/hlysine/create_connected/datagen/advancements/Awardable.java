package com.hlysine.create_connected.datagen.advancements;

import net.minecraft.world.entity.player.Player;

public interface Awardable {
    void awardTo(Player player);

    boolean isAlreadyAwardedTo(Player player);
}
