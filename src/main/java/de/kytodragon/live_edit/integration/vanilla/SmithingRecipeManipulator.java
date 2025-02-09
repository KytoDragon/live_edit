package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.mixins.UpgradeRecipeMixin;
import de.kytodragon.live_edit.recipe.GeneralManipulationData;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

import java.util.List;

import static de.kytodragon.live_edit.recipe.IngredientReplacer.*;

public class SmithingRecipeManipulator extends StandardRecipeManipulator<SmithingRecipe, Container> {

    public SmithingRecipeManipulator() {
        type = net.minecraft.world.item.crafting.RecipeType.SMITHING;
    }

    @Override
    public SmithingRecipe manipulate(SmithingRecipe recipe, GeneralManipulationData data) {
        UpgradeRecipeMixin upgrade = (UpgradeRecipeMixin) recipe;
        ItemStack resultStack = recipe.getResultItem(null);
        Ingredient base = upgrade.live_edit_mixin_getBase();
        Ingredient addition = upgrade.live_edit_mixin_getAddition();
        boolean resultNeedsReplacement = isToReplace(resultStack, data);
        boolean baseNeedReplacement = isToReplace(base, data);
        boolean additionNeedReplacement = isToReplace(addition, data);
        if (resultNeedsReplacement)
            resultStack = replace(resultStack, data);
        if (baseNeedReplacement)
            base = replace(base, data);
        if (additionNeedReplacement)
            addition = replace(addition, data);

        if (resultNeedsReplacement || baseNeedReplacement || additionNeedReplacement) {

            return new SmithingTransformRecipe(recipe.getId(), base, addition, null, resultStack);
        }
        return recipe;
    }

    @Override
    public MyRecipe encodeRecipe(SmithingRecipe recipe) {
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

    @Override
    public SmithingRecipe decodeRecipe(MyRecipe recipe) {
        ItemStack result = ((MyResult.ItemResult)recipe.results.get(0)).item;
        NonNullList<Ingredient> ingredients = decodeIngredients(recipe.ingredients);
        return new SmithingTransformRecipe(recipe.id, ingredients.get(0), ingredients.get(1), null, result);
    }
}
