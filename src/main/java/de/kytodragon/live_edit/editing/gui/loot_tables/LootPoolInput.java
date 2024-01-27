package de.kytodragon.live_edit.editing.gui.loot_tables;

import de.kytodragon.live_edit.editing.MyLootPool;
import de.kytodragon.live_edit.editing.gui.components.*;

public class LootPoolInput extends VerticalList {

    private final IntegerInput rollsMin;
    private final IntegerInput rollsMax;

    private final IntegerInput bonusRollsMin;
    private final IntegerInput bonusRollsMax;

    private final LootEntriesInput entries;
    private final LootConditionsInput conditions;

    public LootPoolInput(int x, int y) {
        super(x, y);

        TextComponent rolls_label = new TextComponent(0, 0, "Rolls:");

        rollsMin = new IntegerInput(60, 0, 30, 10, 0);
        rollsMin.allowNegative = true;

        rollsMax = new IntegerInput(90, 0, 30, 10, 0);
        rollsMax.allowNegative = true;

        addChild(new ComponentGroup(0, 0, rolls_label, rollsMin, rollsMax));

        TextComponent bonus_label = new TextComponent(0, 0, "Bonus rolls:");

        bonusRollsMin = new IntegerInput(60, 0, 30, 10, 0);
        bonusRollsMin.allowNegative = true;

        bonusRollsMax = new IntegerInput(90, 0, 30, 10, 0);
        bonusRollsMax.allowNegative = true;

        addChild(new ComponentGroup(0, 0, bonus_label, bonusRollsMin, bonusRollsMax));

        entries = new LootEntriesInput(0, 0);
        addChild(entries);

        conditions = new LootConditionsInput(0, 0);
        addChild(conditions);
    }

    public void setLootPool(MyLootPool pool) {
        rollsMin.setValue(pool.rollsMin);
        rollsMax.setValue(pool.rollsMax);
        bonusRollsMin.setValue(pool.bonusRollsMin);
        bonusRollsMax.setValue(pool.bonusRollsMax);
        entries.setLootEntries(pool.entries);
        conditions.setLootConditions(pool.conditions);
    }

    public MyLootPool getLootPool() {
        MyLootPool pool = new MyLootPool();
        pool.rollsMin = rollsMin.value;
        pool.rollsMax = rollsMax.value;
        pool.bonusRollsMin = bonusRollsMin.value;
        pool.bonusRollsMax = bonusRollsMax.value;
        pool.entries = entries.getLootEntries();
        pool.conditions = conditions.getLootConditions();
        return pool;
    }
}
