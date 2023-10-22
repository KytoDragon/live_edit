package de.kytodragon.live_edit.recipe;

import de.kytodragon.live_edit.integration.Integration;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.server.MinecraftServer;
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

    public void markItemForReplacement(Item item, Item replacement) {
        data.itemsToReplace.put(item, replacement);
    }

    public void addIntegration(Integration integration) {
        integrations.add(integration);
        integration.registerManipulators(this);
    }

    public <I extends Integration> void addRecipeManipulator(I integration, RecipeType type, IRecipeManipulator<ResourceLocation, ?, I> manipulator) {
        manipulator.setIntegration(integration);
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
        data = new GeneralManipulationData();
    }

    public void handleClientPacket(Object o) {
        integrations.forEach(integration -> integration.acceptClientPacket(o));
    }
}
