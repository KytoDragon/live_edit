package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.recipe.GeneralManipulationData;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.StonecutterRecipe;

import static de.kytodragon.live_edit.recipe.IngredientReplacer.isToReplace;
import static de.kytodragon.live_edit.recipe.IngredientReplacer.replace;

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
}
