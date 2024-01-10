package com.hlysine.create_connected;

import com.hlysine.create_connected.content.noteblock.NoteBlockMovementBehaviour;
import com.simibubi.create.AllMovementBehaviours;
import net.minecraft.world.level.block.Blocks;

public class CCMovementBehaviours {
    public static void register() {
        AllMovementBehaviours.registerBehaviour(Blocks.NOTE_BLOCK, new NoteBlockMovementBehaviour());
    }
}
