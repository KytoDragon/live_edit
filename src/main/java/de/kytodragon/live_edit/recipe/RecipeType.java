package de.kytodragon.live_edit.recipe;

public record RecipeType(String name, net.minecraft.world.item.crafting.RecipeType<?> vanilla_type) {

    public static final RecipeType CRAFTING = new RecipeType("CRAFTING", net.minecraft.world.item.crafting.RecipeType.CRAFTING);
    public static final RecipeType SMOKING = new RecipeType("SMOKING", net.minecraft.world.item.crafting.RecipeType.SMOKING);
    public static final RecipeType SMELTING = new RecipeType("SMELTING", net.minecraft.world.item.crafting.RecipeType.SMELTING);
    public static final RecipeType BLASTING = new RecipeType("BLASTING", net.minecraft.world.item.crafting.RecipeType.BLASTING);
    public static final RecipeType CAMPFIRE_COOKING = new RecipeType("CAMPFIRE_COOKING", net.minecraft.world.item.crafting.RecipeType.CAMPFIRE_COOKING);
    public static final RecipeType STONECUTTING = new RecipeType("STONECUTTING", net.minecraft.world.item.crafting.RecipeType.STONECUTTING);
    public static final RecipeType SMITHING = new RecipeType("SMITHING", net.minecraft.world.item.crafting.RecipeType.SMITHING);
}
