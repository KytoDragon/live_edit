package de.kytodragon.live_edit.editing.gui.modules;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;

public interface IIngredientInput {

    void setIngredient(MyIngredient ingredient);
    MyIngredient getIngredient();
    MyGuiComponent getGUIComponent();
}
