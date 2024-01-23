package de.kytodragon.live_edit.editing.gui.loot_tables;

import de.kytodragon.live_edit.editing.MyLootEntry;
import de.kytodragon.live_edit.editing.gui.components.*;

public class LootEntryInput extends VerticalList {

    // TODO

    private final LootFunctionsInput functions;
    private final LootConditionsInput conditions;

    public LootEntryInput(int x, int y) {
        super(x, y);

        // TODO

        functions = new LootFunctionsInput(0, 0);
        children.add(functions);

        conditions = new LootConditionsInput(0, 0);
        children.add(conditions);
    }

    public void setLootEntry(MyLootEntry entry) {
        // TODO
        conditions.setLootConditions(entry.conditions);
        functions.setLootFunctions(entry.functions);
    }

    public MyLootEntry getLootEntry() {
        MyLootEntry entry = new MyLootEntry();
        // TODO
        entry.conditions = conditions.getLootConditions();
        entry.functions = functions.getLootFunctions();
        return entry;
    }
}
