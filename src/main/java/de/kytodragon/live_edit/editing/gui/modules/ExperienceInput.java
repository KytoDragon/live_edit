package de.kytodragon.live_edit.editing.gui.modules;

import com.mojang.blaze3d.vertex.PoseStack;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.editing.gui.Texture;
import de.kytodragon.live_edit.editing.gui.VanillaTextures;
import de.kytodragon.live_edit.editing.gui.components.Button;
import de.kytodragon.live_edit.editing.gui.components.FloatInput;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;
import net.minecraft.client.gui.screens.Screen;

public class ExperienceInput extends MyGuiComponent implements IResultInput {

    private final FloatInput input;

    public ExperienceInput(int x, int y) {
        super(x, y, 40+9+16, 18);
        input = new FloatInput(0, 0, 40, 18, 0);
        children.add(input);

        children.add(new Button(40, 0, 9, 9, "+", () -> amountChange(1)));
        children.add(new Button(40, 9, 9, 9, "-", () -> amountChange(-1)));
    }

    private void amountChange(int amount) {
        if (Screen.hasShiftDown()) {
            amount *= 5;
        } else if (Screen.hasControlDown()) {
            amount *= 25;
        }

        float new_amount = input.value + amount;
        if (new_amount < 0)
            new_amount = 0;
        input.setValue(new_amount);
    }

    @Override
    public void renderForeground(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        super.renderForeground(pose, partialTick, mouseX, mouseY);

        if (input.value > 0) {
            Texture t;
            if (input.value >= 2477) {
                t = VanillaTextures.EXPERIENCE_ORBS[10];
            } else if (input.value >= 1237) {
                t = VanillaTextures.EXPERIENCE_ORBS[9];
            } else if (input.value >= 617) {
                t = VanillaTextures.EXPERIENCE_ORBS[8];
            } else if (input.value >= 307) {
                t = VanillaTextures.EXPERIENCE_ORBS[7];
            } else if (input.value >= 149) {
                t = VanillaTextures.EXPERIENCE_ORBS[6];
            } else if (input.value >= 73) {
                t = VanillaTextures.EXPERIENCE_ORBS[5];
            } else if (input.value >= 37) {
                t = VanillaTextures.EXPERIENCE_ORBS[4];
            } else if (input.value >= 17) {
                t = VanillaTextures.EXPERIENCE_ORBS[3];
            } else if (input.value >= 7) {
                t = VanillaTextures.EXPERIENCE_ORBS[2];
            } else if (input.value >= 3) {
                t = VanillaTextures.EXPERIENCE_ORBS[1];
            } else {
                t = VanillaTextures.EXPERIENCE_ORBS[0];
            }
            t.draw(this, pose, x+49, y+1);
        }
    }

    @Override
    public void setResult(MyResult result) {
        if (result instanceof MyResult.ExperienceResult experienceResult) {
            input.setValue(experienceResult.experience);
        }
    }

    @Override
    public MyResult getResult() {
        return new MyResult.ExperienceResult(input.value);
    }

    @Override
    public MyGuiComponent getGUIComponent() {
        return this;
    }
}
