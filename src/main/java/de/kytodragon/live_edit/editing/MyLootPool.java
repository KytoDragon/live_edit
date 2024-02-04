package de.kytodragon.live_edit.editing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.List;

public class MyLootPool implements IJsonProvider {

    public int rolls_min;
    public int rolls_max;

    public int bonus_rolls_min;
    public int bonus_rolls_max;

    public List<MyLootCondition> conditions;
    public List<MyLootEntry> entries;

    public static MyLootPool fromJson(JsonObject json) {
        MyLootPool pool = new MyLootPool();

        pool.rolls_min = GsonHelper.getAsInt(json, "rolls_min", 1);
        pool.rolls_max = GsonHelper.getAsInt(json, "rolls_max", 1);

        pool.bonus_rolls_min = GsonHelper.getAsInt(json, "bonus_rolls_min", 0);
        pool.bonus_rolls_max = GsonHelper.getAsInt(json, "bonus_rolls_max", 0);

        pool.conditions = JsonHelper.parseListFromJson(json, "conditions", MyLootCondition::fromJson);
        pool.entries = JsonHelper.parseListFromJson(json, "entries", MyLootEntry::fromJson);

        return pool;
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        if (rolls_min != 1)
            json.addProperty("rolls_min", rolls_min);
        if (rolls_max != 1)
            json.addProperty("rolls_max", rolls_max);
        if (bonus_rolls_min != 0)
            json.addProperty("bonus_rolls_min", bonus_rolls_min);
        if (bonus_rolls_max != 0)
            json.addProperty("bonus_rolls_max", bonus_rolls_max);

        JsonHelper.addArrayToJson(json, "conditions", conditions);
        JsonHelper.addArrayToJson(json, "entries", entries);
        return json;
    }
}
