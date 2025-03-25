package com.hlysine.create_connected;

import com.hlysine.create_connected.content.contraption.jukebox.JukeboxMovementBehaviour;
import com.hlysine.create_connected.content.contraption.noteblock.NoteBlockMovementBehaviour;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import net.minecraft.world.level.block.Blocks;

public class CCMovementBehaviours {
    public static void register() {
        MovementBehaviour.REGISTRY.register(Blocks.NOTE_BLOCK, new NoteBlockMovementBehaviour());
        MovementBehaviour.REGISTRY.register(Blocks.JUKEBOX, new JukeboxMovementBehaviour());
    }
}
