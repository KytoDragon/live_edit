package de.kytodragon.live_edit.editing;

import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class MyRecipe {

    public RecipeType type;
    public ResourceLocation id;
    public String group;

    public List<MyIngredient> ingredients;
    public boolean is_shaped = false;

    public List<MyResult> result;
}
