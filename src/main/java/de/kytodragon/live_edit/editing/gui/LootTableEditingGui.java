package de.kytodragon.live_edit.editing.gui;

import de.kytodragon.live_edit.LiveEditMod;
import de.kytodragon.live_edit.editing.*;
import de.kytodragon.live_edit.editing.gui.components.Background;
import de.kytodragon.live_edit.editing.gui.components.Button;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;
import de.kytodragon.live_edit.editing.gui.loot_tables.LootTableInput;
import de.kytodragon.live_edit.integration.PacketRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

public class LootTableEditingGui extends GuiCommon<LootTableEditingMenu> {

    private static final ResourceLocation MENU_TYPE_ID = new ResourceLocation(LiveEditMod.MODID, "loot_table_editing_menu");

    private MyLootTable loot_table;

    private final LootTableInput loot_table_editor;

    public LootTableEditingGui(LootTableEditingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 176;

        this.inventoryLabelY = this.imageHeight - 94;

        loot_table_editor = new LootTableInput(10, 10);

        components.add(new Background(0, 0, 176, 176));
        components.add(loot_table_editor);
        components.add(menu.inventoryGui);
        components.add(new Button(100, 80, 30, 12, "Save", this::sendLootTableToServer));
    }

    public static void clientSetup(RegisterEvent event) {
        if (event.getRegistryKey() == ForgeRegistries.MENU_TYPES.getRegistryKey()) {
            ForgeRegistries.MENU_TYPES.register(MENU_TYPE_ID, LootTableEditingMenu.MENU_TYPE);
            MenuScreens.register(LootTableEditingMenu.MENU_TYPE, LootTableEditingGui::new);
        }
    }

    @Override
    protected void containerTick() {

        if (loot_table == null) {
            loot_table = menu.loot_table_slot.getLootTable();

            if (loot_table != null) {
                loot_table_editor.setLootTable(loot_table);
            }
        }

        super.containerTick();
    }

    private void sendLootTableToServer() {
        if (loot_table == null)
            return;

        MyLootTable new_loot_table = loot_table_editor.getLootTable();

        EditCommandPacket packet = new EditCommandPacket();
        packet.command = "replace loot_table " + new_loot_table.id.toString() + " " + new_loot_table.toJsonString();
        PacketRegistry.INSTANCE.sendToServer(packet);

        onClose();
    }

    @Override
    public void onClose() {
        super.onClose();
        MyGuiComponent.setFocusOn(null);
    }
}
