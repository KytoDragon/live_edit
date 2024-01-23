package de.kytodragon.live_edit.editing.gui;

import de.kytodragon.live_edit.editing.gui.components.InventoryGui;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class LootTableEditingMenu extends AbstractContainerMenu {

    public static final MenuType<LootTableEditingMenu> MENU_TYPE = new MenuType<>(LootTableEditingMenu::new);
    public InventoryGui inventoryGui;
    public LootTableIDSlot loot_table_slot;

    public LootTableEditingMenu(int containerId, Inventory inventory, ResourceLocation loot_table_id) {
        // Server constructor
        this(containerId, inventory);

        loot_table_slot.id = loot_table_id;
    }

    public LootTableEditingMenu(int containerId, Inventory inventory) {
        // Server + Client constructor
        super(MENU_TYPE, containerId);

        inventoryGui = new InventoryGui(inventory, 7, 93);
        inventoryGui.addSlots(super::addSlot);

        // Minecraft can only synchronize items and integers when dealing with menus.
        // Everything else will have to deal with custom packets.
        // As a workaround, we add a hiden item slot with an item that contains the recipe id as a NBT tag.
        loot_table_slot = new LootTableIDSlot(27);
        addSlot(loot_table_slot);
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
