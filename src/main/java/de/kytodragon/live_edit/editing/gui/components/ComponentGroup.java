package de.kytodragon.live_edit.editing.gui.components;

public class ComponentGroup extends MyGuiComponent {

    public ComponentGroup(int x, int y, MyGuiComponent... components) {
        super(x, y);

        for (MyGuiComponent component : components) {
            addChild(component);
        }
    }

    @Override
    public void calculateBounds() {
        if (propagate_size_change) {
            width = -1;
            height = -1;
        }

        super.calculateBounds();
    }
}
