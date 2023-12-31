package de.kytodragon.live_edit.editing.gui.recipes;

import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;

public interface IRecipeInput {

    void setRecipe(MyRecipe recipe);
    MyRecipe getRecipe();
    default MyGuiComponent getGUIComponent() {
        return (MyGuiComponent) this;
    }
}
