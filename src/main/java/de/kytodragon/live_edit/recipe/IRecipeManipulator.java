package de.kytodragon.live_edit.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.kytodragon.live_edit.editing.IRecipe;
import de.kytodragon.live_edit.integration.Integration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Function;

public abstract class IRecipeManipulator<R, C extends IRecipe, I extends Integration> {

    protected RecipeType my_type;
    protected I integration;
    protected Function<JsonObject, C> json_parser;

    private final HashMap<ResourceLocation, C> recipes_to_add = new HashMap<>();
    private final HashSet<ResourceLocation> recipes_to_delete = new HashSet<>();

    public abstract ResourceLocation getKey(R recipe);

    public abstract Collection<R> getCurrentRecipes();

    public abstract Optional<R> getRecipe(ResourceLocation key);

    public abstract C encodeRecipe(R recipe);

    public boolean isRealImplementation() {
        return true;
    }

    public void setIntegration(I integration) {
        this.integration = integration;
    }
    public void setRecipeType(RecipeType my_type) {
        this.my_type = my_type;
    }
    public RecipeType getRecipeType() {
        return this.my_type;
    }

    public void markRecipeForDeletion(ResourceLocation recipeKey) {
        if (recipes_to_add.remove(recipeKey) == null)
            recipes_to_delete.add(recipeKey);
    }

    public void markRecipeForAddition(ResourceLocation recipeKey, JsonObject recipe) {
        recipes_to_add.put(recipeKey, json_parser.apply(recipe));
    }

    public void shutdownServer() {
        recipes_to_delete.clear();
        recipes_to_add.clear();
    }

    public void saveRecipes(Path data_path) throws UncheckedIOException {
        Path deleted_file = data_path.resolve(my_type.name() + "_deleted.json");
        JsonArray deleted_list = new JsonArray();
        recipes_to_delete.forEach(k -> {
            deleted_list.add(k.toString());
        });
        jsonToFile(deleted_file, deleted_list);

        Path added_file = data_path.resolve(my_type.name() + "_added.json");
        JsonArray added_list = new JsonArray();
        recipes_to_add.forEach((k, v) -> {
            added_list.add(v.toJson());
        });
        jsonToFile(added_file, added_list);
    }

    private void jsonToFile(Path path, JsonArray json) throws UncheckedIOException {
        try {
            if (json.isEmpty()) {
                Files.deleteIfExists(path);
            } else {
                JsonObject root = new JsonObject();
                root.add("data", json);
                Files.writeString(path, json.toString(), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void loadRecipes(Path data_path) throws UncheckedIOException, JsonParseException {
        Path deleted_file = data_path.resolve(my_type.name() + "_deleted.json");
        JsonArray deleted_list = fileToJson(deleted_file);
        deleted_list.forEach(elem -> {
            recipes_to_delete.add(ResourceLocation.of(elem.getAsString(), ':'));
        });

        Path added_file = data_path.resolve(my_type.name() + "_added.json");
        JsonArray added_list = fileToJson(added_file);
        added_list.forEach(elem -> {
            C recipe = json_parser.apply(elem.getAsJsonObject());
            recipes_to_add.put(recipe.getId(), recipe);
        });
    }

    private JsonArray fileToJson(Path path) throws UncheckedIOException, JsonParseException {
        try {
            if (Files.exists(path)) {
                String content = Files.readString(path, StandardCharsets.UTF_8);
                JsonObject root = GsonHelper.parse(content);
                return root.getAsJsonArray("data");
            } else {
                return new JsonArray();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
