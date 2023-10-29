package de.kytodragon.live_edit.editing.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;

public class TextComponent extends MyGuiComponent {

    private final String text;
    private final Font font;

    public TextComponent(int x, int y, Font font, String text) {
        super(x, y, font.width(text), font.lineHeight);
        this.font = font;
        this.text = text;
    }

    @Override
    public void renderBackground(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        font.draw(pose, text, x, y, 0x404040);
    }
}
