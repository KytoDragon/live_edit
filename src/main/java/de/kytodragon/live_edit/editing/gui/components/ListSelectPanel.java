package de.kytodragon.live_edit.editing.gui.components;

import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class ListSelectPanel extends ScrolledListPanel {

    private final int element_height;

    private final List<String> options;
    private final Consumer<String> selector;

    public ListSelectPanel(int x, int y, int width, int height, List<String> options, Consumer<String> selector) {
        super(x, y, width, height);

        this.options = options;
        this.selector = selector;
        element_height = minecraft.font.lineHeight;

        for (int i = 0; i < options.size(); i++) {
            addChild(new TextComponent(0, element_height * i, options.get(i)));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouse_button, ItemStack carried) {
        if (!isInside(mouseX, mouseY))
            return false;

        int barLeft = x + width - barWidth;
        scrolling = mouse_button == 0 && mouseX >= barLeft && mouseX < barLeft + barWidth;
        if (scrolling) {
            return true;
        }

        int mouseListY = ((int)mouseY) - y + (int)scrollDistance - border;
        int selected = mouseListY / element_height;
        if (selected >= 0 && selected < options.size()) {
            selector.accept(options.get(selected));
            return true;
        }
        return false;
    }
}
