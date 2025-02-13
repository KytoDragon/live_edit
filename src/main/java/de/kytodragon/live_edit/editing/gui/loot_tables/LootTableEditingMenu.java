package de.kytodragon.live_edit.editing.gui.loot_tables;

import de.kytodragon.live_edit.LiveEditMod;
import de.kytodragon.live_edit.editing.gui.MenuCommon;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LootTableEditingMenu extends MenuCommon {

    private static final DeferredRegister<MenuType<?>> MENU_TYPE_REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, LiveEditMod.MODID);
    public static final RegistryObject<MenuType<LootTableEditingMenu>> MENU_TYPE = MENU_TYPE_REGISTRY.register("loot_table_editing_menu", () -> new MenuType<>(LootTableEditingMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public LootTableIDSlot loot_table_slot;

    public static void registerMenu(FMLJavaModLoadingContext context) {
        MENU_TYPE_REGISTRY.register(context.getModEventBus());
        context.getModEventBus().addListener(LootTableEditingMenu::clientSetup);
    }

    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(
                () -> MenuScreens.register(LootTableEditingMenu.MENU_TYPE.get(), LootTableEditingGui::new)
        );
    }

    public LootTableEditingMenu(int containerId, Inventory inventory, ResourceLocation loot_table_id) {
        // Server constructor
        this(containerId, inventory);

        loot_table_slot.id = loot_table_id;
    }

    public LootTableEditingMenu(int containerId, Inventory inventory) {
        // Server + Client constructor
        super(MENU_TYPE.get(), containerId, inventory, true);

        // Minecraft can only synchronize items and integers when dealing with menus.
        // Everything else will have to deal with custom packets.
        // As a workaround, we add a hiden item slot with an item that contains the recipe id as a NBT tag.
        loot_table_slot = new LootTableIDSlot(27);
        addSlot(loot_table_slot);
    }
}
