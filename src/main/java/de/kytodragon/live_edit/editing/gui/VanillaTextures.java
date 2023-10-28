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
}
