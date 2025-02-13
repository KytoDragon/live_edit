package de.kytodragon.live_edit.editing.gui.recipes;

import de.kytodragon.live_edit.LiveEditMod;
import de.kytodragon.live_edit.editing.gui.MenuCommon;
import de.kytodragon.live_edit.recipe.RecipeType;

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

public class RecipeEditingMenu extends MenuCommon {

    private static final DeferredRegister<MenuType<?>> MENU_TYPE_REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, LiveEditMod.MODID);
    public static final RegistryObject<MenuType<RecipeEditingMenu>> MENU_TYPE = MENU_TYPE_REGISTRY.register("recipe_editing_menu", () -> new MenuType<>(RecipeEditingMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static void registerMenu(FMLJavaModLoadingContext context) {
        MENU_TYPE_REGISTRY.register(context.getModEventBus());
        context.getModEventBus().addListener(RecipeEditingMenu::clientSetup);
    }

    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(
                () -> MenuScreens.register(RecipeEditingMenu.MENU_TYPE.get(), RecipeEditingGui::new)
        );
    }

    public RecipeIDSlot recipe_slot;

    public RecipeEditingMenu(int containerId, Inventory inventory, RecipeType type, ResourceLocation recipe_id) {
        // Server constructor
        this(containerId, inventory);

        recipe_slot.type = type;
        recipe_slot.id = recipe_id;
    }

    public RecipeEditingMenu(int containerId, Inventory inventory) {
        // Server + Client constructor
        super(MENU_TYPE.get(), containerId, inventory, false);

        // Minecraft can only synchronize items and integers when dealing with menus.
        // Everything else will have to deal with custom packets.
        // As a workaround, we add a hiden item slot with an item that contains the recipe id as a NBT tag.
        recipe_slot = new RecipeIDSlot(27);
        addSlot(recipe_slot);
    }
}
