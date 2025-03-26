package com.hlysine.create_connected;

import com.mojang.blaze3d.systems.RenderSystem;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum CCGuiTextures implements ScreenElement {

    SEQUENCER("sequencer", 173, 205),
    SEQUENCER_INSTRUCTION("sequencer", 0, 16, 162, 22),
    SEQUENCER_DELAY("sequencer", 0, 104, 162, 22),
    SEQUENCER_END("sequencer", 0, 126, 162, 22),
    SEQUENCER_EMPTY("sequencer", 0, 148, 162, 22),
    SEQUENCER_AWAIT("sequencer", 0, 206, 162, 22),
    ;

    public static final int FONT_COLOR = 0x575F7A;

    public final ResourceLocation location;
    public final int width;
    public final int height;
    public final int startX;
    public final int startY;

    CCGuiTextures(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    CCGuiTextures(int startX, int startY) {
        this("icons", startX * 16, startY * 16, 16, 16);
    }

    CCGuiTextures(String location, int startX, int startY, int width, int height) {
        this(CreateConnected.MODID, location, startX, startY, width, height);
    }

    CCGuiTextures(String namespace, String location, int startX, int startY, int width, int height) {
        this.location = new ResourceLocation(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }

    @OnlyIn(Dist.CLIENT)
    public void bind() {
        RenderSystem.setShaderTexture(0, location);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(location, x, y, startX, startY, width, height);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(graphics, c, x, y, startX, startY, width, height);
    }

}

