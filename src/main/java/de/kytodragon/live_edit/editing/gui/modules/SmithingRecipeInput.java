package de.kytodragon.live_edit.editing.gui.modules;

import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.gui.VanillaTextures;
import de.kytodragon.live_edit.editing.gui.components.Decal;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;

import java.util.List;

public class SmithingRecipeInput extends MyGuiComponent implements IRecipeInput {

    private final ItemOrTagInput base;
    private final ItemOrTagInput addition;
    private final ItemInput result;

    public SmithingRecipeInput(int x, int y) {
        super(x, y, 151, 54);

        base = new ItemOrTagInput(10, 30, true, true, false);
        children.add(base);
        addition = new ItemOrTagInput(59, 30, true, true, false);
        children.add(addition);
        result = new ItemInput(123, 30, true, false, true, true);
        children.add(result);
        children.add(new Decal(90, 32, VanillaTextures.ARROW_RIGHT));
        children.add(new Decal(10, -3, VanillaTextures.SMITHING_HAMMER));
        children.add(new Decal(59, 30, VanillaTextures.SMITHING_SLOT));
        children.add(new Decal(42, 32, VanillaTextures.LARGE_PLUS));
    }

    @Override
    public void setRecipe(MyRecipe recipe) {
        base.setIngredient(recipe.ingredients.get(0));
        addition.setIngredient(recipe.ingredients.get(1));
        result.setResult(recipe.results.get(0));
    }

    @Override
    public MyRecipe getRecipe() {
        MyRecipe recipe = new MyRecipe();
        recipe.ingredients = List.of(base.getIngredient(), addition.getIngredient());
        recipe.results = List.of(result.getResult());
        return recipe;
    }
}
