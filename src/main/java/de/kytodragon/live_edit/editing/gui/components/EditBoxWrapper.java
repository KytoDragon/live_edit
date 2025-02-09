package de.kytodragon.live_edit.editing.gui.components;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.item.ItemStack;

public abstract class EditBoxWrapper extends MyGuiComponent {

    protected final EditBox edit_box;
    private int highlight_pos;

    public EditBoxWrapper(int x, int y, int width, int height, String initial_text) {
        super(x, y, width, height);
        //noinspection DataFlowIssue
        edit_box = new EditBox(minecraft.font, 2, 2, width - 4, height - 4, null) {
            @Override
            public void setHighlightPos(int pos) {
                // the EditBox has no method to get the current highlight pos, so we use a subclass to get the value.
                highlight_pos = pos;
                super.setHighlightPos(pos);
            }
        };
        // We draw our own background
        edit_box.setBordered(false);
        edit_box.setValue(initial_text);
        edit_box.setResponder(this::setText);
    }

    protected abstract void setText(String text);

    @Override
    public void renderForeground(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int i = has_focus ? 0xFFFFFFFF : 0xFFA0A0A0;
        graphics.fill(x, y, x + width, y + height, i);
        graphics.fill(x+1, y+1, x + width - 1, y + height - 1, 0xFF808080);

        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        mouseX -= x;
        mouseY -= y;
        edit_box.render(graphics, mouseX, mouseY, partialTick);
        graphics.pose().popPose();

        // The EditBox draws the highlight in the wrong position (not respecting pose)
        if (edit_box.getCursorPosition() != highlight_pos) {
            String text = edit_box.getValue();
            int highlight_start = minecraft.font.width(text.substring(0, edit_box.getCursorPosition()));
            int highlight_end = minecraft.font.width(text.substring(0, highlight_pos));
            renderHighlight(graphics, x + 2 + highlight_start, y + 2, highlight_end + highlight_start, height - 4);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouse_button, ItemStack carried) {
        boolean result = edit_box.mouseClicked(mouseX - x, mouseY - y, mouse_button);
        if (!result && isInside(mouseX, mouseY))
            edit_box.setFocused(true);
        if (edit_box.isFocused())
            MyGuiComponent.setFocusOn(this);
        return result;
    }

    @Override
    protected void setFocused(boolean focus) {
        has_focus = focus;
        edit_box.setFocused(focus);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouse_button) {
        mouseX -= x;
        mouseY -= y;
        return edit_box.mouseReleased(mouseX, mouseY, mouse_button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouse_button, double deltaX, double deltaY) {
        mouseX -= x;
        mouseY -= y;
        return edit_box.mouseDragged(mouseX, mouseY, mouse_button, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(int key, int scanncode, int unknown) {
        return edit_box.keyPressed(key, scanncode, unknown);
    }

    @Override
    public boolean charTyped(char character, int scanncode) {
        return edit_box.charTyped(character, scanncode);
    }

    private void renderHighlight(GuiGraphics graphics, int startX, int startY, int width, int height) {
        if (width < 0) {
            startX += width;
            width = -width;
        }

        if (startX > this.x + 2 + this.width - 4) {
            return;
        }

        if (startX + width > this.x + 2 + this.width - 4) {
            width = (this.x + 2 + this.width - 4) - startX;
        }

        Matrix4f matrix = graphics.pose().last().pose();

        // Draw a quad that inverts colors
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(0.0F, 0.0F, 1.0F, 1.0F);
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        bufferbuilder.vertex(matrix, startX, startY, 0).endVertex();
        bufferbuilder.vertex(matrix, startX + width, startY, 0).endVertex();
        bufferbuilder.vertex(matrix, startX + width, startY + height, 0).endVertex();
        bufferbuilder.vertex(matrix, startX, startY + height, 0).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());

        RenderSystem.disableColorLogicOp();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
