package de.kytodragon.live_edit.editing.gui.loot_tables;

import de.kytodragon.live_edit.editing.MyLootPool;
import de.kytodragon.live_edit.editing.gui.components.Button;
import de.kytodragon.live_edit.editing.gui.components.ComponentGroup;
import de.kytodragon.live_edit.editing.gui.components.TextComponent;
import de.kytodragon.live_edit.editing.gui.components.VerticalList;

import java.util.ArrayList;
import java.util.List;

public class LootPoolsInput extends VerticalList {

    private final List<LootPoolInput> pools = new ArrayList<>();

    public LootPoolsInput(int x, int y) {
        super(x, y);

        TextComponent label = new TextComponent(0, 0, "Pools:");
        Button button = new Button(50, 0, 60, 12, "Add Pool", this::addNewPool);

        addChild(new ComponentGroup(0, 0, label, button));
    }

    private void addNewPool() {
        LootPoolInput entry = new LootPoolInput(10, 0);
        pools.add(entry);
        addChild(entry);
        width = -1;
        height = -1;
        propagate_size_change = true;
    }

    public void setLootPools(List<MyLootPool> pools) {
        this.pools.clear();
        if (pools != null) {
            for (MyLootPool pool : pools) {
                LootPoolInput input = new LootPoolInput(10, 0);
                input.setLootPool(pool);
                this.pools.add(input);
                addChild(input);
            }
        }
    }

    public List<MyLootPool> getLootPools() {
        List<MyLootPool> result = new ArrayList<>(pools.size());
        for (LootPoolInput pool : pools) {
            result.add(pool.getLootPool());
        }
        return result;
    }
}
