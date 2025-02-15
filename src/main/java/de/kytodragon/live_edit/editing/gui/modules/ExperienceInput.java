package de.kytodragon.live_edit.editing.gui.modules;

import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.editing.gui.components.Texture;
import de.kytodragon.live_edit.editing.gui.components.VanillaTextures;
import de.kytodragon.live_edit.editing.gui.components.Button;
import de.kytodragon.live_edit.editing.gui.components.FloatInput;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class ExperienceInput extends MyGuiComponent implements IResultInput {

    private final FloatInput input;

    public ExperienceInput(int x, int y) {
        super(x, y, 40+9+16, 18);
        input = new FloatInput(0, 0, 40, 18);
        addChild(input);

        addChild(new Button(40, 0, 9, 9, "+", () -> amountChange(1)));
        addChild(new Button(40, 9, 9, 9, "-", () -> amountChange(-1)));
    }

    private void amountChange(int amount) {
        if (Screen.hasShiftDown()) {
            amount *= 5;
        } else if (Screen.hasControlDown()) {
            amount *= 25;
        }

        float new_amount = input.getValue() + amount;
        if (new_amount < 0)
            new_amount = 0;
        input.setValue(new_amount);
    }

    @Override
    public void renderForeground(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        super.renderForeground(graphics, partialTick, mouseX, mouseY);

        float value = input.getValue();
        if (value > 0) {
            Texture t;
            if (value >= 2477) {
                t = VanillaTextures.EXPERIENCE_ORBS[10];
            } else if (value >= 1237) {
                t = VanillaTextures.EXPERIENCE_ORBS[9];
            } else if (value >= 617) {
                t = VanillaTextures.EXPERIENCE_ORBS[8];
            } else if (value >= 307) {
                t = VanillaTextures.EXPERIENCE_ORBS[7];
            } else if (value >= 149) {
                t = VanillaTextures.EXPERIENCE_ORBS[6];
            } else if (value >= 73) {
                t = VanillaTextures.EXPERIENCE_ORBS[5];
            } else if (value >= 37) {
                t = VanillaTextures.EXPERIENCE_ORBS[4];
            } else if (value >= 17) {
                t = VanillaTextures.EXPERIENCE_ORBS[3];
            } else if (value >= 7) {
                t = VanillaTextures.EXPERIENCE_ORBS[2];
            } else if (value >= 3) {
                t = VanillaTextures.EXPERIENCE_ORBS[1];
            } else {
                t = VanillaTextures.EXPERIENCE_ORBS[0];
            }
            t.draw(graphics, x+49, y+1);
        }
    }

    @Override
    public void setResult(MyResult result) {
        if (result == null)
            input.setValue(0);
        else if (result instanceof MyResult.ExperienceResult experienceResult) {
            input.setValue(experienceResult.experience);
        }
    }

    @Override
    public MyResult getResult() {
        return new MyResult.ExperienceResult(input.getValue());
    }
}
