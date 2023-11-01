package de.kytodragon.live_edit.editing.gui.modules;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.gui.components.Button;
import de.kytodragon.live_edit.editing.gui.components.ItemComponent;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;
import de.kytodragon.live_edit.editing.gui.components.TagComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemOrTagInput extends MyGuiComponent implements IIngredientInput {

    private boolean is_tag;
    private final ItemComponent item;
    private final TagComponent tag;

    public ItemOrTagInput(int x, int y) {
        this(x, y, true, false, true);
    }

    public ItemOrTagInput(int x, int y, boolean can_change, boolean only_one_item, boolean only_one_stack) {
        super(x, y, 18+1+9+9, 18);

        item = new ItemComponent(0, 0, ItemStack.EMPTY);
        item.can_change = can_change;
        item.only_one_item = only_one_item;
        item.only_one_stack = only_one_stack;

        tag = new TagComponent(0, 0, (TagKey<Item>) null);
        tag.can_change = can_change;

        if (can_change) {
            children.add(new Button(19+9, 0, 9, 9, "t", this::switchMode));

            if (!only_one_item) {
                children.add(new Button(19, 0, 9, 9, "+", () -> amountChange(1, only_one_stack)));
                children.add(new Button(19, 9, 9, 9, "-", () -> amountChange(-1, only_one_stack)));
            }
        }
        children.add(item);
    }

    private void switchMode() {
        if (is_tag) {
            item.itemStack = tag.current_item.copy();
            is_tag = false;
            children.remove(tag);
            children.add(item);
        } else  {
            tag.setAmount(item.itemStack.getCount());
            tag.setTagFromItem(item.itemStack.getItem(), true);
            is_tag = true;
            children.remove(item);
            children.add(tag);
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
            if (item.itemStack.isEmpty())
                return;

            int new_amount = item.itemStack.getCount() + amount;
            if (new_amount <= 0)
                new_amount = 1;
            if (only_one_stack && new_amount > item.itemStack.getMaxStackSize())
                new_amount = item.itemStack.getMaxStackSize();
            item.itemStack.setCount(new_amount);
        }
    }

    @Override
    public MyGuiComponent getGUIComponent() {
        return this;
    }

    @Override
    public void setIngredient(MyIngredient ingredient) {
        children.remove(item);
        children.remove(tag);
        if (ingredient instanceof MyIngredient.ItemIngredient itemIngredient) {
            is_tag = false;
            item.itemStack = itemIngredient.item.copy();
            children.add(item);
        } else if (ingredient instanceof MyIngredient.TagIngredient tagIngredient) {
            is_tag = true;
            tag.setTag(tagIngredient.tag);
            tag.amount = tagIngredient.tag_amount;
            children.add(tag);
        }
    }

    @Override
    public MyIngredient getIngredient() {
        if (is_tag) {
            return new MyIngredient.TagIngredient(tag.tag, tag.amount);
        } else {
            return new MyIngredient.ItemIngredient(item.itemStack.copy());
        }
    }
}
