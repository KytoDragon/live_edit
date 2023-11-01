package de.kytodragon.live_edit.editing.gui.modules;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.gui.VanillaTextures;
import de.kytodragon.live_edit.editing.gui.components.Button;
import de.kytodragon.live_edit.editing.gui.components.Decal;
import de.kytodragon.live_edit.editing.gui.components.IntegerInput;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;
import net.minecraft.client.gui.screens.Screen;

public class TimeInput extends MyGuiComponent implements IIngredientInput {

    private final IntegerInput input;

    public TimeInput(int x, int y) {
        super(x, y, 40+9+16, 18);
        input = new IntegerInput(0, 0, 40, 18, 0);
        children.add(input);

        children.add(new Button(40, 0, 9, 9, "+", () -> amountChange(1)));
        children.add(new Button(40, 9, 9, 9, "-", () -> amountChange(-1)));
        children.add(new Decal(49, 1, VanillaTextures.CLOCK));
    }

    private void amountChange(int amount) {
        if (Screen.hasShiftDown()) {
            amount *= 5;
        } else if (Screen.hasControlDown()) {
            amount *= 25;
        }

        int new_amount = input.value + amount;
        if (new_amount < 0)
            new_amount = 0;
        input.setValue(new_amount);
    }

    @Override
    public void setIngredient(MyIngredient ingredient) {
        if (ingredient instanceof MyIngredient.TimeIngredient timeIngredient) {
            input.setValue(timeIngredient.processing_time);
        }
    }

    @Override
    public MyIngredient getIngredient() {
        return new MyIngredient.TimeIngredient(input.value);
    }

    @Override
    public MyGuiComponent getGUIComponent() {
        return this;
    }
}
