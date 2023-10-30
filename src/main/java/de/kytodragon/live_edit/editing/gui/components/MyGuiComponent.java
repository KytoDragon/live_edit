package de.kytodragon.live_edit.editing.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class MyGuiComponent extends GuiComponent {

    public int x;
    public int y;
    public int width;
    public int height;

    protected Minecraft minecraft;
    protected static MyGuiComponent focused;

    public List<MyGuiComponent> children = new ArrayList<>();
    public boolean has_focus;

    public MyGuiComponent(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.minecraft = Minecraft.getInstance();
    }

    public void renderBackground(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        pose.pushPose();
        pose.translate(x, y, 0);
        mouseX -= x;
        mouseY -= y;
        for (MyGuiComponent component : children) {
            component.renderBackground(pose, partialTick, mouseX, mouseY);
        }
        pose.popPose();
    }

    public void renderForeground(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        pose.pushPose();
        pose.translate(x, y, 0);
        mouseX -= x;
        mouseY -= y;
        for (MyGuiComponent component : children) {
            component.renderForeground(pose, partialTick, mouseX, mouseY);
        }
        pose.popPose();
    }

    public void renderOverlay(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        pose.pushPose();
        pose.translate(x, y, 0);
        mouseX -= x;
        mouseY -= y;
        for (MyGuiComponent component : children) {
            component.renderOverlay(pose, partialTick, mouseX, mouseY);
        }
        pose.popPose();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, ItemStack carried) {
        if (!isInside(mouseX, mouseY))
            return false;

        mouseX -= x;
        mouseY -= y;
        for (MyGuiComponent component : children) {
            if (component.mouseClicked(mouseX, mouseY, button, carried))
                return true;
        }
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!isInside(mouseX, mouseY))
            return false;

        mouseX -= x;
        mouseY -= y;
        for (MyGuiComponent component : children) {
            if (component.mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
                return true;
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!isInside(mouseX, mouseY))
            return false;

        mouseX -= x;
        mouseY -= y;
        for (MyGuiComponent component : children) {
            if (component.mouseReleased(mouseX, mouseY, button))
                return true;
        }
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (!isInside(mouseX, mouseY))
            return false;

        mouseX -= x;
        mouseY -= y;
        for (MyGuiComponent component : children) {
            if (component.mouseScrolled(mouseX, mouseY, scroll))
                return true;
        }
        return false;
    }

    public boolean keyPressed(int key, int scancode, int unknown) {
        for (MyGuiComponent component : children) {
            if (component.keyPressed(key, scancode, unknown))
                return true;
        }
        return false;
    }

    public boolean charTyped(char character, int scancode) {
        for (MyGuiComponent component : children) {
            if (component.charTyped(character, scancode))
                return true;
        }
        return false;
    }

    public boolean isInside(double x, double y) {
        return x >= this.x && y >= this.y && x < this.x + width  && y < this.y + height;
    }

    protected void setFocused(boolean focus) {
        has_focus = focus;
    }

    protected static void setFocusOn(MyGuiComponent component) {
        if (focused == null && component != null) {
            component.has_focus = true;
            focused = component;
        } else if (focused != component) {
            focused.setFocused(false);
            if (component != null)
                component.has_focus = true;
            focused = component;
        }
    }
}
