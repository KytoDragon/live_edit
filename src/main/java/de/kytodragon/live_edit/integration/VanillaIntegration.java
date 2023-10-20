package de.kytodragon.live_edit.integration;

import de.kytodragon.live_edit.mixin_interfaces.UpgradeRecipeInterface;
import de.kytodragon.live_edit.recipe.GeneralManipulationData;
import de.kytodragon.live_edit.recipe.IRecipeManipulator;
import de.kytodragon.live_edit.recipe.RecipeManager;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.Collection;
import java.util.Optional;

import static de.kytodragon.live_edit.recipe.IngredientReplacer.*;

public class VanillaIntegration implements Integration {

    @Override
    public void registerManipulators(RecipeManager manager) {
        manager.manipulators.put(RecipeType.CRAFTING, new CraftingManipulator());
        manager.manipulators.put(RecipeType.SMELTING, new CoockingRecipeManipulator<>(net.minecraft.world.item.crafting.RecipeType.SMELTING, SmeltingRecipe::new));
        manager.manipulators.put(RecipeType.BLASTING, new CoockingRecipeManipulator<>(net.minecraft.world.item.crafting.RecipeType.BLASTING, BlastingRecipe::new));
        manager.manipulators.put(RecipeType.SMOKING, new CoockingRecipeManipulator<>(net.minecraft.world.item.crafting.RecipeType.SMOKING, SmokingRecipe::new));
        manager.manipulators.put(RecipeType.CAMPFIRE_COOKING, new CoockingRecipeManipulator<>(net.minecraft.world.item.crafting.RecipeType.CAMPFIRE_COOKING, CampfireCookingRecipe::new));
        manager.manipulators.put(RecipeType.STONECUTTING, new StoneCuttingRecipeManipulator());
        manager.manipulators.put(RecipeType.SMITHING, new SmithingRecipeManipulator());
    }

    static abstract class StandardRecipeManipulator <T extends Recipe<C>, C extends Container> extends IRecipeManipulator<ResourceLocation, T> {

        public net.minecraft.world.item.crafting.RecipeType<T> type;

        @Override
        public ResourceLocation getKey(T recipe) {
            return recipe.getId();
        }

        @Override
        public Collection<T> getCurrentRecipes(net.minecraft.world.item.crafting.RecipeManager manager) {
            return manager.getAllRecipesFor(type);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Optional<T> getRecipe(net.minecraft.world.item.crafting.RecipeManager manager, ResourceLocation key) {
            return (Optional<T>)manager.byKey(key);
        }
    }

    static class CraftingManipulator extends StandardRecipeManipulator<CraftingRecipe, CraftingContainer> {

        public CraftingManipulator() {
            type = net.minecraft.world.item.crafting.RecipeType.CRAFTING;
        }

        @Override
        public CraftingRecipe manipulate(CraftingRecipe _recipe, GeneralManipulationData data) {
            ItemStack resultStack = _recipe.getResultItem();
            NonNullList<Ingredient> ingredients = _recipe.getIngredients();
            boolean resultNeedsReplacement = isToReplace(resultStack, data);
            boolean ingredientsNeedReplacement = isToReplace(ingredients, data);
            if (resultNeedsReplacement)
                resultStack = replace(resultStack, data);
            if (ingredientsNeedReplacement)
                ingredients = replace(ingredients, data);

            if (resultNeedsReplacement || ingredientsNeedReplacement) {

                if (_recipe instanceof ShapedRecipe recipe) {

                    if (_recipe instanceof MapExtendingRecipe) {
                        // Custon recipe, we can not change these as they use hardcoded ingredients
                        return _recipe;
                    }

                    return new ShapedRecipe(recipe.getId(), recipe.getGroup(), recipe.getWidth(), recipe.getHeight(), ingredients, resultStack);

                } else if (_recipe instanceof ShapelessRecipe recipe) {

                    return new ShapelessRecipe(recipe.getId(), recipe.getGroup(), resultStack, ingredients);

                } else {
                    // CustonRecipe, we can not change these as they use hardcoded ingredients
                    return _recipe;
                }
            }
            return _recipe;
        }
    }

    static class CoockingRecipeManipulator <T extends AbstractCookingRecipe> extends StandardRecipeManipulator<T, Container> {

        CookieBaker<T> constructor;

        public CoockingRecipeManipulator(net.minecraft.world.item.crafting.RecipeType<T> type, CookieBaker<T> constructor) {
            this.type = type;
            this.constructor = constructor;

        }

        @Override
        public T manipulate(T recipe, GeneralManipulationData data) {
            ItemStack resultStack = recipe.getResultItem();
            NonNullList<Ingredient> ingredients = recipe.getIngredients();
            boolean resultNeedsReplacement = isToReplace(resultStack, data);
            boolean ingredientsNeedReplacement = isToReplace(ingredients, data);
            if (resultNeedsReplacement)
                resultStack = replace(resultStack, data);
            if (ingredientsNeedReplacement)
                ingredients = replace(ingredients, data);

            if (resultNeedsReplacement || ingredientsNeedReplacement) {
                recipe = constructor.create(recipe.getId(), recipe.getGroup(), ingredients.get(0), resultStack, recipe.getExperience(), recipe.getCookingTime());
            }
            return recipe;
        }

        interface CookieBaker<T extends AbstractCookingRecipe> {
            T create(ResourceLocation key, String group, Ingredient ingredient, ItemStack result, float experience, int cookingTime);
        }
    }

    static class StoneCuttingRecipeManipulator extends StandardRecipeManipulator<StonecutterRecipe, Container> {

        public StoneCuttingRecipeManipulator() {
            type = net.minecraft.world.item.crafting.RecipeType.STONECUTTING;
        }

        @Override
        public StonecutterRecipe manipulate(StonecutterRecipe recipe, GeneralManipulationData data) {
            ItemStack resultStack = recipe.getResultItem();
            NonNullList<Ingredient> ingredients = recipe.getIngredients();
            boolean resultNeedsReplacement = isToReplace(resultStack, data);
            boolean ingredientsNeedReplacement = isToReplace(ingredients, data);
            if (resultNeedsReplacement)
                resultStack = replace(resultStack, data);
            if (ingredientsNeedReplacement)
                ingredients = replace(ingredients, data);

            if (resultNeedsReplacement || ingredientsNeedReplacement) {

                return new StonecutterRecipe(recipe.getId(), recipe.getGroup(), ingredients.get(0), resultStack);
            }
            return recipe;
        }
    }

    static class SmithingRecipeManipulator extends StandardRecipeManipulator<UpgradeRecipe, Container> {

        public SmithingRecipeManipulator() {
            type = net.minecraft.world.item.crafting.RecipeType.SMITHING;
        }

        @Override
        public UpgradeRecipe manipulate(UpgradeRecipe recipe, GeneralManipulationData data) {
            UpgradeRecipeInterface upgrade = (UpgradeRecipeInterface) recipe;
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

}
