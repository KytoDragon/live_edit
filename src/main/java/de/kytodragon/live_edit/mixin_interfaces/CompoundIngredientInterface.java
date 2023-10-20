package de.kytodragon.live_edit.mixin_interfaces;

import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public interface CompoundIngredientInterface {
    List<Ingredient> live_edit_mixin_getChildren();
}
