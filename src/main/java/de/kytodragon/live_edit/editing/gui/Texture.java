package de.kytodragon.live_edit.editing.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;

public record Texture (
    ResourceLocation texture_id,
    int startX,
    int startY,
    int width,
    int height) {

    public void draw(GuiComponent component, PoseStack pose, int x, int y) {
        RenderSystem.setShaderTexture(0, texture_id);

        component.blit(pose, x, y, startX, startY, width, height);
    }
}
