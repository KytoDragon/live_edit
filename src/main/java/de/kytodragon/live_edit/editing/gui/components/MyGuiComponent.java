package de.kytodragon.live_edit.editing.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.item.ItemStack;

public abstract class MyGuiComponent extends GuiComponent {

    public int x;
    public int y;
    public int width;
    public int height;

    public MyGuiComponent(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void renderBackground(PoseStack pose, float partialTick, int mouseX, int mouseY) {}
    public void renderForeground(PoseStack pose, float partialTick, int mouseX, int mouseY) {}
    public void renderOverlay(PoseStack pose, float partialTick, int mouseX, int mouseY) {}

    public boolean mouseClicked(double mouseX, double mouseY, int button, ItemStack carried) { return false; }
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) { return false; }
    public boolean mouseReleased(double mouseX, double mouseY, int button) { return false; }
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) { return false; }

    public boolean isInside(double x, double y) {
        return x >= this.x && y >= this.y && x < this.x + width  && y < this.y + height;
    }
}
