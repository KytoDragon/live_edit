package de.kytodragon.live_edit.editing.gui.components;

public class ComponentGroup extends MyGuiComponent {

    public ComponentGroup(int x, int y, MyGuiComponent... components) {
        super(x, y);

        for (MyGuiComponent component : components) {
            addChild(component);
        }
    }
}
