package de.kytodragon.live_edit.editing;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class MyLootTable implements IRecipe {

    public ResourceLocation id;
    public List<ResourceLocation> requiredParams; // TODO are these neccesary?
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
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id.toString());
        //JsonHelper.addResourceLocationsToJson(json, "requiredParams", requiredParams);
        //JsonHelper.addResourceLocationsToJson(json, "optionalParams", optionalParams);
        JsonHelper.addArrayToJson(json, "pools", pools);
        JsonHelper.addArrayToJson(json, "functions", functions);

        return json;
    }

    @Override
    public boolean containsItem(Item item) {
        if (pools != null) {
            ResourceLocation item_id = ForgeRegistries.ITEMS.getKey(item);
            for (MyLootPool pool : pools) {
                for (MyLootEntry entry : pool.entries) {
                    if (entry.containsItem(item_id))
                        return true;
                }
            }
        }
        return false;
    }

}
