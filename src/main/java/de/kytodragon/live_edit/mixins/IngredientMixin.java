package de.kytodragon.live_edit.mixins;

import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Ingredient.class)
public interface IngredientMixin {

    @Accessor("values")
    Ingredient.Value[] live_edit_mixin_getRawIngrediants();
}
