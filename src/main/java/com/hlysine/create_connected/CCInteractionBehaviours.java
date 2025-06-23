package com.hlysine.create_connected;

import com.hlysine.create_connected.content.contraption.jukebox.JukeboxInteractionBehaviour;
import com.hlysine.create_connected.content.contraption.menu.MenuBlockInteractionBehaviour;
import com.hlysine.create_connected.content.contraption.noteblock.NoteBlockInteractionBehaviour;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class CCInteractionBehaviours {
    public static void register() {
        registerOptional(Blocks.NOTE_BLOCK, new NoteBlockInteractionBehaviour());
        registerOptional(Blocks.JUKEBOX, new JukeboxInteractionBehaviour());
        registerOptional(Blocks.CRAFTING_TABLE, new MenuBlockInteractionBehaviour());
        registerOptional(Blocks.STONECUTTER, new MenuBlockInteractionBehaviour());
        registerOptional(Blocks.GRINDSTONE, new MenuBlockInteractionBehaviour());
        registerOptional(Blocks.SMITHING_TABLE, new MenuBlockInteractionBehaviour());
        registerOptional(Blocks.LOOM, new MenuBlockInteractionBehaviour());
        registerOptional(Blocks.CARTOGRAPHY_TABLE, new MenuBlockInteractionBehaviour());
    }

    private static void registerOptional(Block block, MovingInteractionBehaviour behavior) {
        if (MovingInteractionBehaviour.REGISTRY.get(block) == null) {
            MovingInteractionBehaviour.REGISTRY.register(block, behavior);
        }
    }
}