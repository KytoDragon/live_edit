package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.recipe.IRecipeManipulator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Collection;
import java.util.Optional;

public class DummyRecipeManipulator<T extends Recipe<?>> extends IRecipeManipulator<T, MyRecipe, VanillaIntegration> {

    private final net.minecraft.world.item.crafting.RecipeType<T> vanilla_type;

    public DummyRecipeManipulator(net.minecraft.world.item.crafting.RecipeType<T> vanilla_type) {
        this.vanilla_type = vanilla_type;
    }

    @Override
    public ResourceLocation getKey(T recipe) {
        return recipe.getId();
    }

    @Override
    public boolean isRealImplementation() {
        return false;
    }

    @Override
    public Collection<T> getCurrentRecipes() {
        return genericsHelper();
    }

    @SuppressWarnings("unchecked")
    private  <C extends Container> Collection<T> genericsHelper() {
        net.minecraft.world.item.crafting.RecipeType<? extends Recipe<C>> type = (net.minecraft.world.item.crafting.RecipeType<? extends Recipe<C>>) vanilla_type;
        return (Collection<T>) integration.vanilla_recipe_manager.getAllRecipesFor(type);
    }

    @Override
    public Optional<T> getRecipe(ResourceLocation key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MyRecipe encodeRecipe(T recipe) {
        return null;
    }
}
