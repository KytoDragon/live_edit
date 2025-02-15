package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.recipe.CraftTweakerUtils;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;

import java.util.List;

import static de.kytodragon.live_edit.recipe.IngredientReplacer.*;

public class CoockingRecipeManipulator <T extends AbstractCookingRecipe> extends StandardRecipeManipulator<T, Container> {

    public CoockingRecipeManipulator(net.minecraft.world.item.crafting.RecipeType<T> type) {
        super(type);
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
        result.results = List.of(new MyResult.ItemResult(recipe.getResultItem(NULL_ACCESS)), new MyResult.ExperienceResult(recipe.getExperience()));
        result.type = my_type;
        return result;
    }

    @Override
    protected void exportAdded(StringBuilder sb, MyRecipe recipe) {
        CraftTweakerUtils.exportRecipeType(sb, my_type);
        sb.append(".addRecipe(\"");
        sb.append(recipe.getId().getPath());
        sb.append("\", ");
        recipe.results.get(0).export(sb);
        sb.append(", ");
        recipe.ingredients.get(0).export(sb);
        recipe.results.get(1).export(sb);
        recipe.ingredients.get(1).export(sb);
    }
}
