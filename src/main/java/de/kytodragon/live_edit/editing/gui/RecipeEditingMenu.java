package de.kytodragon.live_edit.editing.gui;

import de.kytodragon.live_edit.editing.gui.components.InventoryGui;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class RecipeEditingMenu extends AbstractContainerMenu {

    public static final MenuType<RecipeEditingMenu> MENU_TYPE = new MenuType<>(RecipeEditingMenu::new);
    public InventoryGui inventoryGui;
    public RecipeIDSlot recipe_slot;

    public RecipeEditingMenu(int containerId, Inventory inventory, RecipeType type, ResourceLocation recipe_id) {
        // Server constructor
        this(containerId, inventory);

        recipe_slot.type = type;
        recipe_slot.id = recipe_id;
    }

    public RecipeEditingMenu(int containerId, Inventory inventory) {
        // Server + Client constructor
        super(MENU_TYPE, containerId);

        inventoryGui = new InventoryGui(inventory, 7, 93);
        inventoryGui.addSlots(super::addSlot);

        // Minecraft can only synchronize items and integers when dealing with menus.
        // Everything else will have to deal with custom packets.
        // As a workaround, we add a hiden item slot with an item that contains the recipe id as a NBT tag.
        recipe_slot = new RecipeIDSlot(27);
        addSlot(recipe_slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot_index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
