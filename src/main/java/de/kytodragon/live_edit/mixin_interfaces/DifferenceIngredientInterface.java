package de.kytodragon.live_edit.mixin_interfaces;

import net.minecraft.world.item.crafting.Ingredient;

public interface DifferenceIngredientInterface {
    Ingredient live_edit_mixin_getBase();
    Ingredient live_edit_mixin_getSubtracted();
}
