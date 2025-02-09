package de.kytodragon.live_edit.editing.gui.loot_tables;

import de.kytodragon.live_edit.editing.gui.MenuCommon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;

public class LootTableEditingMenu extends MenuCommon {

    public static final MenuType<LootTableEditingMenu> MENU_TYPE = new MenuType<>(LootTableEditingMenu::new, FeatureFlagSet.of());
    public LootTableIDSlot loot_table_slot;

    public LootTableEditingMenu(int containerId, Inventory inventory, ResourceLocation loot_table_id) {
        // Server constructor
        this(containerId, inventory);

        loot_table_slot.id = loot_table_id;
    }

    public LootTableEditingMenu(int containerId, Inventory inventory) {
        // Server + Client constructor
        super(MENU_TYPE, containerId, inventory, true);

        // Minecraft can only synchronize items and integers when dealing with menus.
        // Everything else will have to deal with custom packets.
        // As a workaround, we add a hiden item slot with an item that contains the recipe id as a NBT tag.
        loot_table_slot = new LootTableIDSlot(27);
        addSlot(loot_table_slot);
    }
}
