package de.kytodragon.live_edit.editing.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;

public class Background extends MyGuiComponent {

    public Background(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void renderBackground(PoseStack pose, float partialTick, int mouseX, int mouseY) {

        for (int y = 0; y < height; y += 16) {
            for (int x = 0; x < width; x += 16) {
                Texture texture;
                if (y == 0) {
                    if (x == 0) {
                        texture = VanillaTextures.BACKGROUND_UPPER_LEFT;
                    } else if (x + 16 >= width) {
                        texture = VanillaTextures.BACKGROUND_UPPER_RIGHT;
                    } else {
                        texture = VanillaTextures.BACKGROUND_UPPER;
                    }
                } else if (y + 16 >= height) {
                    if (x == 0) {
                        texture = VanillaTextures.BACKGROUND_LOWER_LEFT;
                    } else if (x + 16 >= width) {
                        texture = VanillaTextures.BACKGROUND_LOWER_RIGHT;
                    } else {
                        texture = VanillaTextures.BACKGROUND_LOWER;
                    }
                } else {
                    if (x == 0) {
                        texture = VanillaTextures.BACKGROUND_LEFT;
                    } else if (x + 16 >= width) {
                        texture = VanillaTextures.BACKGROUND_RIGHT;
                    } else {
                        texture = VanillaTextures.BACKGROUND_MIDDLE;
                    }
                }
                texture.draw(this, pose, this.x + x, this.y + y);
            }
        }
    }

}
