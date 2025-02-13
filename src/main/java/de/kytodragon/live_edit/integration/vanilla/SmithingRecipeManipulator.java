package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.mixins.UpgradeRecipeMixin;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

import java.util.List;

import static de.kytodragon.live_edit.recipe.IngredientReplacer.*;

public class SmithingRecipeManipulator extends StandardRecipeManipulator<SmithingRecipe, Container> {

    public SmithingRecipeManipulator() {
        type = net.minecraft.world.item.crafting.RecipeType.SMITHING;
    }

    @Override
    public MyRecipe encodeRecipe(SmithingRecipe recipe) {
        if (!(recipe instanceof SmithingTransformRecipe))
            return null;
        UpgradeRecipeMixin upgrade = (UpgradeRecipeMixin) recipe;

        MyIngredient base = encodeIngredient(upgrade.live_edit_mixin_getBase());
        MyIngredient addition = encodeIngredient(upgrade.live_edit_mixin_getAddition());
        if (base == null || addition == null) {
            return null;
        }

        MyRecipe result = new MyRecipe();
        result.id = getKey(recipe);
        result.ingredients = List.of(base, addition);
        result.results = List.of(new MyResult.ItemResult(recipe.getResultItem(null)));
        result.type = RecipeType.SMITHING;
        return result;
    }
}
