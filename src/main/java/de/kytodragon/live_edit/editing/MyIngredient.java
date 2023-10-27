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

import java.util.Objects;

public abstract class MyIngredient {

    // List<ItemStack> item_list;
    // TagKey<Fluid> fluid_tag;
    // int custom_ingredient;
    // int custom_amount;

    public abstract JsonElement toJson();

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
            return new ItemIngredient(new ItemStack(item, amount, tag));
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
    }
}
