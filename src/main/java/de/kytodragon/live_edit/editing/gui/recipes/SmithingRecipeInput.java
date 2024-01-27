package de.kytodragon.live_edit.editing.gui.recipes;

import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.gui.VanillaTextures;
import de.kytodragon.live_edit.editing.gui.components.Decal;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;
import de.kytodragon.live_edit.editing.gui.modules.ItemInput;
import de.kytodragon.live_edit.editing.gui.modules.ItemOrTagInput;

import java.util.List;

public class SmithingRecipeInput extends MyGuiComponent implements IRecipeInput {

    private final ItemOrTagInput base;
    private final ItemOrTagInput addition;
    private final ItemInput result;

    public SmithingRecipeInput(int x, int y) {
        super(x, y);

        base = new ItemOrTagInput(10, 30, true, true, false);
        addChild(base);
        addition = new ItemOrTagInput(59, 30, true, true, false);
        addChild(addition);
        result = new ItemInput(123, 30, true, false, true, true);
        addChild(result);
        addChild(new Decal(90, 32, VanillaTextures.ARROW_RIGHT));
        addChild(new Decal(10, -3, VanillaTextures.SMITHING_HAMMER));
        addChild(new Decal(59, 30, VanillaTextures.SMITHING_SLOT));
        addChild(new Decal(42, 32, VanillaTextures.LARGE_PLUS));
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
