package de.kytodragon.live_edit.editing.gui.loot_tables;

import de.kytodragon.live_edit.editing.MyLootPool;
import de.kytodragon.live_edit.editing.gui.components.*;

public class LootPoolInput extends VerticalList {

    private final IntegerInput rolls_min;
    private final IntegerInput rolls_max;

    private final IntegerInput bonus_rolls_min;
    private final IntegerInput bonus_rolls_max;

    private final LootEntriesInput entries;
    private final LootConditionsInput conditions;
    private final LootFunctionsInput functions;

    public LootPoolInput(int x, int y) {
        super(x, y);

        TextComponent rolls_label = new TextComponent(0, 0, "Rolls:");

        rolls_min = new IntegerInput(60, 0, 30, 12);
        rolls_min.allow_negative = true;

        rolls_max = new IntegerInput(90, 0, 30, 12);
        rolls_max.allow_negative = true;

        addChild(new ComponentGroup(0, 0, rolls_label, rolls_min, rolls_max));

        TextComponent bonus_label = new TextComponent(0, 0, "Bonus rolls:");

        bonus_rolls_min = new IntegerInput(60, 0, 30, 12);
        bonus_rolls_min.allow_negative = true;

        bonus_rolls_max = new IntegerInput(90, 0, 30, 12);
        bonus_rolls_max.allow_negative = true;

        addChild(new ComponentGroup(0, 0, bonus_label, bonus_rolls_min, bonus_rolls_max));

        entries = new LootEntriesInput(0, 0);
        addChild(entries);

        conditions = new LootConditionsInput(0, 0);
        addChild(conditions);

        functions = new LootFunctionsInput(0, 0);
        addChild(functions);
    }

    public void setLootPool(MyLootPool pool) {
        rolls_min.setValue(pool.rolls_min);
        rolls_max.setValue(pool.rolls_max);
        bonus_rolls_min.setValue(pool.bonus_rolls_min);
        bonus_rolls_max.setValue(pool.bonus_rolls_max);
        entries.setLootEntries(pool.entries);
        conditions.setLootConditions(pool.conditions);
        functions.setLootFunctions(pool.functions);
    }

    public MyLootPool getLootPool() {
        MyLootPool pool = new MyLootPool();
        pool.rolls_min = rolls_min.getValue();
        pool.rolls_max = rolls_max.getValue();
        pool.bonus_rolls_min = bonus_rolls_min.getValue();
        pool.bonus_rolls_max = bonus_rolls_max.getValue();
        pool.entries = entries.getLootEntries();
        pool.conditions = conditions.getLootConditions();
        pool.functions = functions.getLootFunctions();
        return pool;
    }
}
