package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.editing.MyLootTable;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.recipe.LootTableItemReplacer;
import de.kytodragon.live_edit.recipe.GeneralManipulationData;
import de.kytodragon.live_edit.recipe.IRecipeManipulator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.*;

public class LootTableManipulator extends IRecipeManipulator<ResourceLocation, LootTable, VanillaIntegration> {
    @Override
    public ResourceLocation getKey(LootTable table) {
        return table.getLootTableId();
    }

    @Override
    public LootTable manipulate(LootTable table, GeneralManipulationData data) {
        if (LootTableItemReplacer.isToReplace(table, data)) {
            table = LootTableItemReplacer.replace(table, data);
        }
        return table;
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

    public void markLootTableForReplacement(ResourceLocation lootTableKey, MyLootTable loot_table) {
        recipes_to_replace.put(lootTableKey, null); // TODO
    }

    @Override
    public void prepareReload(Collection<LootTable> tables) {
        integration.addLootTables(tables);
    }

    @Override
    public MyRecipe encodeRecipe(LootTable recipe) {
        return null;
    }

    @Override
    public LootTable decodeRecipe(MyRecipe recipe) {
        return null;
    }
}
