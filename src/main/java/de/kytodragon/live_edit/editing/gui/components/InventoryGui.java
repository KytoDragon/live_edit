package de.kytodragon.live_edit.editing.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InventoryGui extends MyGuiComponent {
    Inventory inventory;
    List<Slot> slots = new ArrayList<>(27);

    public InventoryGui(Inventory inventory, int inventoryX, int inventoryY) {
        super(inventoryX, inventoryY, 9*18, 4*18 + 4);
        this.inventory = inventory;
    }

    // Uses a consumer as AbstractContainerMenu.addSlot is not public
    public void addSlots(Consumer<Slot> slotAdder) {

        for (int row = 0; row < 4; row++) {
            int y = this.y;
            if (row == 0) {
                y += 58;
            } else {
                y += 18 * (row - 1);
            }
            for (int collumn = 0; collumn < 9; collumn++) {
                int x = this.x + collumn * 18;
                Slot slot = new Slot(inventory, row * 9 + collumn, x + 1, y + 1);
                slots.add(slot);
                slotAdder.accept(slot);
            }
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {

        for (Slot slot : slots) {
            VanillaTextures.EMPTY_SLOT.draw(graphics, slot.x - 1, slot.y - 1);
        }
    }
}
