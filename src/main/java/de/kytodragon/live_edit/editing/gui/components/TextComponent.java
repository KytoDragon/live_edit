package de.kytodragon.live_edit.editing.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.font.FontSet;

public class TextComponent extends MyGuiComponent {

    private String text;

    public TextComponent(int x, int y, String text) {
        super(x, y, 0, 0);
        this.width = minecraft.font.width(text);
        this.height = minecraft.font.lineHeight;
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.drawString(minecraft.font, text, x, y, 0x404040, false);
    }
}
