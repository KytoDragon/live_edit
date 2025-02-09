package de.kytodragon.live_edit.editing.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class MyGuiComponent {

    public int x;
    public int y;
    public int width;
    public int height;

    protected Minecraft minecraft;
    protected static MyGuiComponent focused;

    public static MyGuiComponent popup;

    public List<MyGuiComponent> children = new ArrayList<>();
    public boolean has_focus;
    public boolean is_visible = true;

    public MyGuiComponent parent;
    public boolean propagate_size_change;

    public MyGuiComponent(int x, int y) {
        this(x, y, -1, -1);
    }

    public MyGuiComponent(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.minecraft = Minecraft.getInstance();
    }

    public void renderBackground(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        mouseX -= x;
        mouseY -= y;
        for (MyGuiComponent component : children) {
            if (component.is_visible)
                component.renderBackground(graphics, partialTick, mouseX, mouseY);
        }
        graphics.pose().popPose();
    }

    public void renderForeground(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        mouseX -= x;
        mouseY -= y;
        for (MyGuiComponent component : children) {
            if (component.is_visible)
                component.renderForeground(graphics, partialTick, mouseX, mouseY);
        }
        graphics.pose().popPose();
    }

    public void renderOverlay(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        mouseX -= x;
        mouseY -= y;
        for (MyGuiComponent component : children) {
            if (component.is_visible)
                component.renderOverlay(graphics, partialTick, mouseX, mouseY);
        }
        graphics.pose().popPose();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int mouse_button, ItemStack carried) {
        if (!isInside(mouseX, mouseY))
            return false;

        mouseX -= x;
        mouseY -= y;
        for (MyGuiComponent component : children) {
            if (component.is_visible && component.mouseClicked(mouseX, mouseY, mouse_button, carried))
                return true;
        }
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int mouse_button, double deltaX, double deltaY) {
        if (!isInside(mouseX, mouseY))
            return false;

        mouseX -= x;
        mouseY -= y;
        for (MyGuiComponent component : children) {
            if (component.is_visible && component.mouseDragged(mouseX, mouseY, mouse_button, deltaX, deltaY))
                return true;
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int mouse_button) {
        if (!isInside(mouseX, mouseY))
            return false;

        mouseX -= x;
        mouseY -= y;
        for (MyGuiComponent component : children) {
            if (component.is_visible && component.mouseReleased(mouseX, mouseY, mouse_button))
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
            if (component.is_visible && component.mouseScrolled(mouseX, mouseY, scroll))
                return true;
        }
        return false;
    }

    public boolean keyPressed(int key, int scancode, int unknown) {

        for (MyGuiComponent component : children) {
            if (component.is_visible && component.keyPressed(key, scancode, unknown))
                return true;
        }
        return false;
    }

    public boolean charTyped(char character, int scancode) {

        for (MyGuiComponent component : children) {
            if (component.is_visible && component.charTyped(character, scancode))
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

    public static void setFocusOn(MyGuiComponent component) {
        if (popup != null) {
            if (popup == component) {
                return;
            } else {
                popup = null;
            }
        }
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

    public void tick() {
        boolean size_change = propagate_size_change;
        for (MyGuiComponent component : children) {
            component.tick();
            size_change |= component.propagate_size_change;
        }

        propagate_size_change = size_change;
    }

    public void calculateBounds() {
        for (MyGuiComponent component : children) {
            component.calculateBounds();
        }
        if (width == -1 && height == -1) {
            width = 0;
            height = 0;
            for (MyGuiComponent component : children) {
                if (component.is_visible) {
                    this.width = Math.max(width, component.x + component.width);
                    this.height = Math.max(height, component.y + component.height);
                }
            }
        }
        propagate_size_change = false;
    }

    public static void setPopup(MyGuiComponent popup, int x, int y) {
        MyGuiComponent.popup = popup;
        if (focused != null && focused != popup) {
            focused.setFocused(false);
            focused = null;
        }
        for (MyGuiComponent parent = popup.parent; parent != null; parent = parent.parent) {
            x += parent.x;
            if (parent instanceof ScrolledListPanel panel) {
                y += (int)panel.getContentYOffset();
            } else {
                y += parent.y;
            }
        }
        popup.x = x;
        popup.y = y;
    }

    public void addChild(MyGuiComponent child) {
        children.add(child);
        child.parent = this;
    }
}
