package de.kytodragon.live_edit.editing.gui.recipes;

import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.gui.VanillaTextures;
import de.kytodragon.live_edit.editing.gui.components.Decal;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;
import de.kytodragon.live_edit.editing.gui.modules.ItemInput;
import de.kytodragon.live_edit.editing.gui.modules.ItemOrTagInput;

import java.util.List;

public class StoneCuttingRecipeInput extends MyGuiComponent implements IRecipeInput {

    private final ItemOrTagInput ingredient;
    private final ItemInput result;

    public StoneCuttingRecipeInput(int x, int y) {
        super(x, y);

        ingredient = new ItemOrTagInput(20 , 10, true, true, false);
        children.add(ingredient);
        result = new ItemInput(103, 10, true, false, true, true);
        children.add(result);
        children.add(new Decal(65, 11, VanillaTextures.ARROW_RIGHT));
    }

    @Override
    public void setRecipe(MyRecipe recipe) {
        ingredient.setIngredient(recipe.ingredients.get(0));
        result.setResult(recipe.results.get(0));
    }

    @Override
    public MyRecipe getRecipe() {
        MyRecipe recipe = new MyRecipe();
        recipe.ingredients = List.of(ingredient.getIngredient());
        recipe.results = List.of(result.getResult());
        return recipe;
    }
}
