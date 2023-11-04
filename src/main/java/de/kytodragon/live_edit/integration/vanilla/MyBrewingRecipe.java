package de.kytodragon.live_edit.integration.vanilla;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;

public class MyBrewingRecipe extends BrewingRecipe {

    private final ResourceLocation key;

    public MyBrewingRecipe(ResourceLocation key, Ingredient input, Ingredient ingredient, ItemStack output) {
        super(input, ingredient, output);
        this.key = key;
    }

    public ResourceLocation getKey() {
        return key;
    }
}
