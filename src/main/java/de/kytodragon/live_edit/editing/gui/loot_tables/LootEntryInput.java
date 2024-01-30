package de.kytodragon.live_edit.editing.gui.loot_tables;

import de.kytodragon.live_edit.editing.MyLootEntry;
import de.kytodragon.live_edit.editing.gui.components.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class LootEntryInput extends VerticalList {

    private final ListSelectBox type;
    private final IntegerInput weight;
    private final IntegerInput quality;
    private final ItemComponent item;
    private final TagComponent tag;
    private final TextInput loot_table_reference;
    private final CheckBox dropAllItemsFromTag;
    private final LootFunctionsInput functions;
    private final LootConditionsInput conditions;

    public LootEntryInput(int x, int y) {
        super(x, y);

        List<String> types = Registry.LOOT_POOL_ENTRY_TYPE.keySet().stream().map(ResourceLocation::toString).toList();
        type = new ListSelectBox(0, 0, 160, types, this::setType);
        addChild(type);

        weight = new IntegerInput(0, 0, 20, 16, 0);
        addChild(weight);
        quality = new IntegerInput(0, 0, 20, 16, 0);
        addChild(quality);

        item = new ItemComponent(0, 0, ItemStack.EMPTY);
        addChild(item);
        tag = new TagComponent(0, 0, (TagKey<Item>)null);
        addChild(tag);
        loot_table_reference = new TextInput(0, 0, 60, 16);
        addChild(loot_table_reference);

        dropAllItemsFromTag = new CheckBox(0, 0);
        addChild(dropAllItemsFromTag);

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
        type.setValue(Registry.LOOT_POOL_ENTRY_TYPE.getKey(entry.type).toString());
        weight.setValue(entry.weight);
        quality.setValue(entry.quality);

        if (entry.id != null) {
            if (entry.type == LootPoolEntries.ITEM) {
                item.itemStack = ForgeRegistries.ITEMS.getValue(entry.id).getDefaultInstance();
            } else if (entry.type == LootPoolEntries.TAG) {
                tag.setTag(TagKey.create(Registry.ITEM_REGISTRY, entry.id));
            } else if (entry.type == LootPoolEntries.REFERENCE) {
                loot_table_reference.setValue(entry.id.toString());
            }
        }

        dropAllItemsFromTag.value = entry.dropAllItemsFromTag;
        conditions.setLootConditions(entry.conditions);
        functions.setLootFunctions(entry.functions);
    }

    public MyLootEntry getLootEntry() {
        MyLootEntry entry = new MyLootEntry();
        entry.type = Registry.LOOT_POOL_ENTRY_TYPE.get(ResourceLocation.of(type.getValue(), ':'));
        entry.weight = weight.value;
        entry.quality = quality.value;

        if (entry.type == LootPoolEntries.ITEM) {
            entry.id = ForgeRegistries.ITEMS.getKey(item.itemStack.getItem());
        } else if (entry.type == LootPoolEntries.TAG) {
            if (tag.tag != null) {
                entry.id = tag.tag.location();
            } else {
                entry.id = null;
            }
        } else if (entry.type == LootPoolEntries.REFERENCE) {
            entry.id = ResourceLocation.of(loot_table_reference.getValue(), ':');
        }

        entry.dropAllItemsFromTag = dropAllItemsFromTag.value;
        entry.conditions = conditions.getLootConditions();
        entry.functions = functions.getLootFunctions();
        return entry;
    }
}
