package de.kytodragon.live_edit.editing.gui.loot_tables;

import de.kytodragon.live_edit.editing.MyLootEntry;
import de.kytodragon.live_edit.editing.gui.components.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;

import java.util.List;
import java.util.Locale;

public class LootEntryInput extends VerticalList {

    private final ListSelectBox type;
    private final IntegerInput weight;
    private final IntegerInput quality;
    private final ItemComponent item;
    private final TagComponent tag;
    private final TextInput loot_table_reference_text;
    private final ListSelectBox loot_table_reference_list;
    private final CheckBox drop_all_items_from_tag;
    private final LootFunctionsInput functions;
    private final LootConditionsInput conditions;
    private final LootEntriesInput components;

    public LootEntryInput(int x, int y) {
        super(x, y);

        List<String> types = BuiltInRegistries.LOOT_POOL_ENTRY_TYPE.keySet().stream().map(ResourceLocation::toString).toList();
        type = new ListSelectBox(0, 0, 160, types, this::setType);
        addChild(type);

        TextComponent weight_label = new TextComponent(0, 0, "Weight:");
        weight = new IntegerInput(40, 0, 20, 12);
        addChild(new ComponentGroup(0, 0, weight_label, weight));
        TextComponent quality_label = new TextComponent(0, 0, "Quality:");
        quality = new IntegerInput(40, 0, 20, 12);
        addChild(new ComponentGroup(0, 0, quality_label, quality));

        item = new ItemComponent(0, 0);
        item.only_one_item = true;
        addChild(item);
        tag = new TagComponent(0, 0);
        addChild(tag);

        MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server == null) {
            loot_table_reference_text = new TextInput(0, 0, 160, 16);
            addChild(loot_table_reference_text);
            loot_table_reference_list = null;

        } else {
            loot_table_reference_list = new ListSelectBox(0, 0, 160, server.getLootData().getKeys(LootDataType.TABLE), null);
            addChild(loot_table_reference_list);
            loot_table_reference_text = null;
        }

        TextComponent tag_all_label = new TextComponent(0, 0, "Drop all:");
        drop_all_items_from_tag = new CheckBox(40, 0);
        addChild(new ComponentGroup(0, 0, tag_all_label, drop_all_items_from_tag));

        functions = new LootFunctionsInput(0, 0);
        addChild(functions);

        conditions = new LootConditionsInput(0, 0);
        addChild(conditions);

        components = new LootEntriesInput(0, 0);
        addChild(components);

        setType(types.get(0));
    }

    private void setType(String type_name) {
        LootPoolEntryType type = BuiltInRegistries.LOOT_POOL_ENTRY_TYPE.get(ResourceLocation.of(type_name, ':'));
        setVisiblityBasedOnType(type);
    }

    private void setVisiblityBasedOnType(LootPoolEntryType type) {

        item.is_visible = false;
        tag.is_visible = false;
        if (loot_table_reference_list != null) {
            loot_table_reference_list.is_visible = false;
        } else {
            loot_table_reference_text.is_visible = false;
        }
        drop_all_items_from_tag.parent.is_visible = false;
        quality.parent.is_visible = false;
        components.is_visible = false;

        if (type == LootPoolEntries.ITEM) {
            item.is_visible = true;
        } else if (type == LootPoolEntries.TAG) {
            tag.is_visible = true;
        } else if (type == LootPoolEntries.REFERENCE) {
            if (loot_table_reference_list != null) {
                loot_table_reference_list.is_visible = true;
            } else {
                loot_table_reference_text.is_visible = true;
            }
            drop_all_items_from_tag.parent.is_visible = true;
            quality.parent.is_visible = true;
        } else if (type == LootPoolEntries.ALTERNATIVES || type == LootPoolEntries.GROUP || type == LootPoolEntries.SEQUENCE) {
            components.is_visible = true;
        }

        this.propagate_size_change = true;
    }

    public void setLootEntry(MyLootEntry entry) {
        type.setValue(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE.getKey(entry.type).toString());
        weight.setValue(entry.weight);
        quality.setValue(entry.quality);

        if (entry.id != null) {
            if (entry.type == LootPoolEntries.ITEM) {
                item.setItemId(entry.id);
            } else if (entry.type == LootPoolEntries.TAG) {
                tag.setTagId(entry.id);
            } else if (entry.type == LootPoolEntries.REFERENCE) {
                if (loot_table_reference_list != null) {
                    loot_table_reference_list.setValue(entry.id.toString());
                } else {
                    loot_table_reference_text.setValue(entry.id.toString());
                }
            }
        }

        drop_all_items_from_tag.value = entry.drop_all_items_from_tag;
        conditions.setLootConditions(entry.conditions);
        functions.setLootFunctions(entry.functions);
        components.setLootEntries(entry.children);

        setVisiblityBasedOnType(entry.type);
    }

    public MyLootEntry getLootEntry() {
        MyLootEntry entry = new MyLootEntry();
        entry.type = BuiltInRegistries.LOOT_POOL_ENTRY_TYPE.get(ResourceLocation.of(type.getValue(), ':'));
        entry.weight = weight.getValue();
        entry.quality = quality.getValue();

        if (entry.type == LootPoolEntries.ITEM) {
            entry.id = item.getItemId();
        } else if (entry.type == LootPoolEntries.TAG) {
            entry.id = tag.getTagId();
        } else if (entry.type == LootPoolEntries.REFERENCE) {
            String loot_table_id;
            if (loot_table_reference_list != null) {
                loot_table_id = loot_table_reference_list.getValue();
            } else {
                loot_table_id = loot_table_reference_text.getValue();
            }
            entry.id = ResourceLocation.of(loot_table_id, ':');
        }

        entry.drop_all_items_from_tag = drop_all_items_from_tag.value;
        entry.conditions = conditions.getLootConditions();
        entry.functions = functions.getLootFunctions();
        entry.children = components.getLootEntries();
        return entry;
    }
}
