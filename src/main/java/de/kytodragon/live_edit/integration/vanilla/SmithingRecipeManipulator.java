package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.mixins.UpgradeRecipeMixin;
import de.kytodragon.live_edit.recipe.GeneralManipulationData;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.UpgradeRecipe;

import static de.kytodragon.live_edit.recipe.IngredientReplacer.isToReplace;
import static de.kytodragon.live_edit.recipe.IngredientReplacer.replace;

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
}
