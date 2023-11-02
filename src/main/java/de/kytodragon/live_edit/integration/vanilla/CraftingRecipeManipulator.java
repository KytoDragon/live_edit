package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.recipe.GeneralManipulationData;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.List;

import static de.kytodragon.live_edit.recipe.IngredientReplacer.*;

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
                // NOTE TippedArrowRecipe, ShulkerBoxColoring and SuspiciousStewRecipe could be changed into a list of recipes,
                // but this list would not take new potions, colors or flowers into account.
                return _recipe;
            }
        }
        return _recipe;
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
        result.results = List.of(new MyResult.ItemResult(recipe.getResultItem()));
        result.type = RecipeType.CRAFTING;
        if (recipe instanceof ShapedRecipe shaped)
            result.shaped_width = shaped.getRecipeWidth();
        return result;
    }

    @Override
    public CraftingRecipe decodeRecipe(MyRecipe recipe) {
        ItemStack result = ((MyResult.ItemResult)recipe.results.get(0)).item;
        if (recipe.shaped_width > 0) {
            int shaped_height = (recipe.ingredients.size() + recipe.shaped_width - 1) / recipe.shaped_width;
            return new ShapedRecipe(recipe.id, recipe.group, recipe.shaped_width, shaped_height, decodeIngredients(recipe.ingredients), result);
        } else {
            return new ShapelessRecipe(recipe.id, recipe.group, result, decodeIngredients(recipe.ingredients));
        }
    }
}
