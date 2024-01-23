package de.kytodragon.live_edit.editing.gui.components;

import java.util.Arrays;

public class ComponentGroup extends MyGuiComponent {

    public ComponentGroup(int x, int y, MyGuiComponent... components) {
        super(x, y, 0, 0);

        children.addAll(Arrays.asList(components));
    }
}
