package de.kytodragon.live_edit.editing.gui.components;

public class IntegerInput extends EditBoxWrapper {

    public int value;
    public boolean allowNegative = false;

    public IntegerInput(int x, int y, int width, int height, int value) {
        super(x, y, width, height, Integer.toString(value));
        this.value = value;
        edit_box.setFilter(text -> {
            if (allowNegative)
                return text.matches("-?[0-9]{0,5}");
            else
                return text.matches("[0-9]{0,5}");
        });
    }

    public void setValue(int value) {
        this.value = value;
        edit_box.setValue(Integer.toString(value));
    }

    @Override
    protected void setText(String text) {
        if (text == null || text.isEmpty() || text.equals("-"))
            this.value = 0;
        else
            this.value = Integer.parseInt(text);
    }
}
