package de.kytodragon.live_edit.editing.gui.modules;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.gui.components.Button;
import de.kytodragon.live_edit.editing.gui.components.ItemComponent;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;
import de.kytodragon.live_edit.editing.gui.components.TagComponent;
import net.minecraft.client.gui.screens.Screen;

public class ItemOrTagInput extends MyGuiComponent implements IIngredientInput {

    private boolean is_tag;
    private final ItemComponent item;
    private final TagComponent tag;

    public ItemOrTagInput(int x, int y) {
        this(x, y, true, false, true);
    }

    public ItemOrTagInput(int x, int y, boolean can_change, boolean only_one_item, boolean only_one_stack) {
        super(x, y);

        item = new ItemComponent(0, 0);
        item.can_change = can_change;
        item.only_one_item = only_one_item;
        item.only_one_stack = only_one_stack;

        tag = new TagComponent(0, 0);
        tag.can_change = can_change;

        if (can_change) {
            addChild(new Button(only_one_item ? 19 : 19+9, 0, 9, 9, "t", this::switchMode));

            if (!only_one_item) {
                addChild(new Button(19, 0, 9, 9, "+", () -> amountChange(1, only_one_stack)));
                addChild(new Button(19, 9, 9, 9, "-", () -> amountChange(-1, only_one_stack)));
            }
        }
        addChild(item);
    }

    private void switchMode() {
        if (is_tag) {
            item.itemstack = tag.current_item.copy();
            is_tag = false;
            children.remove(tag);
            addChild(item);
        } else  {
            tag.setAmount(item.itemstack.getCount());
            tag.setTagFromItem(item.itemstack.getItem(), true);
            is_tag = true;
            children.remove(item);
            addChild(tag);
        }
    }

    private void amountChange(int amount, boolean only_one_stack) {
        if (Screen.hasShiftDown()) {
            amount *= 5;
        } else if (Screen.hasControlDown()) {
            amount *= 64;
        }
        if (is_tag) {
            if (tag.tag == null)
                return;

            int new_amount = tag.amount + amount;
            if (new_amount <= 0)
                new_amount = 1;
            if (only_one_stack && new_amount > tag.current_item.getMaxStackSize())
                new_amount = tag.current_item.getMaxStackSize();
            tag.setAmount(new_amount);
        } else {
            if (item.itemstack.isEmpty())
                return;

            int new_amount = item.itemstack.getCount() + amount;
            if (new_amount <= 0)
                new_amount = 1;
            if (only_one_stack && new_amount > item.itemstack.getMaxStackSize())
                new_amount = item.itemstack.getMaxStackSize();
            item.itemstack.setCount(new_amount);
        }
    }

    public boolean isEmpty() {
        if (is_tag) {
            return tag.tag == null;
        } else {
            return item.itemstack.isEmpty();
        }
    }

    @Override
    public void setIngredient(MyIngredient ingredient) {
        children.remove(item);
        children.remove(tag);
        if (ingredient instanceof MyIngredient.ItemIngredient itemIngredient) {
            is_tag = false;
            item.itemstack = itemIngredient.item.copy();
            addChild(item);
        } else if (ingredient instanceof MyIngredient.TagIngredient tagIngredient) {
            is_tag = true;
            tag.amount = tagIngredient.tag_amount;
            tag.setTag(tagIngredient.tag);
            addChild(tag);
        }
    }

    @Override
    public MyIngredient getIngredient() {
        if (is_tag) {
            return new MyIngredient.TagIngredient(tag.tag, tag.amount);
        } else {
            return new MyIngredient.ItemIngredient(item.itemstack.copy());
        }
    }
}
