package de.kytodragon.live_edit.editing;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class MyLootTable implements IJsonProvider {

    public ResourceLocation id;
    public List<ResourceLocation> requiredParams;
    public List<ResourceLocation> optionalParams;

    public List<MyLootPool> pools;
    public List<MyLootFunction> functions;

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
