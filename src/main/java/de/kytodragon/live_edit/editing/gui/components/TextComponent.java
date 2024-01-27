package de.kytodragon.live_edit.editing.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;

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
    public void renderBackground(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        minecraft.font.draw(pose, text, x, y, 0x404040);
    }
}
