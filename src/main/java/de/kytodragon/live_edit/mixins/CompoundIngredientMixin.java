package de.kytodragon.live_edit.mixins;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CompoundIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(CompoundIngredient.class)
public interface CompoundIngredientMixin {

    @Accessor(value = "children",remap = false)
    List<Ingredient> live_edit_mixin_getChildren();
}
