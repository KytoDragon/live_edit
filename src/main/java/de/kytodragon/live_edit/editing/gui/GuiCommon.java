package de.kytodragon.live_edit.editing.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.kytodragon.live_edit.LiveEditMod;
import de.kytodragon.live_edit.editing.gui.components.Background;
import de.kytodragon.live_edit.editing.gui.components.ComponentGroup;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class GuiCommon<T extends MenuCommon> extends AbstractContainerScreen<T> {

    protected final MyGuiComponent content = new ComponentGroup(0, 0);

    public GuiCommon(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.leftPos = 0;
        this.topPos = 0;
        this.imageWidth = menu.imageWidth;
        this.imageHeight = menu.imageHeight;

        this.inventoryLabelY = this.imageHeight - 94;
        // TODO inventoryLabelX

        content.addChild(new Background(0, 0, this.imageWidth, this.imageHeight));
        content.addChild(menu.inventoryGui);
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        content.x = this.leftPos;
        content.y = this.topPos;

        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        super.renderBackground(pose);
        super.render(pose, mouseX, mouseY, partialTick);

        RenderSystem.disableDepthTest();
        content.renderForeground(pose, partialTick, mouseX, mouseY);

        super.renderTooltip(pose, mouseX, mouseY);

        content.renderOverlay(pose, partialTick, mouseX, mouseY);

        if (MyGuiComponent.popup != null) {
            pose.pushPose();
            // Text with shadow gets draw slightly above the current Z, translate to make sure the popup is above them.
            pose.translate(0, 0, 1);
            MyGuiComponent.popup.renderBackground(pose, partialTick, mouseX, mouseY);
            MyGuiComponent.popup.renderForeground(pose, partialTick, mouseX, mouseY);
            MyGuiComponent.popup.renderOverlay(pose, partialTick, mouseX, mouseY);
            pose.popPose();
        }
        RenderSystem.enableDepthTest();
    }

    @Override
    protected void renderBg(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        RenderSystem.disableDepthTest();
        content.renderBackground(pose, partialTick, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouse_button) {
        super.mouseClicked(mouseX, mouseY, mouse_button);

        try {
            if (MyGuiComponent.popup != null) {
                if (MyGuiComponent.popup.mouseClicked(mouseX, mouseY, mouse_button, menu.getCarried()))
                    return true;
                MyGuiComponent.popup = null;
                return true;
            }

            return content.mouseClicked(mouseX, mouseY, mouse_button, menu.getCarried());
        } catch (Exception e) {
            LiveEditMod.LOGGER.error("Cought error in main GUI mouseClicked-Method", e);
            onClose();
            return false;
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouse_button, double deltaX, double deltaY) {
        super.mouseDragged(mouseX, mouseY, mouse_button, deltaX, deltaY);

        if (MyGuiComponent.popup != null) {
            if (MyGuiComponent.popup.mouseDragged(mouseX, mouseY, mouse_button, deltaX, deltaY))
                return true;
        }

        return content.mouseDragged(mouseX, mouseY, mouse_button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouse_button) {
        super.mouseReleased(mouseX, mouseY, mouse_button);

        if (MyGuiComponent.popup != null) {
            if (MyGuiComponent.popup.mouseReleased(mouseX, mouseY, mouse_button))
                return true;
        }

        return content.mouseReleased(mouseX, mouseY, mouse_button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (super.mouseScrolled(mouseX, mouseY, scroll))
            return true;

        if (MyGuiComponent.popup != null) {
            if (MyGuiComponent.popup.mouseScrolled(mouseX, mouseY, scroll))
                return true;
        }

        return content.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public boolean keyPressed(int key, int scancode, int unknown) {
        if (super.keyPressed(key, scancode, unknown))
            return true;

        if (MyGuiComponent.popup != null) {
            if (MyGuiComponent.popup.keyPressed(key, scancode, unknown))
                return true;
        }

        return content.keyPressed(key, scancode, unknown);
    }

    @Override
    public boolean charTyped(char character, int scancode) {
        if (super.charTyped(character, scancode))
            return true;

        if (MyGuiComponent.popup != null) {
            if (MyGuiComponent.popup.charTyped(character, scancode))
                return true;
        }

        return content.charTyped(character, scancode);
    }

    @Override
    protected void containerTick() {
        content.x = this.leftPos;
        content.y = this.topPos;

        try {
            if (MyGuiComponent.popup != null) {
                MyGuiComponent.popup.tick();
            }

            content.tick();
            if (content.propagate_size_change) {
                content.calculateBounds();
            }
        } catch (Exception e) {
            LiveEditMod.LOGGER.error("Cought error in main GUI tick-Method", e);
            onClose();
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        MyGuiComponent.setFocusOn(null);
    }
}
