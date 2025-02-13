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
    public MyRecipe encodeRecipe(StonecutterRecipe recipe) {

        List<MyIngredient> ingredients = encodeIngredients(recipe.getIngredients());
        if (ingredients == null)
            return null;

        MyRecipe result = new MyRecipe();
        result.id = recipe.getId();
        result.group = recipe.getGroup();
        result.ingredients = encodeIngredients(recipe.getIngredients());
        result.results = List.of(new MyResult.ItemResult(recipe.getResultItem(null)));
        result.type = RecipeType.STONECUTTING;
        return result;
    }
}
