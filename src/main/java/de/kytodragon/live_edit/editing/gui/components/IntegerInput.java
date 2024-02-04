package de.kytodragon.live_edit.editing.gui.components;

public class IntegerInput extends EditBoxWrapper {

    private int value;
    public boolean allow_negative = false;

    public IntegerInput(int x, int y, int width, int height) {
        super(x, y, width, height, "0");

        edit_box.setFilter(text -> {
            if (allow_negative)
                return text.matches("-?[0-9]{0,5}");
            else
                return text.matches("[0-9]{0,5}");
        });
    }

    public void setValue(int value) {
        this.value = value;
        edit_box.setValue(Integer.toString(value));
    }

    public int getValue() {
        return value;
    }

    @Override
    protected void setText(String text) {
        if (text == null || text.isEmpty() || text.equals("-"))
            this.value = 0;
        else
            this.value = Integer.parseInt(text);
    }
}
