package de.kytodragon.live_edit.editing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;

import java.util.List;

public class MyLootFunction implements IJsonProvider {

    public enum Function {
        SET_COUNT, // min + max count
        LOOTING, // min + max additional count per level of looting, limit
        POTION_EFFECT, // potion id
        FURNACE_SMELT,
        DAMAGE, // damage min + max
        ENCHANT_RANDOMLY,
        ENCHANT_RANDOMLY_WITH_LIST, // list of possible enchantments
        INSTRUMENT, // tag id
        STEW_EFFECT, // list of effects and duration min+max
        ENCHANT_WITH_LEVELS, // level min + max,
        FORTUNE_ORES,
        FORTUNE_UNIFORM, // additional count per level of fortune
        FORTUNE_BINOMIAL, // number of tries and chance per try
        EXPLOSION_DECAY,
    }

    public Function type;
    public float min_count;
    public float max_count;
    public int limit;
    public boolean add_count;
    public ResourceLocation id;
    public List<ResourceLocation> ids;
    public List<Float> stew_duration_min;
    public List<Float> stew_duration_max;
    public boolean treasure_enchant;
    public List<MyLootCondition> conditions;

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", type.name());
        if (min_count != 0)
            json.addProperty("min_count", min_count);
        if (max_count != 0)
            json.addProperty("max_count", max_count);
        if (limit != 0)
            json.addProperty("limit", limit);
        if (type == Function.SET_COUNT)
            json.addProperty("add_count", add_count);
        if (id != null)
            json.addProperty("id", id.toString());
        if (type == Function.ENCHANT_WITH_LEVELS)
            json.addProperty("treasure_enchant", treasure_enchant);

        JsonHelper.addResourceLocationsToJson(json, "ids", ids);
        JsonHelper.addFloatArrayToJson(json, "stew_duration_min", stew_duration_min);
        JsonHelper.addFloatArrayToJson(json, "stew_duration_max", stew_duration_max);
        JsonHelper.addArrayToJson(json, "conditions", conditions);

        return json;
    }
}
