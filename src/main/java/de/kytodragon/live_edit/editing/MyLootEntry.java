package de.kytodragon.live_edit.editing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
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
    public boolean dropAllItemsFromTag;

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        //noinspection DataFlowIssue
        json.addProperty("type", Registry.LOOT_POOL_ENTRY_TYPE.getKey(type).toString());
        if (weight != 1 && (weight != 0 || type != LootPoolEntries.ALTERNATIVES))
            json.addProperty("weight", weight);
        if (quality != 0)
            json.addProperty("quality", quality);
        if (id != null)
            json.addProperty("id", id.toString());
        if (type == LootPoolEntries.TAG)
            json.addProperty("dropAllItemsFromTag", dropAllItemsFromTag);

        JsonHelper.addArrayToJson(json, "conditions", conditions);
        JsonHelper.addArrayToJson(json, "children", children);
        JsonHelper.addArrayToJson(json, "functions", functions);
        return json;
    }
}
