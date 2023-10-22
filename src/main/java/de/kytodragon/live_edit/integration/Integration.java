package de.kytodragon.live_edit.integration;

import de.kytodragon.live_edit.integration.vanilla.VanillaIntegration;
import de.kytodragon.live_edit.recipe.RecipeManager;
import net.minecraft.server.MinecraftServer;

public interface Integration {

    void registerManipulators(RecipeManager manager);

    void initServer(MinecraftServer server);

    void shutdownServer();

    void prepareReload();

    void reload();

    void acceptClientPacket(Object o);

    static void addAllIntegration(RecipeManager manager) {
        manager.addIntegration(new VanillaIntegration());
    }
}
