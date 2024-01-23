package de.kytodragon.live_edit;

import com.mojang.logging.LogUtils;
import de.kytodragon.live_edit.command.Command;
import de.kytodragon.live_edit.editing.EditCommandPacket;
import de.kytodragon.live_edit.editing.gui.LootTableEditingGui;
import de.kytodragon.live_edit.editing.gui.RecipeEditingGui;
import de.kytodragon.live_edit.integration.Integration;
import de.kytodragon.live_edit.integration.PacketRegistry;
import de.kytodragon.live_edit.recipe.RecipeManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(LiveEditMod.MODID)
public class LiveEditMod {

    public static final String MODID = "live_edit";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LiveEditMod() {
        PacketRegistry.registerServerPacket(EditCommandPacket.class, EditCommandPacket::new);
        Integration.addAllIntegration(RecipeManager.instance);

        MinecraftForge.EVENT_BUS.addListener(this::onServerStopping);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
        MinecraftForge.EVENT_BUS.addListener(Command::onRegisterCommandEvent);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLogin);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(RecipeEditingGui::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(LootTableEditingGui::clientSetup);
    }

    public void onServerStarting(ServerStartedEvent event) {
        RecipeManager.instance.initServer(event.getServer());
        RecipeManager.instance.manipulateAllRecipesAndReload();
    }

    public void onServerStopping(ServerStoppedEvent event) {
        RecipeManager.instance.shutdownServer();
    }

    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        RecipeManager.instance.informNewPlayer((ServerPlayer) event.getEntity());
    }
}
