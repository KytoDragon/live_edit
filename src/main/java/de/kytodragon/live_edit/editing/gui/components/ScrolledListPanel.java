package de.kytodragon.live_edit.editing.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Vector4f;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.item.ItemStack;

/**
 * Copy of ScrollPanel that actually works
 */
public class ScrolledListPanel extends MyGuiComponent {

    private boolean scrolling;
    protected float scrollDistance;

    private static final int bgColorFrom = 0xFFB0B0B0;
    private static final int bgColorTo = 0xFFB0B0B0;
    private static final int barBgColor = 0xFF000000;
    private static final int barColor = 0xFF808080;
    private static final int barBorderColor = 0xFFC0C0C0;
    private static final int border = 4;
    private static final int barWidth = 6;
    private final int barLeft;

    public ScrolledListPanel(int x, int y, int width, int height) {
        super(x, y, width, height);
        barLeft = x + width - barWidth;
    }

    @Override
    public void renderBackground(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        scissorWithPose(pose, x, y, width, height);

        this.fillGradient(pose, x, y, x+width, y+height, bgColorFrom, bgColorTo);

        pose.pushPose();
        pose.translate(x, y + border - (int)scrollDistance, 0);
        mouseX -= x;
        mouseY -= y + border - (int)scrollDistance;
        for (MyGuiComponent component : children) {
            component.renderBackground(pose, partialTick, mouseX, mouseY);
        }
        pose.popPose();

        RenderSystem.disableScissor();
    }

    @Override
    public void renderForeground(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        scissorWithPose(pose, x, y, width, height);

        pose.pushPose();
        pose.translate(x, y + border - (int)scrollDistance, 0);
        mouseX -= x;
        mouseY -= y + border - (int)scrollDistance;
        for (MyGuiComponent component : children) {
            component.renderForeground(pose, partialTick, mouseX, mouseY);
        }
        pose.popPose();

        RenderSystem.disableScissor();

        int extraHeight = (getContentHeight() + border) - height;
        if (extraHeight > 0) {
            int barHeight = getBarHeight();
            int barTop = (int)scrollDistance * (height - barHeight) / extraHeight + y;
            if (barTop < y) {
                barTop = y;
            }

            GuiComponent.fill(pose, barLeft, y, barLeft + barWidth, y+height, barBgColor);
            GuiComponent.fill(pose, barLeft, barTop, barLeft + barWidth, barTop + barHeight, barColor);
            GuiComponent.fill(pose, barLeft, barTop, barLeft + barWidth - 1, barTop + barHeight - 1, barBorderColor);
        }
    }

    @Override
    public void renderOverlay(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        if (!isInside(mouseX, mouseY))
            return;

        pose.pushPose();
        pose.translate(x, y + border - (int)scrollDistance, 0);
        mouseX -= x;
        mouseY -= y + border - (int)scrollDistance;
        for (MyGuiComponent component : children) {
            component.renderOverlay(pose, partialTick, mouseX, mouseY);
        }
        pose.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, ItemStack carried) {
        if (!isInside(mouseX, mouseY))
            return false;

        scrolling = button == 0 && mouseX >= barLeft && mouseX < barLeft + barWidth;
        if (scrolling) {
            return true;
        }

        int mouseListY = ((int)mouseY) - y - getContentHeight() + (int)scrollDistance - border;
        if (mouseX >= x && mouseX <= x + width && mouseListY < 0) {
            mouseX -= x;
            mouseY -= y - scrollDistance + border;
            for (MyGuiComponent component : children) {
                if (component.mouseClicked(mouseX, mouseY, button, carried))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!isInside(mouseX, mouseY))
            return false;

        if (scrolling) {
            int maxScroll = height - getBarHeight();
            double moved = deltaY / maxScroll;
            scrollDistance += (float) (getMaxScroll() * moved);
            applyScrollLimits();
            return true;
        }

        int mouseListY = ((int)mouseY) - y - getContentHeight() + (int)scrollDistance - border;
        if (mouseX >= x && mouseX <= x + width && mouseListY < 0) {
            mouseX -= x;
            mouseY -= y - scrollDistance + border;
            for (MyGuiComponent component : children) {
                if (component.mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!isInside(mouseX, mouseY))
            return false;

        if (super.mouseReleased(mouseX, mouseY, button))
            return true;
        if (scrolling) {
            scrolling = false;
            return true;
        }

        int mouseListY = ((int)mouseY) - y - getContentHeight() + (int)scrollDistance - border;
        if (mouseX >= x && mouseX <= x + width && mouseListY < 0) {
            mouseX -= x;
            mouseY -= y - scrollDistance + border;
            for (MyGuiComponent component : children) {
                if (component.mouseReleased(mouseX, mouseY, button))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (!isInside(mouseX, mouseY))
            return false;

        if (scroll != 0) {
            scrollDistance += (float) (-scroll * getScrollAmount());
            applyScrollLimits();
            return true;
        }

        int mouseListY = ((int)mouseY) - y - getContentHeight() + (int)scrollDistance - border;
        if (mouseX >= x && mouseX <= x + width && mouseListY < 0) {
            mouseX -= x;
            mouseY -= y - scrollDistance + border;
            for (MyGuiComponent component : children) {
                if (component.mouseScrolled(mouseX, mouseY, scroll))
                    return true;
            }
        }
        return false;
    }

    /**
     * Scissor with respect to GUI-Scale and current offset.
     */
    private void scissorWithPose(PoseStack pose, int x, int y, int width, int height) {
        double scale = minecraft.getWindow().getGuiScale();
        Vector4f scissorPos = new Vector4f(x, y, 0, 1);
        scissorPos.transform(pose.last().pose());
        RenderSystem.enableScissor((int)(scissorPos.x() * scale), (int)(minecraft.getWindow().getHeight() - ((scissorPos.y() + height) * scale)),
            (int)(width * scale), (int)(height * scale));
    }

    private int getContentHeight() {
        int height = 0;
        for (MyGuiComponent component : children) {
            height = Math.max(height, component.y + component.height);
        }
        return height;
    }

    private int getBarHeight() {
        int barHeight = (height * height) / getContentHeight();

        if (barHeight < 32) barHeight = 32;

        if (barHeight > height - border*2)
            barHeight = height - border*2;

        return barHeight;
    }

    private int getMaxScroll() {
        return getContentHeight() - (height - border);
    }

    private void applyScrollLimits() {
        int max = getMaxScroll();
        if (max < 0)
            max /= 2;

        if (scrollDistance < 0.0F)
            scrollDistance = 0.0F;
        if (scrollDistance > max)
            scrollDistance = max;
    }

    private int getScrollAmount() {
        return 16;
    }
}
