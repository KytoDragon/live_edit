package de.kytodragon.live_edit.recipe;

import de.kytodragon.live_edit.integration.Integration;

import java.util.*;

public abstract class IRecipeManipulator<K, R, I extends Integration> {

    private final HashSet<K> recipes_to_delete = new HashSet<>();
    private final HashMap<K, R> recipes_to_replace = new HashMap<>();
    private final HashMap<K, R> recipes_to_add = new HashMap<>();
    protected I integration;

    public abstract K getKey(R recipe);

    public abstract R manipulate(R recipe, GeneralManipulationData data);

    public abstract Collection<R> getCurrentRecipes();

    public abstract Optional<R> getRecipe(K key);

    public abstract void prepareReload(Collection<R> recipes);

    public boolean isRealImplementation() {
        return true;
    }

    public void setIntegration(I integration) {
        this.integration = integration;
    }

    public void markRecipeForDeletion(K recipeKey) {
        recipes_to_delete.add(recipeKey);
    }

    public void shutdownServer() {
        recipes_to_delete.clear();
        recipes_to_replace.clear();
        recipes_to_add.clear();
    }

    public void manipulateRecipesAndPrepareReload(GeneralManipulationData data) {
        Collection<R> old_recipes = getCurrentRecipes();
        List<R> new_recipes = manipulateRecipes(old_recipes, data);
        prepareReload(new_recipes);
    }

    public List<R> manipulateRecipes(Collection<R> old_recipes, GeneralManipulationData data) {

        List<R> new_recipes = new ArrayList<>(old_recipes.size());
        for (R recipe : old_recipes) {
            K key = getKey(recipe);
            if (key == null) {
                // If the recipe can not be handled by us and vanilla minecraft does not assign it an id already
                new_recipes.add(recipe);
                continue;
            }
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
