package de.kytodragon.live_edit.editing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;

import java.util.List;

public class MyLootEntry implements IJsonProvider {

    public LootPoolEntryType type;
    public int weight;
    public int quality; // for fishing loot tables
    public ResourceLocation id;
    public List<MyLootCondition> conditions;
    public List<MyLootEntry> children;
    public List<MyLootFunction> functions;
    public boolean drop_all_items_from_tag;

    public static MyLootEntry fromJson(JsonObject json) {
        MyLootEntry entry = new MyLootEntry();

        ResourceLocation type = JsonHelper.getResourceLocation(json, "type");
        entry.type = BuiltInRegistries.LOOT_POOL_ENTRY_TYPE.get(type);

        entry.weight = GsonHelper.getAsInt(json, "weight", 1);
        entry.quality = GsonHelper.getAsInt(json, "quality", 0);

        entry.id = JsonHelper.getResourceLocationOrNull(json, "id");

        entry.conditions = JsonHelper.parseListFromJson(json, "conditions", MyLootCondition::fromJson);
        entry.children = JsonHelper.parseListFromJson(json, "children", MyLootEntry::fromJson);
        entry.functions = JsonHelper.parseListFromJson(json, "functions", MyLootFunction::fromJson);

        entry.drop_all_items_from_tag = GsonHelper.getAsBoolean(json, "drop_all_items_from_tag", false);

        return entry;
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        //noinspection DataFlowIssue
        json.addProperty("type", BuiltInRegistries.LOOT_POOL_ENTRY_TYPE.getKey(type).toString());
        if (weight != 1)
            json.addProperty("weight", weight);
        if (quality != 0)
            json.addProperty("quality", quality);
        if (id != null)
            json.addProperty("id", id.toString());
        if (drop_all_items_from_tag)
            json.addProperty("drop_all_items_from_tag", drop_all_items_from_tag);

        JsonHelper.addArrayToJson(json, "conditions", conditions);
        JsonHelper.addArrayToJson(json, "children", children);
        JsonHelper.addArrayToJson(json, "functions", functions);
        return json;
    }

    public boolean containsItem(ResourceLocation item_id) {
        if (type == LootPoolEntries.ITEM && id.equals(item_id)) {
            return true;
        }
        for (MyLootEntry entry : children) {
            if (entry.containsItem(item_id))
                return true;
        }
        return false;
    }
}
