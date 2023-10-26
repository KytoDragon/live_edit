package de.kytodragon.live_edit.editing;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class MyResult {

    public static class ItemResult extends MyResult {
        public ItemStack item;
        public ItemResult(ItemStack item) { this.item = item; }
        public ItemResult(Item item) { this.item = new ItemStack(item); }
    }

    public static class AmountResult extends MyResult {
        public int output_amount;
        public AmountResult(int output_amount) { this.output_amount = output_amount; }
    }

    public static class ExperienceResult extends MyResult {
        public float experience;
        public ExperienceResult(float experience) { this.experience = experience; }
    }

    public static class ChanceResult extends MyResult {
        public float output_chance;
        public ChanceResult(float output_chance) { this.output_chance = output_chance; }
    }

    public static class TagResult extends MyResult {
        public TagKey<Item> tag;
        public int tag_amount = 1;
        public TagResult(TagKey<Item> tag) { this.tag = tag; }
        public TagResult(TagKey<Item> tag, int tag_amount) { this.tag = tag; this.tag_amount = tag_amount; }
    }
}
