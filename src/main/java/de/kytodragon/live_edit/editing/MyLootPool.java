package de.kytodragon.live_edit.editing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.List;

public class MyLootPool implements IJsonProvider {

    public int rollsMin;
    public int rollsMax;

    public int bonusRollsMin;
    public int bonusRollsMax;

    public List<MyLootCondition> conditions;
    public List<MyLootEntry> entries;

    public static MyLootPool fromJson(JsonObject json) {
        MyLootPool pool = new MyLootPool();

        pool.rollsMin = GsonHelper.getAsInt(json, "rollsMin", 1);
        pool.rollsMax = GsonHelper.getAsInt(json, "rollsMax", 1);

        pool.bonusRollsMin = GsonHelper.getAsInt(json, "bonusRollsMin", 0);
        pool.bonusRollsMax = GsonHelper.getAsInt(json, "bonusRollsMax", 0);

        pool.conditions = JsonHelper.parseListFromJson(json, "conditions", MyLootCondition::fromJson);
        pool.entries = JsonHelper.parseListFromJson(json, "entries", MyLootEntry::fromJson);

        return pool;
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        if (rollsMin != 1)
            json.addProperty("rollsMin", rollsMin);
        if (rollsMax != 1)
            json.addProperty("rollsMax", rollsMax);
        if (bonusRollsMin != 0)
            json.addProperty("bonusRollsMin", bonusRollsMin);
        if (bonusRollsMax != 0)
            json.addProperty("bonusRollsMax", bonusRollsMax);

        JsonHelper.addArrayToJson(json, "conditions", conditions);
        JsonHelper.addArrayToJson(json, "entries", entries);
        return json;
    }
}
