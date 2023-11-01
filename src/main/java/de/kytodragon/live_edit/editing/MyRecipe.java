package de.kytodragon.live_edit.editing;

import com.google.gson.*;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MyRecipe {

    public static final Map<String, Function<JsonObject, MyIngredient>> ingredient_deserializers = new HashMap<>();
    public static final Map<String, Function<JsonObject, MyResult>> result_deserializers = new HashMap<>();

    public RecipeType type;
    public ResourceLocation id;
    public String group = "";

    public List<MyIngredient> ingredients;
    public boolean is_shaped = false;

    public List<MyResult> results;

    public static MyRecipe fromJsonString(String json) {
        return fromJson(JsonParser.parseString(json).getAsJsonObject());
    }

    public static MyRecipe fromJson(JsonObject json) {
        MyRecipe recipe = new MyRecipe();

        String type = GsonHelper.getAsString(json, "type");
        recipe.type = RecipeType.ALL_TYPES.get(type);

        String id = GsonHelper.getAsString(json, "id");
        recipe.id = ResourceLocation.of(id, ':');

        recipe.group = GsonHelper.getAsString(json, "group", "");

        JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients", null);
        if (ingredients != null) {
            recipe.ingredients = new ArrayList<>(ingredients.size());
            for (JsonElement elem : ingredients) {
                // since most ingredients and results are single items, this is a shortcut
                if (GsonHelper.isStringValue(elem)) {
                    ResourceLocation item_id = ResourceLocation.of(elem.getAsString(), ':');
                    Item item = ForgeRegistries.ITEMS.getValue(item_id);
                    recipe.ingredients.add(new MyIngredient.ItemIngredient(item));
                    continue;
                }

                if (!elem.isJsonObject())
                    throw new JsonSyntaxException("Expected ingredient, fround " + GsonHelper.getType(elem));

                JsonObject ingredient = elem.getAsJsonObject();
                String ingredient_type = GsonHelper.getAsString(ingredient, "type");
                Function<JsonObject, MyIngredient> deserializer = ingredient_deserializers.get(ingredient_type);
                if (deserializer == null)
                    throw new JsonSyntaxException("Unknown ingredient type " + ingredient_type);
                recipe.ingredients.add(deserializer.apply(ingredient));
            }
        }

        recipe.is_shaped = GsonHelper.getAsBoolean(json, "is_shaped", false);

        JsonArray results = GsonHelper.getAsJsonArray(json, "results", null);
        if (results != null) {
            recipe.results = new ArrayList<>(results.size());
            for (JsonElement elem : results) {
                // since most ingredients and results are single items, this is a shortcut
                if (GsonHelper.isStringValue(elem)) {
                    ResourceLocation item_id = ResourceLocation.of(elem.getAsString(), ':');
                    Item item = ForgeRegistries.ITEMS.getValue(item_id);
                    recipe.results.add(new MyResult.ItemResult(item));
                    continue;
                }

                if (!elem.isJsonObject())
                    throw new JsonSyntaxException("Expected recipe result, fround " + GsonHelper.getType(elem));

                JsonObject ingredient = elem.getAsJsonObject();
                String ingredient_type = GsonHelper.getAsString(ingredient, "type");
                Function<JsonObject, MyResult> deserializer = result_deserializers.get(ingredient_type);
                if (deserializer == null)
                    throw new JsonSyntaxException("Unknown recipe result type " + ingredient_type);
                recipe.results.add(deserializer.apply(ingredient));
            }
        }

        return recipe;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", type.name());
        json.addProperty("id", id.toString());
        if (!StringUtils.isEmpty(group))
            json.addProperty("group", group);
        if (ingredients != null) {
            JsonArray json_ingredients = new JsonArray(ingredients.size());
            for (MyIngredient ingredient : ingredients) {
                json_ingredients.add(ingredient.toJson());
            }
            json.add("ingredients", json_ingredients);
        }
        if (is_shaped)
            json.addProperty("is_shaped", Boolean.TRUE);
        if (results != null) {
            JsonArray json_result = new JsonArray(results.size());
            for (MyResult result : results) {
                json_result.add(result.toJson());
            }
            json.add("results", json_result);
        }

        return json;
    }

    public String toJsonString() {
        return new Gson().toJson(toJson());
    }
}
