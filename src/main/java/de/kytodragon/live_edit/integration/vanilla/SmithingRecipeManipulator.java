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
import net.minecraft.world.item.crafting.UpgradeRecipe;

import java.util.List;

import static de.kytodragon.live_edit.recipe.IngredientReplacer.*;

public class SmithingRecipeManipulator extends StandardRecipeManipulator<UpgradeRecipe, Container> {

    public SmithingRecipeManipulator() {
        type = net.minecraft.world.item.crafting.RecipeType.SMITHING;
    }

    @Override
    public UpgradeRecipe manipulate(UpgradeRecipe recipe, GeneralManipulationData data) {
        UpgradeRecipeMixin upgrade = (UpgradeRecipeMixin) recipe;
        ItemStack resultStack = recipe.getResultItem();
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

            return new UpgradeRecipe(recipe.getId(), base, addition, resultStack);
        }
        return recipe;
    }

    @Override
    public MyRecipe encodeRecipe(UpgradeRecipe recipe) {
        UpgradeRecipeMixin upgrade = (UpgradeRecipeMixin) recipe;

        MyIngredient base = encodeIngredient(upgrade.live_edit_mixin_getBase());
        MyIngredient addition = encodeIngredient(upgrade.live_edit_mixin_getAddition());
        if (base == null || addition == null) {
            return null;
        }

        MyRecipe result = new MyRecipe();
        result.id = getKey(recipe);
        result.ingredients = List.of(base, addition);
        result.result = List.of(new MyResult.ItemResult(recipe.getResultItem()));
        result.type = RecipeType.SMITHING;
        return result;
    }

    @Override
    public UpgradeRecipe decodeRecipe(MyRecipe recipe) {
        ItemStack result = ((MyResult.ItemResult)recipe.result.get(0)).item;
        NonNullList<Ingredient> ingredients = decodeIngredients(recipe.ingredients);
        return new UpgradeRecipe(recipe.id, ingredients.get(0), ingredients.get(1), result);
    }
}
