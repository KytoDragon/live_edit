package de.kytodragon.live_edit.mixins;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IntersectionIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(IntersectionIngredient.class)
public interface IntersectionIngredientMixin {

    @Accessor(value = "children", remap = false)
    List<Ingredient> live_edit_mixin_getChildren();
}
