package de.kytodragon.live_edit.editing.gui.loot_tables;

import de.kytodragon.live_edit.editing.MyLootEntry;
import de.kytodragon.live_edit.editing.gui.components.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;

import java.util.List;

public class LootEntryInput extends VerticalList {

    // TODO

    private final ListSelectBox type;
    private final LootFunctionsInput functions;
    private final LootConditionsInput conditions;

    public LootEntryInput(int x, int y) {
        super(x, y);

        List<String> types = Registry.LOOT_POOL_ENTRY_TYPE.keySet().stream().map(ResourceLocation::toString).toList();
        type = new ListSelectBox(0, 0, 160, types, this::setType);
        addChild(type);

        // TODO

        functions = new LootFunctionsInput(0, 0);
        addChild(functions);

        conditions = new LootConditionsInput(0, 0);
        addChild(conditions);
    }

    private void setType(String type_name) {
        LootPoolEntryType type = Registry.LOOT_POOL_ENTRY_TYPE.get(ResourceLocation.of(type_name, ':'));
        // TODO change widgets based on type
    }

    public void setLootEntry(MyLootEntry entry) {
        // TODO
        conditions.setLootConditions(entry.conditions);
        functions.setLootFunctions(entry.functions);
    }

    public MyLootEntry getLootEntry() {
        MyLootEntry entry = new MyLootEntry();
        entry.type = Registry.LOOT_POOL_ENTRY_TYPE.get(ResourceLocation.of(type.getValue(), ':'));
        // TODO
        entry.conditions = conditions.getLootConditions();
        entry.functions = functions.getLootFunctions();
        return entry;
    }
}
