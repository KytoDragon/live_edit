package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.recipe.GeneralManipulationData;
import de.kytodragon.live_edit.recipe.IRecipeManipulator;
import de.kytodragon.live_edit.recipe.IngredientReplacer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.common.brewing.VanillaBrewingRecipe;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class BrewingRecipeManipulator extends IRecipeManipulator<ResourceLocation, IBrewingRecipe, VanillaIntegration> {

    private static final ResourceLocation vanilla_potions = new ResourceLocation("minecraft", "vanilla_potions");

    @Override
    public ResourceLocation getKey(IBrewingRecipe recipe) {
        if (recipe instanceof VanillaBrewingRecipe) {
            return vanilla_potions;
        } else if (recipe instanceof BrewingRecipe brew) {
            // TODO This is not unique. Creating a new name will be difficult, if the ingrediants are not a single item.
            return ForgeRegistries.ITEMS.getKey(brew.getOutput().getItem());
        }
        return null;
    }

    @Override
    public IBrewingRecipe manipulate(IBrewingRecipe recipe, GeneralManipulationData data) {
        if (recipe instanceof BrewingRecipe brew) {
            Ingredient input = brew.getInput();
            Ingredient additive = brew.getIngredient();
            ItemStack resultItem = brew.getOutput();
            boolean inputToReplace = IngredientReplacer.isToReplace(input, data);
            boolean additiveToReplace = IngredientReplacer.isToReplace(additive, data);
            boolean resultToReplace = IngredientReplacer.isToReplace(resultItem, data);
            if (inputToReplace)
                input = IngredientReplacer.replace(input, data);
            if (additiveToReplace)
                additive = IngredientReplacer.replace(additive, data);
            if (resultToReplace)
                resultItem = IngredientReplacer.replace(resultItem, data);
            if (inputToReplace || additiveToReplace || resultToReplace) {
                return new BrewingRecipe(input, additive, resultItem);
            }
        }
        return recipe;
    }

    @Override
    public Collection<IBrewingRecipe> getCurrentRecipes() {
        List<IBrewingRecipe> recipes = BrewingRecipeRegistry.getRecipes();

        recipes = recipes.stream().flatMap(recipe -> {
            if (recipe instanceof VanillaBrewingRecipe) {
                // Swap the vanilla placeholder with a list of single recipes.
                return getVanillaRepolacementREcipes().stream();
            } else {
                return Stream.of(recipe);
            }
        }).toList();
        return recipes;
    }

    @Override
    public Optional<IBrewingRecipe> getRecipe(ResourceLocation key) {
        return getCurrentRecipes().stream().filter(recipe -> key.equals(getKey(recipe))).findAny();
    }

    @Override
    public void prepareReload(Collection<IBrewingRecipe> recipes) {
        integration.addNewPotions(recipes);
    }

    /**
     * Get a list of brewing recipes that is equivalent to the vanilla potion recipe.
     */
    private static List<BrewingRecipe> getVanillaRepolacementREcipes() {
        // Get list of all vanilla potions
        List<Potion> all_potions = ForgeRegistries.POTIONS.getValues().stream()
            .filter(potion -> potion == Potions.WATER || PotionBrewing.isBrewablePotion(potion))
            .toList();

        // Go over all items
        return ForgeRegistries.ITEMS.getValues().stream()
            .map(Item::getDefaultInstance)
            // check if the item is a vanilla ingredient
            .filter(PotionBrewing::isIngredient)
            // for each potion
            .flatMap(item -> all_potions.stream()
                // map potion to actual items
                .flatMap(potion -> {
                    return Stream.of(PotionUtils.setPotion(new ItemStack(Items.POTION), potion),
                        PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion),
                        PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), potion));
                })
                // check if potion and ingredient are compatible
                .filter(potion -> PotionBrewing.hasMix(potion, item))
                // create recipe
                .map(potion -> {
                    ItemStack result = PotionBrewing.mix(item, potion);
                    return new BrewingRecipe(Ingredient.of(potion), Ingredient.of(item), result);
                })
            ).toList();
    }
}
