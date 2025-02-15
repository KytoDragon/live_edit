package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.editing.MyLootTable;
import de.kytodragon.live_edit.recipe.IRecipeManipulator;
import de.kytodragon.live_edit.recipe.LootTableConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.*;

public class LootTableManipulator extends IRecipeManipulator<LootTable, MyLootTable, VanillaIntegration> {

    protected LootTableManipulator() {
        super(MyLootTable::fromJson);
    }

    @Override
    public ResourceLocation getKey(LootTable table) {
        return table.getLootTableId();
    }

    @Override
    public Collection<LootTable> getCurrentRecipes() {
        Map<ResourceLocation, LootTable> tables = integration.getCurrentLootTables();
        List<LootTable> result = new ArrayList<>(tables.size());
        tables.forEach((key,value) -> {
            result.add(value);
        });
        return result;
    }

    @Override
    public Optional<LootTable> getRecipe(ResourceLocation key) {
        LootTable table = integration.getCurrentLootTables().get(key);
        return Optional.ofNullable(table);
    }

    @Override
    public MyLootTable encodeRecipe(LootTable loot_table) {
        return LootTableConverter.convertLootTable(loot_table);
    }

    @Override
    protected void exportDeleted(StringBuilder sb, ResourceLocation id) {
        // TODO
    }

    @Override
    protected void exportAdded(StringBuilder sb, MyLootTable recipe) {
        // TODO
    }
}
