package de.kytodragon.live_edit.mixins;

import de.kytodragon.live_edit.mixin_interfaces.DifferenceIngredientInterface;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DifferenceIngredient.class)
public class DifferenceIngredientMixin implements DifferenceIngredientInterface {

    @Final
    @Shadow(remap = false)
    private Ingredient base;
    @Final
    @Shadow(remap = false)
    private Ingredient subtracted;

    @Unique
    public Ingredient live_edit_mixin_getBase() {
        return base;
    }

    @Unique
    public Ingredient live_edit_mixin_getSubtracted() {
        return subtracted;
    }
}
