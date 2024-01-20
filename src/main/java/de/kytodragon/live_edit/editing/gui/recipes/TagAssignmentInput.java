package de.kytodragon.live_edit.editing.gui.recipes;

import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.editing.gui.components.Button;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;
import de.kytodragon.live_edit.editing.gui.components.ScrolledListPanel;
import de.kytodragon.live_edit.editing.gui.modules.ItemInput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class TagAssignmentInput extends MyGuiComponent implements IRecipeInput {

    private final List<ItemInput> items = new ArrayList<>();
    private final ScrolledListPanel list;
    private static final int ITEMS_PER_ROW = 8;
    private TagKey<Item> key;

    public TagAssignmentInput(int x, int y) {
        super(x, y);

        list = new ScrolledListPanel(0, 0, 160, 45);
        children.add(list);
        children.add(new Button(50, 46, 60, 12, "Add row", this::addNewRow));
    }

    private void addNewRow() {
        int startY = items.size() / ITEMS_PER_ROW * 18;
        for (int column= 0; column < ITEMS_PER_ROW; column++) {
            ItemInput item = new ItemInput(4+column*18, startY, true, true, false, false);
            items.add(item);
            list.children.add(item);
        }
        this.calculateBounds();
    }

    @Override
    public void setRecipe(MyRecipe recipe) {
        key = ((MyResult.TagResult)recipe.results.get(0)).tag;

        if (items.size() < recipe.ingredients.size()) {
            for (int row = 0; row < (recipe.ingredients.size() - items.size()) / ITEMS_PER_ROW + 1; row++) {
                addNewRow();
            }
        }
        for (int i = 0; i < recipe.ingredients.size(); i++) {
            items.get(i).setIngredient(recipe.ingredients.get(i));
        }
    }

    @Override
    public MyRecipe getRecipe() {
        MyRecipe recipe = new MyRecipe();
        recipe.ingredients = new ArrayList<>(items.size());
        for (ItemInput item : items) {
            recipe.ingredients.add(item.getIngredient());
        }
        recipe.results = List.of(new MyResult.TagResult(key));
        return recipe;
    }
}
