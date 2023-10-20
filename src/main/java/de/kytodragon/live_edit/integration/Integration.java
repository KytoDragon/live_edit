package de.kytodragon.live_edit.integration;

import de.kytodragon.live_edit.recipe.RecipeManager;

public interface Integration {

    void registerManipulators(RecipeManager manager);
}
