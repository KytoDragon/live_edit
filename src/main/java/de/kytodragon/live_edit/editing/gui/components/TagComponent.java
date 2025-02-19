package de.kytodragon.live_edit.editing.gui.components;

import com.google.common.collect.Iterators;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.Registries;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

    public TagComponent(int x, int y) {
        super(x, y, 18, 18);
        tag_manager = ForgeRegistries.ITEMS.tags();
        setTag(null);
    }

    public void setTag(TagKey<Item> tag) {
        this.tag = tag;
        current_item = ItemStack.EMPTY;
        current_item_pos = 0;
        cycle = 0;
        if (amount == 0)
            amount = 1;
        tick();
    }

    public void setTagId(ResourceLocation id) {
        setTag(TagKey.create(Registries.ITEM, id));
    }

    public ResourceLocation getTagId() {
        if (tag == null) {
            return null;
        } else {
            return tag.location();
        }
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
    public void renderBackground(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        VanillaTextures.EMPTY_SLOT.draw(graphics, x, y);
    }

    @Override
    public void renderForeground(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        if (tag == null || current_item.isEmpty())
            return;

        graphics.pose().pushPose();
        // blitOffset of 100 means the final image is rendered at z = 250 (+50 in ItemRenderer.tryRenderGuiItem, +100 in ItemRenderer.renderGuiItem)
        graphics.pose().translate(0, 0, 100.0F);

        graphics.renderItem(current_item, x+1, y+1);
        graphics.renderItemDecorations(minecraft.font, current_item, x+1, y+1);

        // Undo translation
        graphics.pose().popPose();

        if (isInside(mouseX, mouseY)) {
            // Z of 300 (100 + 200) is above our item at 250 and behind floating items at 350
            graphics.fillGradient(x+1, y+1, x+1 + 16, y+1 + 16, 0x80FFFFFF, 0x80FFFFFF, 200);
        }
    }

    @Override
    public void renderOverlay(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
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
            graphics.pose().pushPose();
            graphics.pose().translate(x, y, 0);
            mouseX -= x;
            mouseY -= y;
            Objects.requireNonNull(minecraft.screen);
            graphics.renderTooltip(minecraft.font, lines, image, mouseX, mouseY);
            graphics.pose().popPose();
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
