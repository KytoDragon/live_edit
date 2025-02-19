package de.kytodragon.live_edit.editing.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class Button extends MyGuiComponent {

    private final ExtendedButton button;
    private final String text;

    public Button(int x, int y, int width, int height, String text, Runnable onPress) {
        super(x, y, width, height);
        this.text = text;
        button = new ExtendedButton(x, y, width, height, Component.literal(""), b -> {
            onPress.run();
        });
    }

    @Override
    public void renderForeground(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        button.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(minecraft.font, text, x + this.width / 2 + 1, y + (this.height - 10) / 2 + 1, button.getFGColor());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouse_button, ItemStack carried) {
        return button.mouseClicked(mouseX, mouseY, mouse_button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouse_button) {
        return button.mouseReleased(mouseX, mouseY, mouse_button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouse_button, double deltaX, double deltaY) {
        return button.mouseDragged(mouseX, mouseY, mouse_button, deltaX, deltaY);
    }
}
