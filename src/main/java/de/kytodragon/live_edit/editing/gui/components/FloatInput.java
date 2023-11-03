package de.kytodragon.live_edit.editing.gui.components;

public class FloatInput extends EditBoxWrapper {

    public float value;
    public boolean allowNegative = false;
    // TODO Decimal format

    public FloatInput(int x, int y, int width, int height, float value) {
        super(x, y, width, height, Float.toString(value));
        this.value = value;

        edit_box.setFilter(text -> {
            if (allowNegative)
                return text.matches("-?[0-9]{0,4}(\\.[0-9]{0,2})?");
            else
                return text.matches("[0-9]{0,4}(\\.[0-9]{0,2})?");
        });
    }

    public void setValue(float value) {
        this.value = value;
        edit_box.setValue(Float.toString(value));
    }

    @Override
    protected void setText(String text) {
        if (text == null || text.isEmpty() || text.equals("-") || text.equals("."))
            this.value = 0;
        else {
            this.value = Float.parseFloat(text);
        }
    }
}
