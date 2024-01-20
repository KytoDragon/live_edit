package de.kytodragon.live_edit.mixins;

import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(BrewingRecipeRegistry.class)
public interface BrewingRecipeRegistryMixin {

    @Accessor("recipes")
    static List<IBrewingRecipe> live_edit_mixin_getRecipes() {
        return null;
    }
}
