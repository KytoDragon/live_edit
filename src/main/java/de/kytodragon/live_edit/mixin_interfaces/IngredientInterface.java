package de.kytodragon.live_edit.mixin_interfaces;

import net.minecraft.world.item.crafting.Ingredient;

public interface IngredientInterface {
    Ingredient.Value[] live_edit_mixin_getRawIngrediants();
}
