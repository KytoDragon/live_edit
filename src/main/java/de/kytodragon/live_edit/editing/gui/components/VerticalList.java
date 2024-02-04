package de.kytodragon.live_edit.editing.gui.components;

public class VerticalList extends MyGuiComponent {

    public VerticalList(int x, int y) {
        super(x, y, -1, -1);
    }

    @Override
    public void calculateBounds() {
        if (propagate_size_change) {
            width = -1;
            height = -1;
        }

        super.calculateBounds();

        // arrange each child below the previous
        int current_y = 0;
        for (MyGuiComponent child : children) {
            if (child.is_visible) {
                child.y = current_y;
                current_y += child.height;
            } else {
                child.y = 0;
            }
        }
        this.height = current_y;
    }
}
