package de.kytodragon.live_edit.recipe;

import net.minecraft.world.item.crafting.RecipeManager;

import java.util.*;

public abstract class IRecipeManipulator<K, R> {

    private final HashSet<K> recipes_to_delete = new HashSet<>();
    private final HashMap<K, R> recipes_to_replace = new HashMap<>();
    private final HashMap<K, R> recipes_to_add = new HashMap<>();

    public abstract K getKey(R recipe);
    public abstract R manipulate(R recipe, GeneralManipulationData data);
    public abstract Collection<R> getCurrentRecipes(RecipeManager manager);
    public abstract Optional<R> getRecipe(RecipeManager manager, K key);

    public boolean isRealImplementation() {
        return true;
    }

    public void markRecipeForDeletion(K recipeKey) {
        recipes_to_delete.add(recipeKey);
    }

    public List<R> manipulateRecipes(Collection<R> old_recipes, GeneralManipulationData data) {

        List<R> new_recipes = new ArrayList<>(old_recipes.size());
        for (R recipe : old_recipes) {
            K key = getKey(recipe);
            if (recipes_to_delete.contains(key)) {
                continue;
            }
            if (recipes_to_replace.containsKey(key)) {
                recipe = recipes_to_replace.get(key);
            }
            recipe = manipulate(recipe, data);
            new_recipes.add(recipe);
        }

        for (R recipe : recipes_to_add.values()) {
            recipe = manipulate(recipe, data);
            new_recipes.add(recipe);
        }

        return new_recipes;
    }
}
