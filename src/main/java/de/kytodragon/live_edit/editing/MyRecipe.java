package de.kytodragon.live_edit.editing;

import com.google.gson.*;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MyRecipe implements IRecipe {

    public static final Map<String, Function<JsonObject, MyIngredient>> ingredient_deserializers = new HashMap<>();
    public static final Map<String, Function<JsonObject, MyResult>> result_deserializers = new HashMap<>();

    public RecipeType type;
    public ResourceLocation id;
    public String group = "";

    public List<MyIngredient> ingredients;
    public int shaped_width = 0;

    public List<MyResult> results;

    public static MyRecipe fromJsonString(String json) {
        return fromJson(JsonParser.parseString(json).getAsJsonObject());
    }

    public static MyRecipe fromJson(JsonObject json) {
        MyRecipe recipe = new MyRecipe();

        String type = GsonHelper.getAsString(json, "type");
        recipe.type = RecipeType.ALL_TYPES.get(type);

        recipe.id = JsonHelper.getResourceLocation(json, "id");

        recipe.group = GsonHelper.getAsString(json, "group", "");

        recipe.ingredients = JsonHelper.parseListFromJsonWithShortcut(json, "ingredients", MyRecipe::getSimpleIngredient, MyRecipe::getComplexIngredient);

        recipe.shaped_width = GsonHelper.getAsInt(json, "shaped_width", 0);

        recipe.results = JsonHelper.parseListFromJsonWithShortcut(json, "results", MyRecipe::getSimpleResult, MyRecipe::getComplexResult);

        return recipe;
    }

    private static MyIngredient.ItemIngredient getSimpleIngredient(JsonElement elem) {
        // since most ingredients and results are single items, this is a shortcut
        ResourceLocation item_id = ResourceLocation.of(elem.getAsString(), ':');
        Item item = ForgeRegistries.ITEMS.getValue(item_id);
        return new MyIngredient.ItemIngredient(item);
    }

    private static MyIngredient getComplexIngredient(JsonObject ingredient) {
        String ingredient_type = GsonHelper.getAsString(ingredient, "type");
        Function<JsonObject, MyIngredient> deserializer = ingredient_deserializers.get(ingredient_type);
        if (deserializer == null)
            throw new JsonSyntaxException("Unknown ingredient type " + ingredient_type);

        return deserializer.apply(ingredient);
    }

    private static MyResult.ItemResult getSimpleResult(JsonElement elem) {
        // since most ingredients and results are single items, this is a shortcut
        ResourceLocation item_id = ResourceLocation.of(elem.getAsString(), ':');
        Item item = ForgeRegistries.ITEMS.getValue(item_id);
        return new MyResult.ItemResult(item);
    }

    private static MyResult getComplexResult(JsonObject ingredient) {
        String ingredient_type = GsonHelper.getAsString(ingredient, "type");
        Function<JsonObject, MyResult> deserializer = result_deserializers.get(ingredient_type);
        if (deserializer == null)
            throw new JsonSyntaxException("Unknown recipe result type " + ingredient_type);

        return deserializer.apply(ingredient);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", type.name());
        json.addProperty("id", id.toString());
        if (!StringUtils.isEmpty(group))
            json.addProperty("group", group);
        JsonHelper.addArrayToJson(json, "ingredients", ingredients);
        if (shaped_width > 0)
            json.addProperty("shaped_width", shaped_width);
        JsonHelper.addArrayToJson(json, "results", results);

        return json;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public boolean containsItem(Item item) {

        if (results != null) {
            for (MyResult result : results) {
                if (result instanceof MyResult.ItemResult itemResult) {
                    if (itemResult.item.getItem() == item) {
                        return true;
                    }
                }
            }
        }
        if (ingredients != null) {
            for (MyIngredient ingredient : ingredients) {
                if (ingredient instanceof MyIngredient.ItemIngredient itemIngredient) {
                    if (itemIngredient.item.getItem() == item) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String toJsonString() {
        return new Gson().toJson(toJson());
    }
}
