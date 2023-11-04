package de.kytodragon.live_edit.editing.gui.components;

import java.text.DecimalFormat;
import java.text.ParseException;

public class FloatInput extends EditBoxWrapper {

    public float value;
    public boolean allowNegative = false;
    private final DecimalFormat positive_format;
    private final DecimalFormat negativ_format;

    public FloatInput(int x, int y, int width, int height, float value) {
        super(x, y, width, height, Float.toString(value));
        this.value = value;

        positive_format = new DecimalFormat("###0.##;###0.##");
        negativ_format = new DecimalFormat("###0.##");

        edit_box.setFilter(text -> {
            try {
                if (allowNegative) {
                    negativ_format.parse(text);
                } else {
                    positive_format.parse(text);
                }
                return true;
            } catch (ParseException e) {
                return false;
            }
        });
    }

    public void setValue(float value) {
        this.value = value;
        edit_box.setValue(negativ_format.format(value));
    }

    @Override
    protected void setText(String text) {
        try {
            this.value = negativ_format.parse(text).floatValue();
        } catch (ParseException e) {
            this.value = 0;
        }
    }
}
