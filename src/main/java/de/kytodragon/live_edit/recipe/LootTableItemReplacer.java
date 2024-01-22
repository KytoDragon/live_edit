package de.kytodragon.live_edit.recipe;

import de.kytodragon.live_edit.mixins.loot_tables.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class LootTableItemReplacer {

    public static boolean isToReplace(LootTable loot_table, GeneralManipulationData data) {
        for (LootPool pool : ((LootTableMixin)loot_table).live_edit_mixin_getPools()) {
            for (LootPoolEntryContainer entry : ((LootPoolMixin)pool).live_edit_mixin_getEntries()) {
                if (isToReplace(entry, data))
                    return true;
            }
        }
        return false;
    }

    private static boolean isToReplace(LootPoolEntryContainer entry, GeneralManipulationData data) {
        if (entry instanceof LootItem item) {
            return data.itemsToReplace.containsKey(((LootItemMixin) item).live_edit_mixin_getItem());
        } else if (entry instanceof CompositeEntryBase composite) {
            for (LootPoolEntryContainer child : ((CompositeEntryBaseMixin)composite).live_edit_mixin_getChildren()) {
                if (isToReplace(child, data))
                    return true;
            }
        }
        return false;
    }

    public static LootTable replace(LootTable loot_table, GeneralManipulationData data) {
        List<LootPool> old_pools = ((LootTableMixin)loot_table).live_edit_mixin_getPools();
        LootPool[] new_pools = new LootPool[old_pools.size()];
        int i = 0;
        for (LootPool pool : old_pools) {
            new_pools[i] = replace(pool, data);
            i++;
        }
        LootItemFunction[] functions = ((LootTableMixin)loot_table).live_edit_mixin_getFunctions();
        LootTable result = LootTableMixin.create(loot_table.getParamSet(), new_pools, functions);
        //noinspection DataFlowIssue
        result.setLootTableId(loot_table.getLootTableId());
        return result;
    }

    private static LootPool replace(LootPool pool, GeneralManipulationData data) {
        LootPoolEntryContainer[] old_entries = ((LootPoolMixin)pool).live_edit_mixin_getEntries();
        LootPoolEntryContainer[] new_entries = new LootPoolEntryContainer[old_entries.length];
        int i = 0;
        for (LootPoolEntryContainer entry : old_entries) {
            new_entries[i] = replace(entry, data);
            i++;
        }

        LootItemCondition[] conditions = ((LootPoolMixin)pool).live_edit_mixin_getConditions();
        LootItemFunction[] functions = ((LootPoolMixin)pool).live_edit_mixin_getFunctions();
        return LootPoolMixin.create(new_entries, conditions, functions, pool.getRolls(), pool.getBonusRolls(), pool.getName());
    }

    private static LootPoolEntryContainer replace(LootPoolEntryContainer entry, GeneralManipulationData data) {
        if (!isToReplace(entry, data))
            return entry;

        LootItemCondition[] conditions = ((LootPoolEntryContainerMixin)entry).live_edit_mixin_getConditions();

        if (entry instanceof LootPoolSingletonContainer single) {
            int weight = ((LootPoolSingletonContainerMixin)single).live_edit_mixin_getWeight();
            int quality = ((LootPoolSingletonContainerMixin)single).live_edit_mixin_getQuality();
            LootItemFunction[] functions = ((LootPoolSingletonContainerMixin)single).live_edit_mixin_getFunctions();
            if (entry instanceof LootItem item) {
                Item new_item = data.itemsToReplace.get(((LootItemMixin)item).live_edit_mixin_getItem());
                return LootItemMixin.create(new_item, weight, quality, conditions, functions);
            }

        } else if (entry instanceof CompositeEntryBase composite) {
            LootPoolEntryContainer[] old_children = ((CompositeEntryBaseMixin)composite).live_edit_mixin_getChildren();
            LootPoolEntryContainer[] new_children = new LootPoolEntryContainer[old_children.length];
            int i = 0;
            for (LootPoolEntryContainer child : old_children) {
                new_children[i] = replace(child, data);
                i++;
            }

            if (composite instanceof SequentialEntry) {
                return SequentialEntryMixin.create(new_children, conditions);
            } else if (composite instanceof AlternativesEntry) {
                return AlternativesEntryMixin.create(new_children, conditions);
            } else if (composite instanceof EntryGroup) {
                return EntryGroupMixin.create(new_children, conditions);
            }
        }
        return entry;
    }
}
