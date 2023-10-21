package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.recipe.GeneralManipulationData;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import static de.kytodragon.live_edit.recipe.IngredientReplacer.isToReplace;
import static de.kytodragon.live_edit.recipe.IngredientReplacer.replace;

public class CraftingRecipeManipulator extends StandardRecipeManipulator<CraftingRecipe, CraftingContainer> {

    public CraftingRecipeManipulator() {
        type = net.minecraft.world.item.crafting.RecipeType.CRAFTING;
    }

    @Override
    public CraftingRecipe manipulate(CraftingRecipe _recipe, GeneralManipulationData data) {
        ItemStack resultStack = _recipe.getResultItem();
        NonNullList<Ingredient> ingredients = _recipe.getIngredients();
        boolean resultNeedsReplacement = isToReplace(resultStack, data);
        boolean ingredientsNeedReplacement = isToReplace(ingredients, data);
        if (resultNeedsReplacement)
            resultStack = replace(resultStack, data);
        if (ingredientsNeedReplacement)
            ingredients = replace(ingredients, data);

        if (resultNeedsReplacement || ingredientsNeedReplacement) {

            if (_recipe instanceof ShapedRecipe recipe) {

                if (_recipe instanceof MapExtendingRecipe) {
                    // Custon recipe, we can not change these as they use hardcoded ingredients
                    return _recipe;
                }

                return new ShapedRecipe(recipe.getId(), recipe.getGroup(), recipe.getWidth(), recipe.getHeight(), ingredients, resultStack);

            } else if (_recipe instanceof ShapelessRecipe recipe) {

                return new ShapelessRecipe(recipe.getId(), recipe.getGroup(), resultStack, ingredients);

            } else {
                // CustonRecipe, we can not change these as they use hardcoded ingredients
                return _recipe;
            }
        }
        return _recipe;
    }
}
