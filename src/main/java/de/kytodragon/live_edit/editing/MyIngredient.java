package de.kytodragon.live_edit.editing;

import com.google.gson.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class MyIngredient implements IJsonProvider {

    // TagKey<Fluid> fluid_tag;
    // int custom_ingredient;
    // int custom_amount;

    public abstract JsonElement toJson();
    public abstract void export(StringBuilder sb);

    public static class ItemIngredient extends MyIngredient {
        public ItemStack item;
        public ItemIngredient(ItemStack item) { this.item = item; }
        public ItemIngredient(Item item) { this.item = new ItemStack(item); }

        @Override
        public JsonElement toJson() {
            ResourceLocation item_id = ForgeRegistries.ITEMS.getKey(item.getItem());
            Objects.requireNonNull(item_id);

            if ((item.getCount() == 1 && item.getTag() == null) || item.getItem() == Items.AIR) {
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

        public static ItemIngredient fromJson(JsonObject json) {
            Item item = JsonHelper.getItem(json, "item");
            int amount = GsonHelper.getAsInt(json, "amount", 1);
            CompoundTag tag = JsonHelper.getNBTTag(json, "nbt");
            ItemStack result = new ItemStack(item, amount);
            result.setTag(tag);
            return new ItemIngredient(result);
        }

        public void export(StringBuilder sb) {
            ResourceLocation item_id = ForgeRegistries.ITEMS.getKey(item.getItem());
            Objects.requireNonNull(item_id);

            sb.append("<item:");
            sb.append(item_id);
            sb.append(">");
        }
    }

    public static class TagIngredient extends MyIngredient {
        public TagKey<Item> tag;
        public int tag_amount = 1;
        public TagIngredient(TagKey<Item> tag) { this.tag = tag; }
        public TagIngredient(TagKey<Item> tag, int tag_amount) { this.tag = tag; this.tag_amount = tag_amount; }

        @Override
        public JsonElement toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("type", "tag");
            json.addProperty("tag", tag.location().toString());
            if (tag_amount != 1)
                json.addProperty("amount", tag_amount);
            return json;
        }

        public static TagIngredient fromJson(JsonObject json) {
            TagKey<Item> tag = JsonHelper.getItemTag(json, "tag");
            int amount = GsonHelper.getAsInt(json, "amount", 1);
            return new TagIngredient(tag, amount);
        }

        public void export(StringBuilder sb) {
            sb.append("<tag:");
            sb.append(tag.location());
            sb.append(">");
        }
    }

    public static class FluidIngredient extends MyIngredient {
        public Fluid fluid;
        public int fluid_mb_amount;
        public FluidIngredient(Fluid fluid, int fluid_mb_amount) { this.fluid = fluid; this.fluid_mb_amount = fluid_mb_amount; }

        @Override
        public JsonElement toJson() {
            ResourceLocation fluid_id = ForgeRegistries.FLUIDS.getKey(fluid);
            Objects.requireNonNull(fluid_id);

            JsonObject json = new JsonObject();
            json.addProperty("type", "fluid");
            json.addProperty("fluid", fluid_id.toString());
            json.addProperty("amount", fluid_mb_amount);
            return json;
        }

        public static FluidIngredient fromJson(JsonObject json) {
            Fluid fluid = JsonHelper.getFluid(json, "fluid");
            int amount = GsonHelper.getAsInt(json, "amount");
            return new FluidIngredient(fluid, amount);
        }

        public void export(StringBuilder sb) {
            ResourceLocation fluid_id = ForgeRegistries.FLUIDS.getKey(fluid);
            Objects.requireNonNull(fluid_id);

            sb.append("<fluid:");
            sb.append(fluid_id);
            sb.append(">");
        }
    }

    public static class TimeIngredient extends MyIngredient {
        public int processing_time;
        public TimeIngredient(int processing_time) { this.processing_time = processing_time; }

        @Override
        public JsonElement toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("type", "time");
            json.addProperty("time", processing_time);
            return json;
        }

        public static TimeIngredient fromJson(JsonObject json) {
            int time = GsonHelper.getAsInt(json, "time");
            return new TimeIngredient(time);
        }

        public void export(StringBuilder sb) {
            sb.append(processing_time);
        }
    }

    public static class ItemListIngredient extends MyIngredient {
        public List<ItemStack> item_list = new ArrayList<>();

        @Override
        public JsonElement toJson() {

            JsonObject json = new JsonObject();
            json.addProperty("type", "item_list");
            JsonArray list = new JsonArray();

            for (ItemStack item : item_list) {
                ResourceLocation item_id = ForgeRegistries.ITEMS.getKey(item.getItem());
                Objects.requireNonNull(item_id);
                json.addProperty("item", item_id.toString());
            }
            json.add("item_list", list);
            return json;
        }

        public static ItemListIngredient fromJson(JsonObject json) {
            ItemListIngredient result = new ItemListIngredient();
            for (JsonElement elem : json.getAsJsonArray("item_list")) {
                Item item = JsonHelper.getItem(elem);
                result.item_list.add(item.getDefaultInstance());
            }

            return result;
        }

        public void export(StringBuilder sb) {
            boolean first = true;
            for (ItemStack item : item_list) {
                if (!first)
                    sb.append(" | ");
                first = false;

                ResourceLocation item_id = ForgeRegistries.ITEMS.getKey(item.getItem());
                Objects.requireNonNull(item_id);

                sb.append("<item:");
                sb.append(item_id);
                sb.append(">");
            }
        }
    }
}
