package de.kytodragon.live_edit.editing.gui.modules;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.editing.gui.components.VanillaTextures;
import de.kytodragon.live_edit.editing.gui.components.Button;
import de.kytodragon.live_edit.editing.gui.components.Decal;
import de.kytodragon.live_edit.editing.gui.components.IntegerInput;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;
import net.minecraft.client.gui.screens.Screen;

public class TimeInput extends MyGuiComponent implements IIngredientInput, IResultInput {

    private final IntegerInput input;
    private final boolean can_be_empty;

    public TimeInput(int x, int y) {
        this(x, y, false);
    }

    public TimeInput(int x, int y, boolean can_be_empty) {
        super(x, y);
        this.can_be_empty = can_be_empty;
        input = new IntegerInput(0, 0, 40, 18);
        addChild(input);

        addChild(new Button(40, 0, 9, 9, "+", () -> amountChange(1)));
        addChild(new Button(40, 9, 9, 9, "-", () -> amountChange(-1)));
        addChild(new Decal(49, 1, VanillaTextures.CLOCK));
    }

    public int getValue() {
        return input.getValue();
    }

    private void amountChange(int amount) {
        if (Screen.hasShiftDown()) {
            amount *= 5;
        } else if (Screen.hasControlDown()) {
            amount *= 25;
        }

        int new_amount = input.getValue() + amount;
        if (new_amount < 0)
            new_amount = 0;
        if (new_amount == 0 && !can_be_empty)
            new_amount = 1;
        input.setValue(new_amount);
    }

    @Override
    public MyGuiComponent getGUIComponent() {
        return this;
    }

    @Override
    public void setIngredient(MyIngredient ingredient) {
        if (ingredient == null)
            input.setValue(0);
        else if (ingredient instanceof MyIngredient.TimeIngredient timeIngredient) {
            input.setValue(timeIngredient.processing_time);
        }
    }

    @Override
    public MyIngredient getIngredient() {
        return new MyIngredient.TimeIngredient(input.getValue());
    }

    @Override
    public void setResult(MyResult result) {
        if (result == null)
            input.setValue(0);
        else if (result instanceof MyResult.TimeResult timeResult) {
            input.setValue(timeResult.processing_time);
        }
    }

    @Override
    public MyResult getResult() {
        return new MyResult.TimeResult(input.getValue());
    }
}
