package de.kytodragon.live_edit;

import com.mojang.logging.LogUtils;
import de.kytodragon.live_edit.command.Command;
import de.kytodragon.live_edit.integration.Integration;
import de.kytodragon.live_edit.recipe.RecipeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(LiveEditMod.MODID)
public class LiveEditMod {

    public static final String MODID = "live_edit";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LiveEditMod() {
        Integration.addAllIntegration(RecipeManager.instance);

        MinecraftForge.EVENT_BUS.addListener(this::onServerStopping);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
        MinecraftForge.EVENT_BUS.addListener(Command::onRegisterCommandEvent);
    }

    public void onServerStarting(ServerStartedEvent event) {
        LOGGER.info("HELLO from server started");

        RecipeManager.instance.initServer(event.getServer());
        RecipeManager.instance.manipulateAllRecipesAndReload();
    }

    public void onServerStopping(ServerStoppedEvent event) {
        RecipeManager.instance.shutdownServer();
    }
}
