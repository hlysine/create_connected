package com.hlysine.create_connected;

import com.hlysine.create_connected.content.contraption.jukebox.JukeboxInteractionBehaviour;
import com.hlysine.create_connected.content.contraption.menu.MenuBlockInteractionBehaviour;
import com.hlysine.create_connected.content.contraption.noteblock.NoteBlockInteractionBehaviour;
import com.simibubi.create.AllInteractionBehaviours;
import net.minecraft.world.level.block.Blocks;

public class CCInteractionBehaviours {
    public static void register() {
        AllInteractionBehaviours.registerBehaviour(Blocks.NOTE_BLOCK, new NoteBlockInteractionBehaviour());
        AllInteractionBehaviours.registerBehaviour(Blocks.JUKEBOX, new JukeboxInteractionBehaviour());
        AllInteractionBehaviours.registerBehaviour(Blocks.CRAFTING_TABLE, new MenuBlockInteractionBehaviour());
        AllInteractionBehaviours.registerBehaviour(Blocks.STONECUTTER, new MenuBlockInteractionBehaviour());
        AllInteractionBehaviours.registerBehaviour(Blocks.GRINDSTONE, new MenuBlockInteractionBehaviour());
        AllInteractionBehaviours.registerBehaviour(Blocks.SMITHING_TABLE, new MenuBlockInteractionBehaviour());
        AllInteractionBehaviours.registerBehaviour(Blocks.LOOM, new MenuBlockInteractionBehaviour());
        AllInteractionBehaviours.registerBehaviour(Blocks.CARTOGRAPHY_TABLE, new MenuBlockInteractionBehaviour());
    }
}
