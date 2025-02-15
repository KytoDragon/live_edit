package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.recipe.CraftTweakerUtils;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.*;

import java.util.List;

import static de.kytodragon.live_edit.recipe.IngredientReplacer.*;

public class CraftingRecipeManipulator extends StandardRecipeManipulator<CraftingRecipe, CraftingContainer> {

    public CraftingRecipeManipulator() {
        super(net.minecraft.world.item.crafting.RecipeType.CRAFTING);
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
        result.results = List.of(new MyResult.ItemResult(recipe.getResultItem(NULL_ACCESS)));
        result.type = RecipeType.CRAFTING;
        if (recipe instanceof ShapedRecipe shaped)
            result.shaped_width = shaped.getRecipeWidth();
        return result;
    }

    protected void exportAdded(StringBuilder sb, MyRecipe recipe) {
        CraftTweakerUtils.exportRecipeType(sb, my_type);
        if (recipe.shaped_width > 0) {
            sb.append(".addShaped(\"").append(recipe.getId().getPath()).append("\", ");
            recipe.results.get(0).export(sb);

            sb.append("[\n\t");
            for (int row = 0; row < recipe.shaped_width; row++) {
                if (row != 0)
                    sb.append(",\n\t");
                sb.append("[");
                for (int column = 0; column < recipe.ingredients.size() / recipe.shaped_width; column++) {
                    if (column != 0)
                        sb.append(", ");
                    recipe.ingredients.get(row * recipe.shaped_width + column).export(sb);
                }
                sb.append("]");
            }
            sb.append("]);");
        } else {
            sb.append(".addShapeless(\"").append(recipe.getId().getPath()).append("\", ");
            recipe.results.get(0).export(sb);
            sb.append(", ");
            CraftTweakerUtils.exportIngredients(sb, recipe.ingredients);
            sb.append(");");
        }
    }
}
