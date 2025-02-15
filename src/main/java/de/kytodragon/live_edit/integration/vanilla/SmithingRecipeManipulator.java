package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.recipe.CraftTweakerUtils;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

import java.util.List;

import static de.kytodragon.live_edit.recipe.IngredientEncoder.*;

public class SmithingRecipeManipulator extends StandardRecipeManipulator<SmithingRecipe, Container> {

    public SmithingRecipeManipulator() {
        super(net.minecraft.world.item.crafting.RecipeType.SMITHING);
    }

    @Override
    public MyRecipe encodeRecipe(SmithingRecipe recipe) {
        if (!(recipe instanceof SmithingTransformRecipe upgrade))
            return null;

        MyIngredient base = encodeIngredient(upgrade.base);
        MyIngredient addition = encodeIngredient(upgrade.addition);
        if (base == null || addition == null) {
            return null;
        }

        MyRecipe result = new MyRecipe();
        result.id = getKey(recipe);
        result.ingredients = List.of(base, addition);
        result.results = List.of(new MyResult.ItemResult(recipe.getResultItem(NULL_ACCESS)));
        result.type = RecipeType.SMITHING;
        return result;
    }

    @Override
    protected void exportAdded(StringBuilder sb, MyRecipe recipe) {
        CraftTweakerUtils.exportRecipeType(sb, my_type);
        sb.append(".addTransformRecipe(\"");
        sb.append(recipe.getId().getPath());
        sb.append("\", ");
        recipe.results.get(0).export(sb);
        sb.append(", ");
        recipe.ingredients.get(0).export(sb);
        sb.append(", ");
        recipe.ingredients.get(1).export(sb);
        sb.append(");");
    }
}
