package de.kytodragon.live_edit.editing.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public record Texture (
    ResourceLocation texture_id,
    int startX,
    int startY,
    int width,
    int height) {

    public void draw(GuiGraphics graphics, int x, int y) {
        graphics.blit(texture_id, x, y, startX, startY, width, height);
    }
}
