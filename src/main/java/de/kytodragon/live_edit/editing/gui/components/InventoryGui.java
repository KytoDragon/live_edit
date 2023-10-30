package de.kytodragon.live_edit.editing.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import de.kytodragon.live_edit.editing.gui.VanillaTextures;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InventoryGui extends MyGuiComponent {
    Inventory inventory;
    int inventoryBaseSlotID;
    List<Slot> slots = new ArrayList<>(27);

    public InventoryGui(Inventory inventory, int inventoryX, int inventoryY, int inventoryBaseSlotID) {
        super(inventoryX, inventoryY, 9*18, 4*18 + 4);
        this.inventory = inventory;
        this.inventoryBaseSlotID = inventoryBaseSlotID;
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
    public void renderBackground(PoseStack pose, float partialTick, int mouseX, int mouseY) {

        for (Slot slot : slots) {
            VanillaTextures.EMPTY_SLOT.draw(this, pose, slot.x - 1, slot.y - 1);
        }
    }
}
