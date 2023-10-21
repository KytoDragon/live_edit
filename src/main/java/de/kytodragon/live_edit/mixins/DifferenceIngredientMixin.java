package de.kytodragon.live_edit.mixins;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DifferenceIngredient.class)
public interface DifferenceIngredientMixin {

    @Accessor(value = "base", remap = false)
    Ingredient live_edit_mixin_getBase();

    @Accessor(value = "subtracted", remap = false)
    Ingredient live_edit_mixin_getSubtracted();
}
