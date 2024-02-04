package de.kytodragon.live_edit.editing.gui.components;

import java.text.DecimalFormat;
import java.text.ParseException;

public class FloatInput extends EditBoxWrapper {

    private float value;
    public boolean allow_negative = false;
    private final DecimalFormat positive_format;
    private final DecimalFormat negativ_format;

    public FloatInput(int x, int y, int width, int height) {
        super(x, y, width, height, "0");

        positive_format = new DecimalFormat("###0.##;###0.##");
        negativ_format = new DecimalFormat("###0.##");

        edit_box.setFilter(text -> {
            try {
                if (allow_negative) {
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

    public float getValue() {
        return this.value;
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
