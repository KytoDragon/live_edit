package de.kytodragon.live_edit.editing.gui.loot_tables;

import de.kytodragon.live_edit.LiveEditMod;
import de.kytodragon.live_edit.editing.*;
import de.kytodragon.live_edit.editing.gui.GuiCommon;
import de.kytodragon.live_edit.editing.gui.components.Button;
import de.kytodragon.live_edit.integration.PacketRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LootTableEditingGui extends GuiCommon<LootTableEditingMenu> {

    private MyLootTable loot_table;

    private final LootTableInput loot_table_editor;

    public LootTableEditingGui(LootTableEditingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        loot_table_editor = new LootTableInput(10, 16, imageWidth - 14, imageHeight - 120);

        content.addChild(loot_table_editor);
        content.addChild(new Button(100, this.inventoryLabelY - 2, 30, 12, "Save", this::sendLootTableToServer));
    }

    @Override
    protected void containerTick() {

        try {
            if (loot_table == null) {
                loot_table = menu.loot_table_slot.getLootTable();

                if (loot_table != null) {
                    loot_table_editor.setLootTable(loot_table);
                    content.calculateBounds();
                }
            }
        } catch (Exception e) {
            LiveEditMod.LOGGER.error("Cought error in loot table GUI tick-Method", e);
            onClose();
        }

        super.containerTick();
    }

    private void sendLootTableToServer() {
        if (loot_table == null)
            return;

        MyLootTable new_loot_table = loot_table_editor.getLootTable();

        EditCommandPacket packet = new EditCommandPacket();
        packet.command = "replace loot_table " + new_loot_table.id.toString() + " " + new_loot_table.toJson().toString();
        PacketRegistry.INSTANCE.sendToServer(packet);

        onClose();
    }
}
