package com.hlysine.create_connected;

import com.hlysine.create_connected.content.contraption.jukebox.JukeboxInteractionBehaviour;
import com.hlysine.create_connected.content.contraption.menu.MenuBlockInteractionBehaviour;
import com.hlysine.create_connected.content.contraption.noteblock.NoteBlockInteractionBehaviour;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import net.minecraft.world.level.block.Blocks;

public class CCInteractionBehaviours {
    public static void register() {
        MovingInteractionBehaviour.REGISTRY.register(Blocks.NOTE_BLOCK, new NoteBlockInteractionBehaviour());
        MovingInteractionBehaviour.REGISTRY.register(Blocks.JUKEBOX, new JukeboxInteractionBehaviour());
        MovingInteractionBehaviour.REGISTRY.register(Blocks.CRAFTING_TABLE, new MenuBlockInteractionBehaviour());
        MovingInteractionBehaviour.REGISTRY.register(Blocks.STONECUTTER, new MenuBlockInteractionBehaviour());
        MovingInteractionBehaviour.REGISTRY.register(Blocks.GRINDSTONE, new MenuBlockInteractionBehaviour());
        MovingInteractionBehaviour.REGISTRY.register(Blocks.SMITHING_TABLE, new MenuBlockInteractionBehaviour());
        MovingInteractionBehaviour.REGISTRY.register(Blocks.LOOM, new MenuBlockInteractionBehaviour());
        MovingInteractionBehaviour.REGISTRY.register(Blocks.CARTOGRAPHY_TABLE, new MenuBlockInteractionBehaviour());
    }
}
