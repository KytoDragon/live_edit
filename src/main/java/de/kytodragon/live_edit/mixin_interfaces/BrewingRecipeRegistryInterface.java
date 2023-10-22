package de.kytodragon.live_edit.mixin_interfaces;

import net.minecraftforge.common.brewing.IBrewingRecipe;

import java.util.List;

public interface BrewingRecipeRegistryInterface {

    void live_edit_mixin_setRecipes(List<IBrewingRecipe> new_recipes);
}
