package de.kytodragon.live_edit.editing.gui.components;

public class VerticalList extends MyGuiComponent {

    public VerticalList(int x, int y) {
        super(x, y, -1, -1);
    }

    @Override
    public void calculateBounds() {
        super.calculateBounds();

        // arrange each child below the previous
        int current_y = 0;
        for (MyGuiComponent child : children) {
            child.y = current_y;
            current_y += child.height;
        }
        this.height = current_y;
    }
}
