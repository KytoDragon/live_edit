package de.kytodragon.live_edit.editing.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

public class CheckBox extends MyGuiComponent {

    public boolean value;

    public CheckBox(int x, int y) {
        super(x, y, 9, 9);
    }

    @Override
    public void renderForeground(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        if (value) {
            VanillaTextures.CHECKBOX_FILLED.draw(this, pose, x, y);
        } else {
            VanillaTextures.CHECKBOX_EMPTY.draw(this, pose, x, y);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouse_button, ItemStack carried) {
        if (isInside(mouseX, mouseY)) {
            value = !value;

            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        return false;
    }
}
