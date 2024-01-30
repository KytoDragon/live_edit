package de.kytodragon.live_edit.editing.gui.components;

public class TextInput extends EditBoxWrapper {

    public TextInput(int x, int y, int width, int height) {
        super(x, y, width, height, "");
    }

    public void setValue(String value) {
        edit_box.setValue(value);
    }

    public String getValue() {
        return edit_box.getValue();
    }

    @Override
    protected void setText(String text) {
    }
}
