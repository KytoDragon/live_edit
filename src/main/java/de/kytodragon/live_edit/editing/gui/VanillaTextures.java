package de.kytodragon.live_edit.editing.gui;

import de.kytodragon.live_edit.LiveEditMod;
import net.minecraft.resources.ResourceLocation;

public class VanillaTextures {
    public static final ResourceLocation TEXTURE_ID = new ResourceLocation(LiveEditMod.MODID, "textures/vanilla_gui.png");

    public static final Texture BACKGROUND_UPPER_LEFT = new Texture(TEXTURE_ID, 0, 0, 16, 16);
    public static final Texture BACKGROUND_UPPER = new Texture(TEXTURE_ID, 17, 0, 16, 16);
    public static final Texture BACKGROUND_UPPER_RIGHT = new Texture(TEXTURE_ID, 34, 0, 16, 16);
    public static final Texture BACKGROUND_LEFT = new Texture(TEXTURE_ID, 0, 17, 16, 16);
    public static final Texture BACKGROUND_MIDDLE = new Texture(TEXTURE_ID, 17, 17, 16, 16);
    public static final Texture BACKGROUND_RIGHT = new Texture(TEXTURE_ID, 34, 17, 16, 16);
    public static final Texture BACKGROUND_LOWER_LEFT = new Texture(TEXTURE_ID, 0, 34, 16, 16);
    public static final Texture BACKGROUND_LOWER = new Texture(TEXTURE_ID, 17, 34, 16, 16);
    public static final Texture BACKGROUND_LOWER_RIGHT = new Texture(TEXTURE_ID, 34, 34, 16, 16);

    public static final Texture EMPTY_SLOT = new Texture(TEXTURE_ID, 0, 51, 18, 18);
    public static final Texture RESULT_SLOT = new Texture(TEXTURE_ID, 19, 51, 26, 26);
    public static final Texture CLOCK = new Texture(TEXTURE_ID, 97, 33, 16, 16);
    public static final Texture ARROW_RIGHT = new Texture(TEXTURE_ID, 108, 0, 23, 16);
    public static final Texture ARROW_RIGHT_FILLED = new Texture(TEXTURE_ID, 108, 17, 23, 16);
    public static final Texture CHECKBOX_EMPTY = new Texture(TEXTURE_ID, 51, 89, 9, 9);
    public static final Texture CHECKBOX_FILLED = new Texture(TEXTURE_ID, 60, 89, 9, 9);
    public static final Texture SMITHING_SLOT = new Texture(TEXTURE_ID, 51, 33, 18, 18);
    public static final Texture SMITHING_HAMMER = new Texture(TEXTURE_ID, 51, 0, 32, 32);
    public static final Texture LARGE_PLUS = new Texture(TEXTURE_ID, 16, 85, 13, 13);
    public static final Texture BURN_EMPTY = new Texture(TEXTURE_ID, 70, 80, 14, 14);
    public static final Texture BURN_FILLED = new Texture(TEXTURE_ID, 85, 80, 14, 14);

    public static final Texture POTION_SLOT = new Texture(TEXTURE_ID, 51, 71, 18, 18);
    public static final Texture BREWING_PIPES = new Texture(TEXTURE_ID, 70, 54, 30, 25);
    public static final Texture ARROW_DOWN = new Texture(TEXTURE_ID, 0, 70, 7, 27);
    public static final Texture ARROW_DOWN_FILLED = new Texture(TEXTURE_ID, 8, 70, 7, 27);

    public static final Texture[] EXPERIENCE_ORBS = repeatTexture(0, 98, 16, 16, 11, 1);

    private static Texture[] repeatTexture(int x, int y, int width, int height, int columns, int rows) {
        Texture[] result = new Texture[columns * rows];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                result[row * columns + column] = new Texture(TEXTURE_ID, x + column * width, y + row * height, width, height);
            }
        }
        return result;
    }
}
