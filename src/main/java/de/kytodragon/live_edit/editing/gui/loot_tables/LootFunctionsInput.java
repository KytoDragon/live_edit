package de.kytodragon.live_edit.editing.gui.loot_tables;

import de.kytodragon.live_edit.editing.MyLootFunction;
import de.kytodragon.live_edit.editing.gui.components.*;

import java.util.ArrayList;
import java.util.List;

public class LootFunctionsInput extends VerticalList {

    private final List<LootFunctionInput> functions = new ArrayList<>();

    public LootFunctionsInput(int x, int y) {
        super(x, y);

        TextComponent label = new TextComponent(0, 0, "Functions:");
        Button button = new Button(50, 46, 60, 12, "Add Function", this::addNewFunction);

        children.add(new ComponentGroup(0, 0, label, button));
    }

    private void addNewFunction() {
        LootFunctionInput function = new LootFunctionInput(10, 0);
        functions.add(function);
        children.add(function);
        propagate_size_change = true;
    }

    public void setLootFunctions(List<MyLootFunction> functions) {
        this.functions.clear();
        for (MyLootFunction function : functions) {
            LootFunctionInput input = new LootFunctionInput(10, 0);
            input.setLootFunction(function);
            this.functions.add(input);
            this.children.add(input);
        }
    }

    public List<MyLootFunction> getLootFunctions() {
        List<MyLootFunction> result = new ArrayList<>(functions.size());
        for (LootFunctionInput function : functions) {
            result.add(function.getLootFunction());
        }
        return result;
    }
}
