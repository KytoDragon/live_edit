package de.kytodragon.live_edit.mixins;

import de.kytodragon.live_edit.mixin_interfaces.UpgradeRecipeInterface;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(UpgradeRecipe.class)
public class UpgradeRecipeMixin implements UpgradeRecipeInterface {

    @Final
    @Shadow
    Ingredient base;
    @Final
    @Shadow
    Ingredient addition;

    @Unique
    public Ingredient live_edit_mixin_getBase() {
        return base;
    }

    @Unique
    public Ingredient live_edit_mixin_getAddition() {
        return addition;
    }
}
