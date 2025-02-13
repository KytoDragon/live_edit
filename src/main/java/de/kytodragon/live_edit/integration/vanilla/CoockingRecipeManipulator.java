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
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

import static de.kytodragon.live_edit.recipe.IngredientReplacer.*;

public class CoockingRecipeManipulator <T extends AbstractCookingRecipe> extends StandardRecipeManipulator<T, Container> {

    public CoockingRecipeManipulator(net.minecraft.world.item.crafting.RecipeType<T> type) {
        this.type = type;
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
        result.ingredients = ingredients;
        result.results = List.of(new MyResult.ItemResult(recipe.getResultItem(null)), new MyResult.ExperienceResult(recipe.getExperience()));
        result.type = my_type;
        return result;
    }
}
