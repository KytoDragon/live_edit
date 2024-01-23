package de.kytodragon.live_edit.editing.gui.loot_tables;

import de.kytodragon.live_edit.editing.MyLootPool;
import de.kytodragon.live_edit.editing.MyLootTable;
import de.kytodragon.live_edit.editing.gui.components.Button;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;
import de.kytodragon.live_edit.editing.gui.components.ScrolledListPanel;

import java.util.ArrayList;
import java.util.List;

public class LootTableInput extends MyGuiComponent {

    private final List<LootPoolInput> pools = new ArrayList<>();
    private final LootFunctionsInput functions;
    private final ScrolledListPanel viewport;

    public LootTableInput(int x, int y) {
        super(x, y);

        viewport = new ScrolledListPanel(0, 0, 160, 45);
        functions = new LootFunctionsInput(0, 0);

        children.add(viewport);
        viewport.children.add(new Button(50, 46, 60, 12, "Add Pool", this::addNewPool));
        viewport.children.add(functions);
    }

    private void addNewPool() {
        LootPoolInput pool = new LootPoolInput(0, 0);
        pools.add(pool);
        viewport.children.add(pool);
        this.calculateBounds();
    }

    public void setLootTable(MyLootTable loot_table) {
        for (MyLootPool pool : loot_table.pools) {
            LootPoolInput input = new LootPoolInput(0, 0);
            input.setLootPool(pool);
            pools.add(input);
            viewport.children.add(input);
        }
        functions.setLootFunctions(loot_table.functions);
    }

    public MyLootTable getLootTable() {
        MyLootTable loot_table = new MyLootTable();
        loot_table.pools = new ArrayList<>(pools.size());
        for (LootPoolInput pool : pools) {
            loot_table.pools.add(pool.getLootPool());
        }
        loot_table.functions = functions.getLootFunctions();
        return loot_table;
    }
}
