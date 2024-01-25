package de.kytodragon.live_edit.editing.gui.loot_tables;

import de.kytodragon.live_edit.editing.MyLootEntry;
import de.kytodragon.live_edit.editing.gui.components.Button;
import de.kytodragon.live_edit.editing.gui.components.ComponentGroup;
import de.kytodragon.live_edit.editing.gui.components.TextComponent;
import de.kytodragon.live_edit.editing.gui.components.VerticalList;

import java.util.ArrayList;
import java.util.List;

public class LootEntriesInput extends VerticalList {

    private final List<LootEntryInput> entries = new ArrayList<>();

    public LootEntriesInput(int x, int y) {
        super(x, y);

        TextComponent label = new TextComponent(0, 0, "Entries:");
        Button button = new Button(50, 46, 60, 12, "Add Entry", this::addNewEntry);

        children.add(new ComponentGroup(0, 0, label, button));
    }

    private void addNewEntry() {
        LootEntryInput entry = new LootEntryInput(10, 0);
        entries.add(entry);
        children.add(entry);
        propagate_size_change = true;
    }

    public void setLootEntries(List<MyLootEntry> entries) {
        this.entries.clear();
        for (MyLootEntry entry : entries) {
            LootEntryInput input = new LootEntryInput(10, 0);
            input.setLootEntry(entry);
            this.entries.add(input);
            this.children.add(input);
        }
    }

    public List<MyLootEntry> getLootEntries() {
        List<MyLootEntry> result = new ArrayList<>(entries.size());
        for (LootEntryInput entry : entries) {
            result.add(entry.getLootEntry());
        }
        return result;
    }
}
