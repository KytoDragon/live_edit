package de.kytodragon.live_edit.editing.gui.components;

import com.google.common.collect.Iterators;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector4f;
import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.editing.gui.VanillaTextures;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.IReverseTag;
import net.minecraftforge.registries.tags.ITag;
import net.minecraftforge.registries.tags.ITagManager;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TagComponent extends MyGuiComponent {

    public TagKey<Item> tag;
    public int amount;
    public boolean can_change = true;

    private int cycle;
    private int current_item_pos;
    public ItemStack current_item;

    private int current_tag_pos;
    private Item last_clicked_item;

    private final ITagManager<Item> tag_manager;

    public TagComponent(int x, int y, MyIngredient.TagIngredient tag) {
        this(x, y, tag.tag);
    }

    public TagComponent(int x, int y, MyResult.TagResult tag) {
        this(x, y, tag.tag);
    }

    public TagComponent(int x, int y, TagKey<Item> tag) {
        super(x, y, 18, 18);
        tag_manager = ForgeRegistries.ITEMS.tags();
        setTag(tag);
    }

    public void setTag(TagKey<Item> tag) {
        this.tag = tag;
        current_item = ItemStack.EMPTY;
        current_item_pos = 0;
        cycle = 0;
        if (tag == null)
            amount = 0;
        tick();
    }

    public void setAmount(int amount) {
        this.amount = amount;
        if (!current_item.isEmpty()) {
            current_item.setCount(amount);
        }
    }

    public void setTagFromItem(Item item, boolean reset_tags) {
        Optional<IReverseTag<Item>> reverse_tag = tag_manager.getReverseTag(item);
        if (reverse_tag.isEmpty()) {
            current_tag_pos = 0;
            setTag(null);
        } else {
            List<ITag<Item>> tags = reverse_tag.get().getTagKeys().map(tag_manager::getTag)
                                        .sorted(Comparator.comparingInt(ITag::size)).toList();
            if (tags.isEmpty()) {
                current_tag_pos = 0;
                setTag(null);
            } else {
                if (reset_tags || last_clicked_item != item)
                    current_tag_pos = 0;
                current_tag_pos = current_tag_pos % tags.size();

                setTag(tags.get(current_tag_pos).getKey());
                current_tag_pos++;
            }
        }
        last_clicked_item = item;
    }

    @Override
    public void renderBackground(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        VanillaTextures.EMPTY_SLOT.draw(this, pose, x, y);
    }

    @Override
    public void renderForeground(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        if (tag == null || current_item.isEmpty())
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
        itemRenderer.renderAndDecorateItem(current_item, x+1, y+1);
        itemRenderer.renderGuiItemDecorations(minecraft.font, current_item, x+1, y+1);

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
        if (tag == null || current_item.isEmpty())
            return;

        if (isInside(mouseX, mouseY)) {
            List<Component> lines = current_item.getTooltipLines(minecraft.player, minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
            Optional<TooltipComponent> image = current_item.getTooltipImage();

            lines.add(Component.translatable("live_edit.tooltip.tags", tag.location().toString()));

            if (minecraft.options.advancedItemTooltips) {
                String modid = tag.location().getNamespace();
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
    public void tick() {
        if (tag == null)
            return;

        cycle--;
        if (cycle <= 0) {
            cycle = 20;
            current_item_pos++;

            ITag<Item> itag = tag_manager.getTag(tag);
            if (itag.isEmpty())
                return;

            if (current_item_pos >= itag.size())
                current_item_pos -= itag.size();
            if (current_item_pos < 0)
                current_item_pos += itag.size();

            Item item = Iterators.get(itag.iterator(), current_item_pos);
            current_item = new ItemStack(item);
            current_item.setCount(amount);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (tag == null)
            return false;

        if (Screen.hasShiftDown()) {
            if (scroll > 0) {
                current_item_pos -= 2;
            }
            cycle = 0;
            tick();
        }
        return false;
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
                setTagFromItem(carried.getItem(), false);
            }
        } else if (tag != null) {
            if (mouse_button == 0) {
                setTag(null);
            }
        }

        return true;
    }
}
