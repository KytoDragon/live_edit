package de.kytodragon.live_edit.recipe;

import com.google.gson.JsonObject;
import de.kytodragon.live_edit.editing.EditCommandPacket;
import de.kytodragon.live_edit.integration.Integration;
import de.kytodragon.live_edit.integration.LiveEditPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static de.kytodragon.live_edit.LiveEditMod.LOGGER;

public class RecipeManager {

    public static final RecipeManager instance = new RecipeManager();

    public HashMap<RecipeType, IRecipeManipulator<?, ?, ?>> manipulators = new HashMap<>();
    public List<Integration> integrations = new ArrayList<>();
    public GeneralManipulationData data;
    public Path data_path;
    public Path script_path;

    public void markRecipeForDeletion(RecipeType type, ResourceLocation recipeKey) {
        manipulators.get(type).markRecipeForDeletion(recipeKey);
    }

    public void markRecipeForAddition(RecipeType type, ResourceLocation recipeKey, JsonObject recipe) {
        manipulators.get(type).markRecipeForAddition(recipeKey, recipe);
    }

    public void markItemForReplacement(Item item, Item replacement) {
        data.itemsToReplace.put(item, replacement);
    }

    public void addIntegration(Integration integration) {
        integrations.add(integration);
        integration.registerManipulators(this);
    }

    public <I extends Integration> void addRecipeManipulator(I integration, RecipeType type, IRecipeManipulator<?, ?, I> manipulator) {
        manipulator.setIntegration(integration);
        manipulator.setRecipeType(type);
        // Remove dummy manipulators. Just a put is not enough, as the recipe type in the key will still be set to "Dummy".
        manipulators.remove(type);
        manipulators.put(type, manipulator);
    }

    public void loadAllRecipes() {
        try {
            manipulators.values().forEach(m -> m.loadRecipes(data_path));
        } catch (Exception e) {
            LOGGER.error("Failed to load recipes: ", e);
            return;
        }
    }

    public void saveAllRecipes() {
        try {
            if (!Files.exists(data_path))
                Files.createDirectory(data_path);
            manipulators.values().forEach(m -> m.saveRecipes(data_path));
        } catch (Exception e) {
            LOGGER.error("Failed to save recipes: ", e);
            return;
        }
    }

    public void exportAllRecipes() {
        try {
            if (!Files.exists(script_path))
                Files.createDirectory(script_path);
            manipulators.values().forEach(m -> m.exportRecipes(script_path));
        } catch (Exception e) {
            LOGGER.error("Failed to save recipes: ", e);
            return;
        }
    }

    public void initServer(MinecraftServer server) {
        data = new GeneralManipulationData();
        integrations.forEach(i -> i.initServer(server, data_path));
        loadAllRecipes();
    }

    public void shutdownServer() {
        saveAllRecipes();
        manipulators.forEach((k, s) -> s.shutdownServer());
        integrations.forEach(i -> i.shutdownServer(data_path));
        data = null;
    }

    public void handleServerPacket(LiveEditPacket packet, ServerPlayer player) {

        if (packet instanceof EditCommandPacket commandPacket) {

            CommandSourceStack commandsourcestack = player.createCommandSourceStack();
            String command = "/live-edit " + commandPacket.command;
            player.server.getCommands().performPrefixedCommand(commandsourcestack, command);
            return;
        }
    }

    public void setPaths(Path root_path) {
        data_path = root_path.resolve("live_edit");
        script_path = root_path.resolve("scripts/live_edit");
    }
}
