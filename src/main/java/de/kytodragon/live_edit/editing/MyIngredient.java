package de.kytodragon.live_edit.editing;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

public class MyIngredient {

    // List<ItemStack> item_list;
    // TagKey<Fluid> fluid_tag;
    // int custom_ingredient;
    // int custom_amount;

    public static class ItemIngredient extends MyIngredient {
        public ItemStack item;
        public ItemIngredient(ItemStack item) { this.item = item; }
        public ItemIngredient(Item item) { this.item = new ItemStack(item); }
    }

    public static class TagIngredient extends MyIngredient {
        public TagKey<Item> tag;
        public int tag_amount = 1;
        public TagIngredient(TagKey<Item> tag) { this.tag = tag; }
        public TagIngredient(TagKey<Item> tag, int tag_amount) { this.tag = tag; this.tag_amount = tag_amount; }
    }

    public static class FluidIngredient extends MyIngredient {
        public Fluid fluid;
        public int fluid_mb_amount;
        public FluidIngredient(Fluid fluid, int fluid_mb_amount) { this.fluid = fluid; this.fluid_mb_amount = fluid_mb_amount; }
    }

    public static class TimeIngredient extends MyIngredient {
        public int processing_time;
        public TimeIngredient(int processing_time) { this.processing_time = processing_time; }
    }
}
