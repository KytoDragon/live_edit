package de.kytodragon.live_edit.editing.gui;

import de.kytodragon.live_edit.editing.gui.components.InventoryGui;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class MenuCommon extends AbstractContainerMenu {

    public int imageWidth;
    public int imageHeight;
    public InventoryGui inventoryGui;

    public MenuCommon(MenuType type, int containerId, Inventory inventory, boolean scaleToScreen) {
        // Server + Client constructor
        super(type, containerId);

        imageWidth = 176;
        imageHeight = 176;
        if (scaleToScreen) {
            Integer width = DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> GUITools::getScreenWidth);
            if (width != null)
                imageWidth = width.intValue() / 2 ;
            Integer height = DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> GUITools::getScreenHeight);
            if (height != null)
                imageHeight = height.intValue() * 3/4;

            // Allign to 16 pixels as the background is rendered in 16x16 tiles
            imageWidth = (imageWidth / 16) * 16;
            imageHeight = (imageHeight / 16) * 16;
        }

        inventoryGui = new InventoryGui(inventory, (imageWidth - 9*18) / 2, imageHeight - 82);
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
