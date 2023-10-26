package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.recipe.GeneralManipulationData;
import de.kytodragon.live_edit.recipe.IRecipeManipulator;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CompostManipulator extends IRecipeManipulator<ResourceLocation, CompostChance, VanillaIntegration> {
    @Override
    public ResourceLocation getKey(CompostChance recipe) {
        return ForgeRegistries.ITEMS.getKey(recipe.item());
    }

    @Override
    public CompostChance manipulate(CompostChance recipe, GeneralManipulationData data) {
        if (data.itemsToReplace.containsKey(recipe.item())) {
            recipe = new CompostChance(data.itemsToReplace.get(recipe.item()), recipe.compastChance());
        }
        return recipe;
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
    public void prepareReload(Collection<CompostChance> recipes) {
        integration.addNewCompostables(recipes);
    }

    @Override
    public MyRecipe encodeRecipe(CompostChance recipe) {
        MyRecipe result = new MyRecipe();
        result.id = getKey(recipe);
        result.ingredients = List.of(new MyIngredient.ItemIngredient(recipe.item()));
        result.result = List.of(new MyResult.ChanceResult(recipe.compastChance()));
        result.type = RecipeType.COMPOSTING;
        return result;
    }

    @Override
    public CompostChance decodeRecipe(MyRecipe recipe) {
        ItemStack result = ((MyResult.ItemResult)recipe.result.get(0)).item;
        float compost_chance = ((MyResult.ChanceResult)recipe.result.get(0)).output_chance;
        return new CompostChance(result.getItem(), compost_chance);
    }
}
