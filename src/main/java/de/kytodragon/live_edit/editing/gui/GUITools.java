package de.kytodragon.live_edit.editing.gui;

import net.minecraft.client.Minecraft;

public class GUITools {

    public static int getScreenWidth() {
        return (int)(Minecraft.getInstance().getWindow().getWidth() / Minecraft.getInstance().getWindow().getGuiScale());
    }

    public static int getScreenHeight() {
        return (int)(Minecraft.getInstance().getWindow().getHeight() / Minecraft.getInstance().getWindow().getGuiScale());
    }
}
