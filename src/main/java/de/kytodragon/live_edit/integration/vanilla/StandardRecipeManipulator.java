package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.recipe.CraftTweakerUtils;
import de.kytodragon.live_edit.recipe.IRecipeManipulator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Collection;
import java.util.Optional;

public abstract class StandardRecipeManipulator <T extends Recipe<C>, C extends Container> extends IRecipeManipulator<T, MyRecipe, VanillaIntegration> {

    public net.minecraft.world.item.crafting.RecipeType<T> type;

    protected StandardRecipeManipulator(net.minecraft.world.item.crafting.RecipeType<T> type) {
        super(MyRecipe::fromJson);
        this.type = type;
    }

    @Override
    public ResourceLocation getKey(T recipe) {
        return recipe.getId();
    }

    @Override
    public Collection<T> getCurrentRecipes() {
        return integration.vanilla_recipe_manager.getAllRecipesFor(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<T> getRecipe(ResourceLocation key) {
        return (Optional<T>)integration.vanilla_recipe_manager.byKey(key);
    }

    @Override
    protected void exportDeleted(StringBuilder sb, ResourceLocation id) {
        CraftTweakerUtils.exportRecipeType(sb, my_type);
        sb.append(".removeByName(\"").append(id.toString()).append("\");");
    }
}
