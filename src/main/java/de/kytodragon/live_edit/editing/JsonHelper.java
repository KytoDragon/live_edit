package de.kytodragon.live_edit.editing;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class JsonHelper {

    public static ResourceLocation getResourceLocation(JsonObject json, String name) {
        return ResourceLocation.of(GsonHelper.getAsString(json, name), ':');
    }

    public static ResourceLocation getResourceLocation(JsonElement json) {
        return ResourceLocation.of(json.getAsString(), ':');
    }

    public static ResourceLocation getResourceLocationOrNull(JsonObject json, String name) {
        String id = GsonHelper.getAsString(json, name, null);
        if (id == null)
            return null;
        return ResourceLocation.of(id, ':');
    }

    public static Item getItem(JsonObject json, String name) {
        Item item = ForgeRegistries.ITEMS.getValue(getResourceLocation(json, name));
        Objects.requireNonNull(item);
        return item;
    }

    public static Item getItem(JsonElement json) {
        Item item = ForgeRegistries.ITEMS.getValue(getResourceLocation(json));
        Objects.requireNonNull(item);
        return item;
    }

    public static Fluid getFluid(JsonObject json, String name) {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(getResourceLocation(json, name));
        Objects.requireNonNull(fluid);
        return fluid;
    }

    public static TagKey<Item> getItemTag(JsonObject json, String name) {
        return TagKey.create(Registry.ITEM_REGISTRY, getResourceLocation(json, name));
    }

    public static TagKey<Fluid> getFluidTag(JsonObject json, String name) {
        return TagKey.create(Registry.FLUID_REGISTRY, getResourceLocation(json, name));
    }

    @Nullable
    public static CompoundTag getNBTTag(JsonObject json, String name) {
        JsonObject tag = GsonHelper.getAsJsonObject(json, name, null);
        if (tag == null)
            return null;

        try {
            String tag_string = new Gson().toJson(tag);
            return TagParser.parseTag(tag_string);
        } catch (CommandSyntaxException e) {
            throw new JsonSyntaxException("Failed to parse item nbt tag, reason: " + e.getMessage());
        }
    }

    public static <T extends IJsonProvider> void addArrayToJson(JsonObject json, String name, List<T> elements) {
        if (elements != null && !elements.isEmpty()) {
            JsonArray json_result = new JsonArray(elements.size());
            for (T element : elements) {
                json_result.add(element.toJson());
            }
            json.add(name, json_result);
        }
    }

    public static void addFloatArrayToJson(JsonObject json, String name, List<Float> elements) {
        if (elements != null && !elements.isEmpty()) {
            JsonArray json_result = new JsonArray(elements.size());
            for (float element : elements) {
                json_result.add(element);
            }
            json.add(name, json_result);
        }
    }

    public static void addResourceLocationsToJson(JsonObject json, String name, List<ResourceLocation> elements) {
        if (elements != null && !elements.isEmpty()) {
            JsonArray json_result = new JsonArray(elements.size());
            for (ResourceLocation element : elements) {
                json_result.add(element.toString());
            }
            json.add(name, json_result);
        }
    }

    public static <T> List<T> parseListFromJson(JsonObject json, String name, Function<JsonObject, T> deserializer) {

        JsonArray jsonlist = GsonHelper.getAsJsonArray(json, name, null);
        if (jsonlist == null)
            return null;

        List<T> result = new ArrayList<>(jsonlist.size());
        for (JsonElement elem : jsonlist) {
            if (!elem.isJsonObject())
                throw new JsonSyntaxException("Expected " + name + ", fround " + GsonHelper.getType(elem));

            result.add(deserializer.apply(elem.getAsJsonObject()));
        }
        return result;
    }

    public static <T> List<T> parseListFromJsonWithShortcut(JsonObject json, String name, Function<JsonElement, T> stringDeserializer, Function<JsonObject, T> deserializer) {

        JsonArray jsonlist = GsonHelper.getAsJsonArray(json, name, null);
        if (jsonlist == null)
            return null;

        List<T> result = new ArrayList<>(jsonlist.size());
        for (JsonElement elem : jsonlist) {

            if (GsonHelper.isStringValue(elem)) {
                result.add(stringDeserializer.apply(elem));
                continue;
            }

            if (!elem.isJsonObject())
                throw new JsonSyntaxException("Expected " + name + ", fround " + GsonHelper.getType(elem));

            result.add(deserializer.apply(elem.getAsJsonObject()));
        }
        return result;
    }

    public static <T> List<T> parsePrimitiveListFromJson(JsonObject json, String name, Function<JsonElement, T> deserializer) {

        JsonArray jsonlist = GsonHelper.getAsJsonArray(json, name, null);
        if (jsonlist == null)
            return null;

        List<T> result = new ArrayList<>(jsonlist.size());
        for (JsonElement elem : jsonlist) {
            result.add(deserializer.apply(elem));
        }
        return result;
    }
}
