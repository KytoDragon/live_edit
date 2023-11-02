package de.kytodragon.live_edit.recipe;

import de.kytodragon.live_edit.editing.EditCommandPacket;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.integration.Integration;
import de.kytodragon.live_edit.integration.LiveEditPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.*;

import static de.kytodragon.live_edit.LiveEditMod.LOGGER;

public class RecipeManager {

    public static final RecipeManager instance = new RecipeManager();

    public HashMap<RecipeType, IRecipeManipulator<ResourceLocation, ?, ?>> manipulators = new HashMap<>();
    public List<Integration> integrations = new ArrayList<>();
    public GeneralManipulationData data;

    public void markRecipeForDeletion(RecipeType type, ResourceLocation recipeKey) {
        manipulators.get(type).markRecipeForDeletion(recipeKey);
    }

    public void markRecipeForAddition(RecipeType type, ResourceLocation recipeKey, MyRecipe recipe) {
        manipulators.get(type).markRecipeForAddition(recipeKey, recipe);
    }

    public void markRecipeForReplacement(RecipeType type, ResourceLocation recipeKey, MyRecipe recipe) {
        manipulators.get(type).markRecipeForReplacement(recipeKey, recipe);
    }

    public void markItemForReplacement(Item item, Item replacement) {
        data.itemsToReplace.put(item, replacement);
    }

    public void addIntegration(Integration integration) {
        integrations.add(integration);
        integration.registerManipulators(this);
    }

    public <I extends Integration> void addRecipeManipulator(I integration, RecipeType type, IRecipeManipulator<ResourceLocation, ?, I> manipulator) {
        manipulator.setIntegration(integration);
        manipulator.setRecipeType(type);
        // Remove dummy manipulators. Just a put is not enough, as the recipe type in the key will still be set to "Dummy".
        manipulators.remove(type);
        manipulators.put(type, manipulator);
    }

    public void manipulateAllRecipesAndReload() {

        try {
            integrations.forEach(Integration::prepareReload);
        } catch (Exception e) {
            LOGGER.info("Failed to prepare recipe replacement: ", e);
            return;
        }

        try {
            manipulators.values().forEach(m -> m.manipulateRecipesAndPrepareReload(data));
        } catch (Exception e) {
            LOGGER.info("Failed to replace recipes: ", e);
            return;
        }

        try {
            integrations.forEach(Integration::reload);
        } catch (Exception e) {
            LOGGER.info("Failed to prepare recipe replacement: ", e);
            return;
        }
    }

    public void initServer(MinecraftServer server) {
        data = new GeneralManipulationData();
        integrations.forEach(i -> i.initServer(server));
    }

    public void shutdownServer() {
        manipulators.forEach((k, s) -> s.shutdownServer());
        integrations.forEach(Integration::shutdownServer);
        data = null;
    }

    public void handleClientPacket(LiveEditPacket packet) {
        integrations.forEach(integration -> integration.acceptClientPacket(packet));
    }

    public void handleServerPacket(LiveEditPacket packet, ServerPlayer player) {

        if (packet instanceof EditCommandPacket commandPacket) {

            CommandSourceStack commandsourcestack = player.createCommandSourceStack();
            String command = "/live-edit " + commandPacket.command;
            player.server.getCommands().performPrefixedCommand(commandsourcestack, command);
            return;
        }
    }

    public void informNewPlayer(ServerPlayer player) {
        integrations.forEach(integration -> integration.informNewPlayer(player));
    }
}
