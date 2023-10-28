package de.kytodragon.live_edit.editing.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class RecipeEditingMenu extends AbstractContainerMenu {

    public static final MenuType<RecipeEditingMenu> MENU_TYPE = new MenuType<>(RecipeEditingMenu::new);
    public InventoryGui inventoryGui;

    public RecipeEditingMenu(int containerId, Inventory inventory) {
        // Client constructor
        this(containerId, inventory, Minecraft.getInstance().player);
    }

    public RecipeEditingMenu(int containerId, Inventory inventory, Player player) {
        // Server + Client constructor
        super(MENU_TYPE, containerId);

        inventoryGui = new InventoryGui(inventory, 7, 93, slots.size());
        inventoryGui.addSlots(super::addSlot);
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
