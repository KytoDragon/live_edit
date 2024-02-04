package de.kytodragon.live_edit.editing.gui.modules;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.editing.gui.components.Button;
import de.kytodragon.live_edit.editing.gui.components.ItemComponent;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;
import net.minecraft.client.gui.screens.Screen;

public class ItemInput extends MyGuiComponent implements IIngredientInput, IResultInput {

    private final ItemComponent item;

    public ItemInput(int x, int y) {
        this(x, y, true, false, true, false);
    }

    public ItemInput(int x, int y, boolean can_change, boolean only_one_item, boolean only_one_stack, boolean draw_result_slot) {
        super(x, y);

        item = new ItemComponent(0, 0);
        item.can_change = can_change;
        item.only_one_item = only_one_item;
        item.only_one_stack = only_one_stack;
        item.draw_result_slot = draw_result_slot;

        if (can_change && !only_one_item) {
            addChild(new Button(19, 0, 9, 9, "+", () -> amountChange(1, only_one_stack)));
            addChild(new Button(19, 9, 9, 9, "-", () -> amountChange(-1, only_one_stack)));
        }
        addChild(item);
    }

    private void amountChange(int amount, boolean only_one_stack) {
        if (Screen.hasShiftDown()) {
            amount *= 5;
        } else if (Screen.hasControlDown()) {
            amount *= 64;
        }

        if (item.itemstack.isEmpty())
            return;

        int new_amount = item.itemstack.getCount() + amount;
        if (new_amount <= 0)
            new_amount = 1;
        if (only_one_stack && new_amount > item.itemstack.getMaxStackSize())
            new_amount = item.itemstack.getMaxStackSize();
        item.itemstack.setCount(new_amount);
    }

    public boolean isEmpty() {
        return item.itemstack.isEmpty();
    }

    @Override
    public MyGuiComponent getGUIComponent() {
        return this;
    }

    @Override
    public void setIngredient(MyIngredient ingredient) {
        if (ingredient instanceof MyIngredient.ItemIngredient itemIngredient) {
            item.itemstack = itemIngredient.item.copy();
        }
    }

    @Override
    public MyIngredient getIngredient() {
        return new MyIngredient.ItemIngredient(item.itemstack.copy());
    }

    @Override
    public void setResult(MyResult result) {
        if (result instanceof MyResult.ItemResult itemResult) {
            item.itemstack = itemResult.item.copy();
        }
    }

    @Override
    public MyResult getResult() {
        return new MyResult.ItemResult(item.itemstack.copy());
    }
}
