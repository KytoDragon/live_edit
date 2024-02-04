package de.kytodragon.live_edit.editing.gui.recipes;

import de.kytodragon.live_edit.editing.gui.MenuCommon;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class RecipeEditingMenu extends MenuCommon {

    public static final MenuType<RecipeEditingMenu> MENU_TYPE = new MenuType<>(RecipeEditingMenu::new);
    public RecipeIDSlot recipe_slot;

    public RecipeEditingMenu(int containerId, Inventory inventory, RecipeType type, ResourceLocation recipe_id) {
        // Server constructor
        this(containerId, inventory);

        recipe_slot.type = type;
        recipe_slot.id = recipe_id;
    }

    public RecipeEditingMenu(int containerId, Inventory inventory) {
        // Server + Client constructor
        super(MENU_TYPE, containerId, inventory, false);

        // Minecraft can only synchronize items and integers when dealing with menus.
        // Everything else will have to deal with custom packets.
        // As a workaround, we add a hiden item slot with an item that contains the recipe id as a NBT tag.
        recipe_slot = new RecipeIDSlot(27);
        addSlot(recipe_slot);
    }
}
