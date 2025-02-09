package de.kytodragon.live_edit.editing.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;

public class Decal extends MyGuiComponent {

    private final Texture texture;

    public Decal(int x, int y, Texture texture) {
        super(x, y, texture.width(), texture.height());
        this.texture = texture;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.disableDepthTest();
        texture.draw(graphics, x, y);
    }
}
