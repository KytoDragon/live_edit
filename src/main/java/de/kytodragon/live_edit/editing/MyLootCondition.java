package de.kytodragon.live_edit.editing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class MyLootCondition implements IJsonProvider {

    public enum Condition {
        KILLED_BY_PLAYER,
        RANDOM, // base chance
        RANDOM_WITH_LOOTING, // base chance, additional chance
        DESTROYED_BY_ENTITY,
        ENTITY_IS_ON_FIRE,
        KILLED_BY_ENTITY_OF_TYPE, // entity type
        KILLED_BY_ENTITY_IN_TAG, // entity tag
        FISHING_IN_OPEN_WATER,
        SLIME_SIZE, // size
        INVERTED_CONDITION,
        KILLED_BY_LIGHTNING,
        IS_IN_BIOME, // biome id
        ALTERNATIVES, // alternatives
        SURVIVES_EXPLOSION,
        BLOCK_STATE, // block id, block state name & value
        FORTUNE, // fortune chances per level
        MATCH_TOOL_ID, // item id
        MATCH_TOOL_TAG, // tag id
        SILK_TOUCH,
        MATCH_TOOL_ACTION, // tool action id
    }

    public Condition type;
    public float base_chance;
    public float additional_chance;
    public ResourceLocation id;
    public int slime_size_min;
    public int slime_size_max;
    public MyLootCondition inverted;
    public List<MyLootCondition> alternatives;
    public String block_state_name;
    public String block_state_value;
    public List<Float> fortune_chances;

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", type.name());
        if (base_chance != 0)
            json.addProperty("base_chance", base_chance);
        if (additional_chance != 0)
            json.addProperty("additional_chance", additional_chance);
        if (id != null)
            json.addProperty("id", id.toString());
        if (slime_size_min != 0)
            json.addProperty("slime_size_min", slime_size_min);
        if (slime_size_max != 0)
            json.addProperty("slime_size_max", slime_size_max);
        if (inverted != null)
            json.add("inverted", inverted.toJson());
        if (block_state_name != null)
            json.addProperty("block_state_name", block_state_name);
        if (block_state_value != null)
            json.addProperty("block_state_value", block_state_value);

        JsonHelper.addFloatArrayToJson(json, "fortune_chances", fortune_chances);

        JsonHelper.addArrayToJson(json, "alternatives", alternatives);
        return json;
    }
}
