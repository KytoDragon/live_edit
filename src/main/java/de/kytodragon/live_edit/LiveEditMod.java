package de.kytodragon.live_edit;

import com.mojang.logging.LogUtils;
import de.kytodragon.live_edit.command.Command;
import de.kytodragon.live_edit.editing.EditCommandPacket;
import de.kytodragon.live_edit.editing.gui.loot_tables.LootTableEditingMenu;
import de.kytodragon.live_edit.editing.gui.recipes.RecipeEditingMenu;
import de.kytodragon.live_edit.integration.Integration;
import de.kytodragon.live_edit.integration.PacketRegistry;
import de.kytodragon.live_edit.recipe.RecipeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.slf4j.Logger;

@Mod(LiveEditMod.MODID)
public class LiveEditMod {

    public static final String MODID = "live_edit";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LiveEditMod(FMLJavaModLoadingContext context) {
        PacketRegistry.registerServerPacket(EditCommandPacket.class, EditCommandPacket::new);
        Integration.addAllIntegration(RecipeManager.instance);

        context.getModEventBus().addListener(this::onModLoad);
        MinecraftForge.EVENT_BUS.addListener(Command::onRegisterCommandEvent);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStopping);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);

        RecipeEditingMenu.registerMenu(context);
        LootTableEditingMenu.registerMenu(context);
    }

    public void onModLoad(FMLCommonSetupEvent event) {
        RecipeManager.instance.data_path = FMLLoader.getGamePath().toAbsolutePath();
    }

    public void onServerStarting(ServerStartedEvent event) {
        RecipeManager.instance.initServer(event.getServer());
    }

    public void onServerStopping(ServerStoppedEvent event) {
        RecipeManager.instance.shutdownServer();
    }
}
