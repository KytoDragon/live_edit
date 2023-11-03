package de.kytodragon.live_edit.editing.gui.modules;

import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;

public interface IResultInput {

    void setResult(MyResult result);
    MyResult getResult();
    default MyGuiComponent getGUIComponent() {
        return (MyGuiComponent) this;
    }
}
