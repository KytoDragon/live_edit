package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.recipe.CraftTweakerUtils;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.*;

import java.util.List;

import static de.kytodragon.live_edit.recipe.IngredientReplacer.*;

public class StoneCuttingRecipeManipulator extends StandardRecipeManipulator<StonecutterRecipe, Container> {

    public StoneCuttingRecipeManipulator() {
        super(net.minecraft.world.item.crafting.RecipeType.STONECUTTING);
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
        result.results = List.of(new MyResult.ItemResult(recipe.getResultItem(NULL_ACCESS)));
        result.type = RecipeType.STONECUTTING;
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
        sb.append(");");
    }
}
