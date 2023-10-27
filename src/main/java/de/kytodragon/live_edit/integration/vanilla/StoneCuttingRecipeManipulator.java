package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.recipe.GeneralManipulationData;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.List;

import static de.kytodragon.live_edit.recipe.IngredientReplacer.*;

public class StoneCuttingRecipeManipulator extends StandardRecipeManipulator<StonecutterRecipe, Container> {

    public StoneCuttingRecipeManipulator() {
        type = net.minecraft.world.item.crafting.RecipeType.STONECUTTING;
    }

    @Override
    public StonecutterRecipe manipulate(StonecutterRecipe recipe, GeneralManipulationData data) {
        ItemStack resultStack = recipe.getResultItem();
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        boolean resultNeedsReplacement = isToReplace(resultStack, data);
        boolean ingredientsNeedReplacement = isToReplace(ingredients, data);
        if (resultNeedsReplacement)
            resultStack = replace(resultStack, data);
        if (ingredientsNeedReplacement)
            ingredients = replace(ingredients, data);

        if (resultNeedsReplacement || ingredientsNeedReplacement) {

            return new StonecutterRecipe(recipe.getId(), recipe.getGroup(), ingredients.get(0), resultStack);
        }
        return recipe;
    }

    @Override
    public MyRecipe encodeRecipe(StonecutterRecipe recipe) {

        List<MyIngredient> ingredients = encodeIngredients(recipe.getIngredients());
        if (ingredients == null)
            return null;

        MyRecipe result = new MyRecipe();
        result.id = recipe.getId();
        result.group = recipe.getGroup();
        result.ingredients = encodeIngredients(recipe.getIngredients());
        result.results = List.of(new MyResult.ItemResult(recipe.getResultItem()));
        result.type = RecipeType.STONECUTTING;
        return result;
    }

    @Override
    public StonecutterRecipe decodeRecipe(MyRecipe recipe) {
        ItemStack result = ((MyResult.ItemResult)recipe.results.get(0)).item;
        return new StonecutterRecipe(recipe.id, recipe.group, decodeIngredient(recipe.ingredients.get(0)), result);
    }
}
