package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.recipe.IRecipeManipulator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Collection;
import java.util.Optional;

public abstract class StandardRecipeManipulator <T extends Recipe<C>, C extends Container> extends IRecipeManipulator<ResourceLocation, T, VanillaIntegration> {

    public net.minecraft.world.item.crafting.RecipeType<T> type;

    @Override
    public ResourceLocation getKey(T recipe) {
        return recipe.getId();
    }

    @Override
    public Collection<T> getCurrentRecipes() {
        return integration.vanilla_recipe_manager.getAllRecipesFor(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<T> getRecipe(ResourceLocation key) {
        return (Optional<T>)integration.vanilla_recipe_manager.byKey(key);
    }

    @Override
    public void prepareReload(Collection<T> recipes) {
        integration.addNewRecipes(recipes);
    }
}
