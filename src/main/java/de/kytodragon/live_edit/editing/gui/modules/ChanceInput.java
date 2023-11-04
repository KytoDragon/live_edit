package de.kytodragon.live_edit.editing.gui.modules;

import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.editing.gui.components.Button;
import de.kytodragon.live_edit.editing.gui.components.FloatInput;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;
import de.kytodragon.live_edit.editing.gui.components.TextComponent;
import net.minecraft.client.gui.screens.Screen;

public class ChanceInput extends MyGuiComponent implements IResultInput {

    private final FloatInput input;

    public ChanceInput(int x, int y) {
        super(x, y);
        input = new FloatInput(0, 0, 30, 18, 0);
        children.add(input);

        children.add(new Button(30, 0, 9, 9, "+", () -> amountChange(1)));
        children.add(new Button(30, 9, 9, 9, "-", () -> amountChange(-1)));
        children.add(new TextComponent(42, 2, "%"));
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
        if (new_amount > 100)
            new_amount = 100;
        input.setValue(new_amount);
    }

    @Override
    public void setResult(MyResult result) {
        if (result instanceof MyResult.ChanceResult chanceResult) {
            input.setValue(chanceResult.output_chance * 100);
        }
    }

    @Override
    public MyResult getResult() {
        return new MyResult.ChanceResult(input.value / 100);
    }
}
