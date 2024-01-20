package de.kytodragon.live_edit.mixins;

import de.kytodragon.live_edit.mixin_interfaces.BrewingRecipeRegistryInterface;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(BrewingRecipeRegistry.class)
public class BrewingRecipeRegistryMixin implements BrewingRecipeRegistryInterface {

    @Shadow(remap = false)
    private static List<IBrewingRecipe> recipes;

    public void live_edit_mixin_setRecipes(List<IBrewingRecipe> new_recipes) {
        recipes.clear();
        recipes.addAll(new_recipes);
    }
}
