package de.kytodragon.live_edit.editing.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector4f;
import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.editing.gui.VanillaTextures;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ItemComponent extends MyGuiComponent {

    ItemStack itemStack;
    boolean canChange = true;
    boolean onlyOneItem = false;
    boolean onlyOneStack = true;

    public ItemComponent(int x, int y, MyIngredient.ItemIngredient ingredient, boolean onlyOneItem) {
        super(x, y, 18, 18);
        itemStack = ingredient.item;
        this.onlyOneItem = onlyOneItem;
    }

    public ItemComponent(int x, int y, MyResult.ItemResult result, boolean onlyOneItem) {
        super(x, y, 18, 18);
        itemStack = result.item;
        this.onlyOneItem = onlyOneItem;
    }

    @Override
    public void renderBackground(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        VanillaTextures.EMPTY_SLOT.draw(this, pose, x, y);
    }

    @Override
    public void renderForeground(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        if (itemStack.isEmpty())
            return;

        // Apply the translation of the current pose to the item-view-matrix
        Vector4f itemPos = new Vector4f(0, 0, 0, 1);
        itemPos.transform(pose.last().pose());
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate(itemPos.x(), itemPos.y(), 0);
        RenderSystem.applyModelViewMatrix();

        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        // blitOffset of 100 means the final image is rendered at z = 250 (+50 in ItemRenderer.tryRenderGuiItem, +100 in ItemRenderer.renderGuiItem)
        itemRenderer.blitOffset = 100.0F;
        itemRenderer.renderAndDecorateItem(itemStack, x+1, y+1);
        itemRenderer.renderGuiItemDecorations(minecraft.font, itemStack, x+1, y+1);

        // Undo translation
        posestack.popPose();
        RenderSystem.applyModelViewMatrix();

        if (isInside(mouseX, mouseY)) {

            // Z of 300 is above our item at 250 and behind floating items at 350
            fillGradient(pose, x+1, y+1, x+1 + 16, y+1 + 16, 0x80FFFFFF, 0x80FFFFFF, 300);
        }
        itemRenderer.blitOffset = 0.0F;
    }

    @Override
    public void renderOverlay(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        if (isInside(mouseX, mouseY) && !itemStack.isEmpty()) {
            List<Component> lines = itemStack.getTooltipLines(minecraft.player, minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
            Optional<TooltipComponent> image = itemStack.getTooltipImage();

            // renderTooltip will clamp the tooltip position to the screen size before the current pose ist considered.
            // Therefore translate to the current position. TODO Will tooltips correctly flip sides on the right screen edge?
            pose.pushPose();
            pose.translate(x, y, 0);
            mouseX -= x;
            mouseY -= y;
            Objects.requireNonNull(minecraft.screen);
            minecraft.screen.renderTooltip(pose, lines, image, mouseX, mouseY);
            pose.popPose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouse_button, ItemStack carried) {
        if (!isInside(mouseX, mouseY))
            return false;

        MyGuiComponent.setFocusOn(null);

        if (!canChange)
            return false;

        if (!carried.isEmpty()) {
            if (mouse_button == 0) {
                if (ItemStack.isSameItemSameTags(itemStack, carried)) {
                    // add amount if same item
                    if (!onlyOneItem) {
                        itemStack.setCount(itemStack.getCount() + carried.getCount());
                        if (onlyOneStack && itemStack.getCount() > itemStack.getMaxStackSize())
                            itemStack.setCount(itemStack.getMaxStackSize());
                    }
                } else {
                    // otherwise replace
                    itemStack = carried.copy();
                    if (onlyOneItem)
                        itemStack.setCount(1);
                }
            } else if (mouse_button == 1) {
                itemStack = carried.copy();
                if (ItemStack.isSameItemSameTags(itemStack, carried)) {
                    // subtract ammount id same item
                    itemStack.setCount(itemStack.getCount() - carried.getCount());
                    if (itemStack.isEmpty())
                        itemStack = ItemStack.EMPTY;
                } else {
                    // add one if empty
                    itemStack = carried.copy();
                    itemStack.setCount(1);
                }
            }
        } else if (!itemStack.isEmpty()) {
            if (mouse_button == 0) {
                // clear if left click
                itemStack = ItemStack.EMPTY;
            } else if (mouse_button == 1) {
                // reduce by one if right click
                itemStack.setCount(itemStack.getCount() - 1);
                if (itemStack.isEmpty())
                    itemStack = ItemStack.EMPTY;
            }
        }

        return true;
    }
}
