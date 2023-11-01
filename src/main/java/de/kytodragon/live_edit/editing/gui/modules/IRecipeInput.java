package de.kytodragon.live_edit.editing.gui.modules;

import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;

public interface IRecipeInput {

    void setRecipe(MyRecipe recipe);
    MyRecipe getRecipe();
    MyGuiComponent getGUIComponent();
}
