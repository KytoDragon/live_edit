package de.kytodragon.live_edit.editing.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.ArrayList;
import java.util.List;

public abstract class GuiCommon<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    protected final List<MyGuiComponent> components = new ArrayList<>();

    public GuiCommon(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.leftPos = 0;
        this.topPos = 0;
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        super.renderBackground(pose);
        super.render(pose, mouseX, mouseY, partialTick);

        RenderSystem.disableDepthTest();
        pose.pushPose();
        pose.translate(this.leftPos, this.topPos, 0);
        for (MyGuiComponent component : components) {
            component.renderForeground(pose, partialTick, mouseX - this.leftPos, mouseY - this.topPos);
        }
        pose.popPose();

        super.renderTooltip(pose, mouseX, mouseY);

        pose.pushPose();
        pose.translate(this.leftPos, this.topPos, 0);
        for (MyGuiComponent component : components) {
            component.renderOverlay(pose, partialTick, mouseX - this.leftPos, mouseY - this.topPos);
        }
        pose.popPose();
    }

    @Override
    protected void renderBg(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        RenderSystem.disableDepthTest();
        pose.pushPose();
        pose.translate(this.leftPos, this.topPos, 0);

        mouseX -= this.leftPos;
        mouseY -= this.topPos;
        for (MyGuiComponent component : components) {
            component.renderBackground(pose, partialTick, mouseX, mouseY);
        }

        pose.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouse_button) {
        super.mouseClicked(mouseX, mouseY, mouse_button);

        mouseX -= this.leftPos;
        mouseY -= this.topPos;
        for (MyGuiComponent component : components) {
            if (component.mouseClicked(mouseX, mouseY, mouse_button, menu.getCarried()))
                return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouse_button, double deltaX, double deltaY) {
        super.mouseDragged(mouseX, mouseY, mouse_button, deltaX, deltaY);

        mouseX -= this.leftPos;
        mouseY -= this.topPos;
        for (MyGuiComponent component : components) {
            if (component.mouseDragged(mouseX, mouseY, mouse_button, deltaX, deltaY))
                return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouse_button) {
        super.mouseReleased(mouseX, mouseY, mouse_button);

        mouseX -= this.leftPos;
        mouseY -= this.topPos;
        for (MyGuiComponent component : components) {
            if (component.mouseReleased(mouseX, mouseY, mouse_button))
                return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (super.mouseScrolled(mouseX, mouseY, scroll))
            return true;

        mouseX -= this.leftPos;
        mouseY -= this.topPos;
        for (MyGuiComponent component : components) {
            if (component.mouseScrolled(mouseX, mouseY, scroll))
                return true;
        }

        return false;
    }

    @Override
    public boolean keyPressed(int key, int scancode, int unknown) {
        if (super.keyPressed(key, scancode, unknown))
            return true;

        for (MyGuiComponent component : components) {
            if (component.keyPressed(key, scancode, unknown))
                return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char character, int scancode) {
        if (super.charTyped(character, scancode))
            return true;

        for (MyGuiComponent component : components) {
            if (component.charTyped(character, scancode))
                return true;
        }
        return false;
    }

    @Override
    protected void containerTick() {
        for (MyGuiComponent component : components) {
            component.tick();
            if (component.propagate_size_change) {
                component.calculateBounds();
            }
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        MyGuiComponent.setFocusOn(null);
    }
}
