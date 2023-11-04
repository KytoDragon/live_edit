package de.kytodragon.live_edit.editing.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector4f;
import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.editing.gui.VanillaTextures;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ItemComponent extends MyGuiComponent {

    public ItemStack itemStack;
    public boolean can_change = true;
    public boolean only_one_item = false;
    public boolean only_one_stack = true;

    public boolean draw_result_slot = false;
    public boolean no_background = false;

    public ItemComponent(int x, int y, MyIngredient.ItemIngredient ingredient) {
        this(x, y, ingredient.item);
    }

    public ItemComponent(int x, int y, MyResult.ItemResult result) {
        this(x, y, result.item);
    }

    public ItemComponent(int x, int y, ItemStack item) {
        super(x, y, 18, 18);
        itemStack = item;
    }

    @Override
    public void renderBackground(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        if (!no_background) {
            if (draw_result_slot) {
                VanillaTextures.RESULT_SLOT.draw(this, pose, x - 4, y - 4);
            } else {
                VanillaTextures.EMPTY_SLOT.draw(this, pose, x, y);
            }
        }
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

            if (minecraft.options.advancedItemTooltips) {
                String modid = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(itemStack.getItem())).getNamespace();
                String modname = ModList.get().getModContainerById(modid)
                    .map(ModContainer::getModInfo)
                    .map(IModInfo::getDisplayName)
                    .orElseGet(() -> StringUtils.capitalize(modid));
                String formatting = ChatFormatting.BLUE.toString() + ChatFormatting.ITALIC.toString();
                lines.add(Component.literal(formatting+modname));
            }

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

        if (!can_change)
            return false;

        if (!carried.isEmpty()) {
            if (mouse_button == 0) {
                if (ItemStack.isSameItemSameTags(itemStack, carried)) {
                    // add amount if same item
                    if (!only_one_item) {
                        itemStack.setCount(itemStack.getCount() + carried.getCount());
                        if (only_one_stack && itemStack.getCount() > itemStack.getMaxStackSize())
                            itemStack.setCount(itemStack.getMaxStackSize());
                    }
                } else {
                    // otherwise replace
                    itemStack = carried.copy();
                    if (only_one_item)
                        itemStack.setCount(1);
                }
            } else if (mouse_button == 1) {
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
