package de.kytodragon.live_edit.editing.gui.loot_tables;

import de.kytodragon.live_edit.editing.MyLootCondition;
import de.kytodragon.live_edit.editing.gui.components.Button;
import de.kytodragon.live_edit.editing.gui.components.ComponentGroup;
import de.kytodragon.live_edit.editing.gui.components.TextComponent;
import de.kytodragon.live_edit.editing.gui.components.VerticalList;

import java.util.ArrayList;
import java.util.List;

public class LootConditionsInput extends VerticalList {

    private final List<LootConditionInput> conditions = new ArrayList<>();

    public LootConditionsInput(int x, int y) {
        super(x, y);

        TextComponent label = new TextComponent(0, 0, "Conditions:");
        Button button = new Button(60, 0, 80, 12, "Add Condition", this::addNewCondition);

        addChild(new ComponentGroup(0, 0, label, button));
    }

    private void addNewCondition() {
        LootConditionInput condition = new LootConditionInput(10, 0);
        conditions.add(condition);
        addChild(condition);
        width = -1;
        height = -1;
        propagate_size_change = true;
    }

    public void setLootConditions(List<MyLootCondition> conditions) {
        this.conditions.clear();
        if (conditions != null) {
            for (MyLootCondition condition : conditions) {
                LootConditionInput input = new LootConditionInput(10, 0);
                input.setLootCondition(condition);
                this.conditions.add(input);
                addChild(input);
            }
        }
    }

    public List<MyLootCondition> getLootConditions() {
        List<MyLootCondition> result = new ArrayList<>(conditions.size());
        for (LootConditionInput condition : conditions) {
            result.add(condition.getLootCondition());
        }
        return result;
    }
}
