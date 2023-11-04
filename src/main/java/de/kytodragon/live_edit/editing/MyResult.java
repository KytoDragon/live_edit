package de.kytodragon.live_edit.editing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public abstract class MyResult {

    public abstract JsonElement toJson();

    public static class ItemResult extends MyResult {
        public ItemStack item;
        public ItemResult(ItemStack item) { this.item = item; }
        public ItemResult(Item item) { this.item = new ItemStack(item); }

        @Override
        public JsonElement toJson() {
            ResourceLocation item_id = ForgeRegistries.ITEMS.getKey(item.getItem());
            Objects.requireNonNull(item_id);

            if (item.getCount() == 1 && item.getTag() == null) {
                return new JsonPrimitive(item_id.toString());
            }
            JsonObject json = new JsonObject();
            json.addProperty("type", "item");
            json.addProperty("item", item_id.toString());
            if (item.getCount() != 1)
                json.addProperty("amount", item.getCount());
            if (item.getTag() != null)
                json.add("nbt", JsonParser.parseString(item.getTag().getAsString()));
            return json;
        }

        public static ItemResult fromJson(JsonObject json) {
            Item item = JsonHelper.getItem(json, "item");
            int amount = GsonHelper.getAsInt(json, "amount", 1);
            CompoundTag tag = JsonHelper.getNBTTag(json, "nbt");
            ItemStack result = new ItemStack(item, amount);
            result.setTag(tag);
            return new ItemResult(result);
        }
    }

    public static class TimeResult extends MyResult {
        public int processing_time;
        public TimeResult(int processing_time) { this.processing_time = processing_time; }

        @Override
        public JsonElement toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("type", "time");
            json.addProperty("time", processing_time);
            return json;
        }

        public static TimeResult fromJson(JsonObject json) {
            int amount = GsonHelper.getAsInt(json, "time");
            return new TimeResult(amount);
        }
    }

    public static class ExperienceResult extends MyResult {
        public float experience;
        public ExperienceResult(float experience) { this.experience = experience; }

        @Override
        public JsonElement toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("type", "experience");
            json.addProperty("experience", experience);
            return json;
        }

        public static ExperienceResult fromJson(JsonObject json) {
            float experience = GsonHelper.getAsFloat(json, "experience");
            return new ExperienceResult(experience);
        }
    }

    public static class ChanceResult extends MyResult {
        public float output_chance;
        public ChanceResult(float output_chance) { this.output_chance = output_chance; }

        @Override
        public JsonElement toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("type", "chance");
            json.addProperty("chance", output_chance);
            return json;
        }

        public static ChanceResult fromJson(JsonObject json) {
            float chance = GsonHelper.getAsFloat(json, "chance");
            return new ChanceResult(chance);
        }
    }

    public static class TagResult extends MyResult {
        public TagKey<Item> tag;
        public int tag_amount = 1;
        public TagResult(TagKey<Item> tag) { this.tag = tag; }
        public TagResult(TagKey<Item> tag, int tag_amount) { this.tag = tag; this.tag_amount = tag_amount; }

        @Override
        public JsonElement toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("type", "tag");
            json.addProperty("tag", tag.location().toString());
            if (tag_amount != 1)
                json.addProperty("count", tag_amount);
            return json;
        }

        public static TagResult fromJson(JsonObject json) {
            TagKey<Item> tag = JsonHelper.getItemTag(json, "tag");
            int amount = GsonHelper.getAsInt(json, "amount", 1);
            return new TagResult(tag, amount);
        }
    }
}
