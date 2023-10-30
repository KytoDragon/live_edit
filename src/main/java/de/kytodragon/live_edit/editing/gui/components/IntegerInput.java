package de.kytodragon.live_edit.editing.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.world.item.ItemStack;

public class IntegerInput extends MyGuiComponent {

    private final EditBox editBox;
    private int value;

    public IntegerInput(int x, int y, int width, int height, int value) {
        super(x, y, width, height);
        this.value = value;
        editBox = new EditBox(minecraft.font, 2, 2, width - 4, height - 4, null);
        editBox.setBordered(false);
        boolean allowNegative = false; // TODO
        editBox.setValue(Integer.toString(value));
        if (allowNegative) {
            editBox.setFilter(text -> text.matches("-?[0-9]{0,5}"));
        } else {
            editBox.setFilter(text -> text.matches("[0-9]{0,5}"));
        }
        editBox.setResponder(text -> {
            if (text == null || text.isEmpty() || text.equals("-"))
                this.value = 0;
            else
                this.value = Integer.parseInt(text);
        });
    }

    public int getValue() {
        return value;
    }

    @Override
    public void renderForeground(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        int i = has_focus ? 0xFFFFFFFF : 0xFFA0A0A0;
        fill(pose, x, y, x + width, y + height, i);
        fill(pose, x+1, y+1, x + width - 1, y + height - 1, 0xFF808080);

        pose.pushPose();
        pose.translate(x, y, 0);
        mouseX -= x;
        mouseY -= y;
        editBox.render(pose, mouseX, mouseY, partialTick);
        pose.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouse_button, ItemStack carried) {
        boolean result = editBox.mouseClicked(mouseX - x, mouseY - y, mouse_button);
        if (!result && isInside(mouseX, mouseY))
            editBox.setFocus(true);
        if (editBox.isFocused())
            MyGuiComponent.setFocusOn(this);
        return result;
    }

    @Override
    protected void setFocused(boolean focus) {
        has_focus = focus;
        editBox.setFocus(focus);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouse_button) {
        mouseX -= x;
        mouseY -= y;
        return editBox.mouseReleased(mouseX, mouseY, mouse_button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouse_button, double deltaX, double deltaY) {
        mouseX -= x;
        mouseY -= y;
        return editBox.mouseDragged(mouseX, mouseY, mouse_button, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(int key, int scanncode, int unknown) {
        return editBox.keyPressed(key, scanncode, unknown);
    }

    @Override
    public boolean charTyped(char character, int scanncode) {
        return editBox.charTyped(character, scanncode);
    }
}
