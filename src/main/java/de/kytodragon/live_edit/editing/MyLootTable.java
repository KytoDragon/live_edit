package de.kytodragon.live_edit.editing;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.List;

public class MyLootTable implements IJsonProvider {

    public ResourceLocation id;
    public List<ResourceLocation> requiredParams;
    public List<ResourceLocation> optionalParams;

    public List<MyLootPool> pools;
    public List<MyLootFunction> functions;

    public static MyLootTable fromJsonString(String json) {
        return fromJson(JsonParser.parseString(json).getAsJsonObject());
    }

    public static MyLootTable fromJson(JsonObject json) {
        MyLootTable loot_table = new MyLootTable();

        String id = GsonHelper.getAsString(json, "id");
        loot_table.id = ResourceLocation.of(id, ':');

        loot_table.pools = JsonHelper.parseListFromJson(json, "pools", MyLootPool::fromJson);
        loot_table.functions = JsonHelper.parseListFromJson(json, "functions", MyLootFunction::fromJson);

        return loot_table;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id.toString());
        JsonHelper.addResourceLocationsToJson(json, "requiredParams", requiredParams);
        JsonHelper.addResourceLocationsToJson(json, "optionalParams", optionalParams);
        JsonHelper.addArrayToJson(json, "pools", pools);
        JsonHelper.addArrayToJson(json, "functions", functions);

        return json;
    }

    public String toJsonString() {
        return new Gson().toJson(toJson());
    }
}
