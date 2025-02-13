package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.*;

import java.util.List;

import static de.kytodragon.live_edit.recipe.IngredientReplacer.*;

public class CraftingRecipeManipulator extends StandardRecipeManipulator<CraftingRecipe, CraftingContainer> {

    public CraftingRecipeManipulator() {
        type = net.minecraft.world.item.crafting.RecipeType.CRAFTING;
    }

    @Override
    public MyRecipe encodeRecipe(CraftingRecipe recipe) {

        if (recipe instanceof CustomRecipe || recipe instanceof MapExtendingRecipe)
            return null;

        List<MyIngredient> ingredients = encodeIngredients(recipe.getIngredients());
        if (ingredients == null)
            return null;

        MyRecipe result = new MyRecipe();
        result.id = recipe.getId();
        result.group = recipe.getGroup();
        result.ingredients = encodeIngredients(recipe.getIngredients());
        result.results = List.of(new MyResult.ItemResult(recipe.getResultItem(null)));
        result.type = RecipeType.CRAFTING;
        if (recipe instanceof ShapedRecipe shaped)
            result.shaped_width = shaped.getRecipeWidth();
        return result;
    }
}
