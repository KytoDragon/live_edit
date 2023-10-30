package de.kytodragon.live_edit.editing.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.kytodragon.live_edit.editing.gui.Texture;

public class Decal extends MyGuiComponent {

    private final Texture texture;

    public Decal(int x, int y, Texture texture) {
        super(x, y, texture.width(), texture.height());
        this.texture = texture;
    }

    @Override
    public void renderBackground(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        RenderSystem.disableDepthTest();
        texture.draw(this, pose, x, y);
    }
}
