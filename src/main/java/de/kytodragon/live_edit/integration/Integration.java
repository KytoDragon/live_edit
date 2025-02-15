package de.kytodragon.live_edit.integration;

import de.kytodragon.live_edit.integration.vanilla.VanillaIntegration;
import de.kytodragon.live_edit.recipe.RecipeManager;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;

public interface Integration {

    void registerManipulators(RecipeManager manager);

    void initServer(MinecraftServer server, Path data_path);

    void shutdownServer(Path data_path);

    static void addAllIntegration(RecipeManager manager) {
        manager.addIntegration(new VanillaIntegration());
    }
}
