package de.kytodragon.live_edit.mixins;

import de.kytodragon.live_edit.mixin_interfaces.IngredientInterface;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Ingredient.class)
public class IngredientMixin implements IngredientInterface {

    @Final
    @Shadow
    private Ingredient.Value[] values;

    @Unique
    public Ingredient.Value[] live_edit_mixin_getRawIngrediants() {
        return values;
    }
}
