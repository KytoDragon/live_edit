package de.kytodragon.live_edit.recipe;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;

public class RecipeReloader {

    public static void changeRecipes(MinecraftServer server) {
        List<Recipe<?>> new_recipes = RecipeManager.instance.manipulateAllRecipes(server);
        RecipeManager.instance.manipulateAllItemTags();

        server.getRecipeManager().replaceRecipes(new_recipes);

        updateClientRecipes(server);
    }

    private static void updateClientRecipes(MinecraftServer server) {

        server.getPlayerList().saveAll();
        server.getPlayerList().reloadResources();
    }
}
