package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.recipe.GeneralManipulationData;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

import static de.kytodragon.live_edit.recipe.IngredientReplacer.*;

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


    @Override
    public MyRecipe encodeRecipe(T recipe) {

        List<MyIngredient> ingredients = encodeIngredients(recipe.getIngredients());
        if (ingredients == null)
            return null;

        ingredients.add(new MyIngredient.TimeIngredient(recipe.getCookingTime()));

        MyRecipe result = new MyRecipe();
        result.id = recipe.getId();
        result.group = recipe.getGroup();
        result.ingredients = encodeIngredients(recipe.getIngredients());
        result.result = List.of(new MyResult.ItemResult(recipe.getResultItem()), new MyResult.ExperienceResult(recipe.getExperience()));
        result.type = my_type;
        return result;
    }

    @Override
    public T decodeRecipe(MyRecipe recipe) {
        ItemStack result = ((MyResult.ItemResult)recipe.result.get(0)).item;
        float experience = ((MyResult.ExperienceResult)recipe.result.get(1)).experience;
        NonNullList<Ingredient> ingredients = decodeIngredients(recipe.ingredients, 1);
        int coocking_time = ((MyIngredient.TimeIngredient)recipe.ingredients.get(1)).processing_time;
        return constructor.create(recipe.id, recipe.group, ingredients.get(0), result, experience, coocking_time);
    }
}
