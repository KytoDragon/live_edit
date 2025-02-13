package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.recipe.IRecipeManipulator;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CompostManipulator extends IRecipeManipulator<CompostChance, MyRecipe, VanillaIntegration> {
    @Override
    public ResourceLocation getKey(CompostChance recipe) {
        return ForgeRegistries.ITEMS.getKey(recipe.item());
    }

    @Override
    public Collection<CompostChance> getCurrentRecipes() {
        return ComposterBlock.COMPOSTABLES.keySet().stream()
                .map(CompostManipulator::getCompostChance).toList();
    }

    private static CompostChance getCompostChance(ItemLike item) {
        return new CompostChance((Item) item, ComposterBlock.COMPOSTABLES.getOrDefault(item, 0));
    }

    @Override
    public Optional<CompostChance> getRecipe(ResourceLocation key) {
        Item item = ForgeRegistries.ITEMS.getValue(key);
        if (item != null) {
            float compostChance = ComposterBlock.COMPOSTABLES.getOrDefault(item, 0);
            if (compostChance > 0) {
                return Optional.of(new CompostChance(item, compostChance));
            }
        }
        return Optional.empty();
    }

    @Override
    public MyRecipe encodeRecipe(CompostChance recipe) {
        MyRecipe result = new MyRecipe();
        result.id = getKey(recipe);
        result.ingredients = List.of(new MyIngredient.ItemIngredient(recipe.item()));
        result.results = List.of(new MyResult.ChanceResult(recipe.compastChance()));
        result.type = RecipeType.COMPOSTING;
        return result;
    }
}
