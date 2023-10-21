package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.recipe.GeneralManipulationData;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;

import static de.kytodragon.live_edit.recipe.IngredientReplacer.isToReplace;
import static de.kytodragon.live_edit.recipe.IngredientReplacer.replace;

public class CoockingRecipeManipulator <T extends AbstractCookingRecipe> extends StandardRecipeManipulator<T, Container> {

    private final CoockinRecipeCreator<T> constructor;

    public CoockingRecipeManipulator(net.minecraft.world.item.crafting.RecipeType<T> type, CoockinRecipeCreator<T> constructor) {
        this.type = type;
        this.constructor = constructor;
    }

    @Override
    public T manipulate(T recipe, GeneralManipulationData data) {
        ItemStack resultStack = recipe.getResultItem();
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        boolean resultNeedsReplacement = isToReplace(resultStack, data);
        boolean ingredientsNeedReplacement = isToReplace(ingredients, data);
        if (resultNeedsReplacement)
            resultStack = replace(resultStack, data);
        if (ingredientsNeedReplacement)
            ingredients = replace(ingredients, data);

        if (resultNeedsReplacement || ingredientsNeedReplacement) {
            recipe = constructor.create(recipe.getId(), recipe.getGroup(), ingredients.get(0), resultStack, recipe.getExperience(), recipe.getCookingTime());
        }
        return recipe;
    }

    public interface CoockinRecipeCreator<T extends AbstractCookingRecipe> {
        T create(ResourceLocation key, String group, Ingredient ingredient, ItemStack result, float experience, int cookingTime);
    }
}
