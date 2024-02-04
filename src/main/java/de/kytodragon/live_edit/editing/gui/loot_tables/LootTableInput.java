package de.kytodragon.live_edit.editing.gui.loot_tables;

import de.kytodragon.live_edit.editing.MyLootTable;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;
import de.kytodragon.live_edit.editing.gui.components.ScrolledListPanel;

public class LootTableInput extends MyGuiComponent {

    private final LootPoolsInput pools;
    private final LootFunctionsInput functions;
    private final ScrolledListPanel viewport;

    public LootTableInput(int x, int y, int width, int height) {
        super(x, y, width, height);

        viewport = new ScrolledListPanel(0, 0, width, height);
        functions = new LootFunctionsInput(2, 0);
        pools = new LootPoolsInput(2, 0);

        addChild(viewport);
        viewport.addChild(pools);
        //viewport.addChild(functions);
    }

    public void setLootTable(MyLootTable loot_table) {
        pools.setLootPools(loot_table.pools);
        functions.setLootFunctions(loot_table.functions);
    }

    public MyLootTable getLootTable() {
        MyLootTable loot_table = new MyLootTable();
        loot_table.pools = pools.getLootPools();
        loot_table.functions = functions.getLootFunctions();
        return loot_table;
    }
}
