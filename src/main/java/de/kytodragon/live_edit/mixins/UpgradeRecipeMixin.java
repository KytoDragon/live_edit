package de.kytodragon.live_edit.mixins;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingTransformRecipe.class)
public interface UpgradeRecipeMixin {

    @Accessor("base")
    Ingredient live_edit_mixin_getBase();

    @Accessor("addition")
    Ingredient live_edit_mixin_getAddition();
}
