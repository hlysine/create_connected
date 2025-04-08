package com.hlysine.create_connected.mixin.sequencedgearshift;

import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Collection;

@Mixin(AbstractSimiScreen.class)
public interface AbstractSimiScreenAccessor {
    @Invoker
    void callRemoveWidgets(Collection<? extends GuiEventListener> widgets);
}
