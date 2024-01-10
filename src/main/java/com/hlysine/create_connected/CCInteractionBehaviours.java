package com.hlysine.create_connected;

import com.hlysine.create_connected.content.contraption.jukebox.JukeboxInteractionBehaviour;
import com.hlysine.create_connected.content.contraption.noteblock.NoteBlockInteractionBehaviour;
import com.simibubi.create.AllInteractionBehaviours;
import net.minecraft.world.level.block.Blocks;

public class CCInteractionBehaviours {
    public static void register() {
        AllInteractionBehaviours.registerBehaviour(Blocks.NOTE_BLOCK, new NoteBlockInteractionBehaviour());
        AllInteractionBehaviours.registerBehaviour(Blocks.JUKEBOX, new JukeboxInteractionBehaviour());
    }
}
