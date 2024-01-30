package de.kytodragon.live_edit.editing.gui.components;

import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class ListSelectBox extends MyGuiComponent {

    private final TextComponent label;
    private final ListSelectPanel select_popup;
    private final Consumer<String> selector;

    public ListSelectBox(int x, int y, int width, List<String> options, Consumer<String> selector) {
        super(x, y, width, 0);
        height = minecraft.font.lineHeight+2;

        this.selector = selector;
        select_popup = new ListSelectPanel(0, 0, width, height * 5, options, this::selecValue);
        select_popup.parent = this;

        label = new TextComponent(0, 1, options.get(0));
        addChild(label);
    }

    public String getValue() {
        return label.getText();
    }

    public void setValue(String value) {
        label.setText(value);
        if (MyGuiComponent.popup == select_popup) {
            MyGuiComponent.popup = null;
        }
    }

    private void selecValue(String value) {
        label.setText(value);
        if (selector != null)
            selector.accept(value);
        if (MyGuiComponent.popup == select_popup) {
            MyGuiComponent.popup = null;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouse_button, ItemStack carried) {
        if (!isInside(mouseX, mouseY))
            return false;

        MyGuiComponent.setPopup(select_popup, 0, 0);
        return true;
    }

}
