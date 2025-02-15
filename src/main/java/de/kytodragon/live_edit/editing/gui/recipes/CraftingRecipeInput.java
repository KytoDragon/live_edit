package de.kytodragon.live_edit.editing.gui.recipes;

import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.gui.components.VanillaTextures;
import de.kytodragon.live_edit.editing.gui.components.CheckBox;
import de.kytodragon.live_edit.editing.gui.components.Decal;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;
import de.kytodragon.live_edit.editing.gui.components.TextComponent;
import de.kytodragon.live_edit.editing.gui.modules.ItemInput;
import de.kytodragon.live_edit.editing.gui.modules.ItemOrTagInput;

import java.util.ArrayList;
import java.util.List;

public class CraftingRecipeInput extends MyGuiComponent implements IRecipeInput {

    private final ItemOrTagInput[] ingredients;
    private final ItemInput result;
    private final CheckBox shaped;

    public CraftingRecipeInput(int x, int y) {
        super(x, y);

        ingredients = new ItemOrTagInput[9];
        for (int i = 0; i < 9; i++) {
            ingredients[i] = new ItemOrTagInput(29*(i % 3) , 18 * (i / 3), true, true, false);
            addChild(ingredients[i]);
        }
        result = new ItemInput(123, 10, true, false, true, true);
        addChild(result);
        shaped = new CheckBox(90, 40);
        addChild(shaped);
        addChild(new Decal(90, 11, VanillaTextures.ARROW_RIGHT));
        addChild(new TextComponent(105, 40, "shaped"));
    }

    @Override
    public void setRecipe(MyRecipe recipe) {
        for (int i = 0; i < recipe.ingredients.size(); i++) {
            int index = i;
            if (recipe.shaped_width != 0 && recipe.shaped_width != 3) {
                index = i % recipe.shaped_width + ( i / recipe.shaped_width * 3);
            }
            ingredients[index].setIngredient(recipe.ingredients.get(i));
        }
        result.setResult(safeIndex(recipe.results, 0));
        shaped.value = recipe.shaped_width > 0;
    }

    @Override
    public MyRecipe getRecipe() {
        MyRecipe recipe = new MyRecipe();
        recipe.results = List.of(result.getResult());
        recipe.ingredients = new ArrayList<>();

        if (shaped.value) {
            int minx = 2, miny = 2, maxx = 0, maxy = 0;
            for (int i = 0; i < 9; i++) {
                if (!ingredients[i].isEmpty()) {
                    minx = Math.min(minx, i % 3);
                    miny = Math.min(miny, i / 3);
                    maxx = Math.max(maxx, i % 3);
                    maxy = Math.max(maxy, i / 3);
                }
            }
            for (int row = miny; row <= maxy; row++) {
                for (int column = minx; column <= maxx; column++) {
                    recipe.ingredients.add(ingredients[row * 3 +column].getIngredient());
                }
            }
            recipe.shaped_width = maxx - minx + 1;

        } else {
            for (int i = 0; i < 9; i++) {
                if (!ingredients[i].isEmpty()) {
                    recipe.ingredients.add(ingredients[i].getIngredient());
                }
            }
        }
        return recipe;
    }
}
