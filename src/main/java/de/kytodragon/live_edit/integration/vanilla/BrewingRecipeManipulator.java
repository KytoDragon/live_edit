package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.recipe.IRecipeManipulator;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.common.brewing.VanillaBrewingRecipe;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Stream;

import static de.kytodragon.live_edit.recipe.IngredientEncoder.*;

public class BrewingRecipeManipulator extends IRecipeManipulator<IBrewingRecipe, MyRecipe, VanillaIntegration> {

    private static final ResourceLocation vanilla_potions = new ResourceLocation("minecraft", "vanilla_potions");

    protected BrewingRecipeManipulator() {
        super(MyRecipe::fromJson);
    }

    @Override
    public ResourceLocation getKey(IBrewingRecipe recipe) {
        if (recipe instanceof VanillaBrewingRecipe) {
            return vanilla_potions;
        } else if (recipe instanceof MyBrewingRecipe brew) {
            return brew.getKey();
        }
        return null;
    }

    @Override
    public Collection<IBrewingRecipe> getCurrentRecipes() {
        List<IBrewingRecipe> recipes = BrewingRecipeRegistry.getRecipes();

        recipes = recipes.stream().flatMap(recipe -> {
            if (recipe instanceof VanillaBrewingRecipe) {
                // Swap the vanilla placeholder with a list of single recipes.
                return getVanillaReplacementRecipes().stream();
            } else if (recipe instanceof MyBrewingRecipe) {
                return Stream.of(recipe);
            } else {
                // IBrewingRecipe does not have serializer so do not even touch recipes that we can't handle
                return Stream.empty();
            }
        }).toList();
        return recipes;
    }

    @Override
    public Optional<IBrewingRecipe> getRecipe(ResourceLocation key) {
        return getCurrentRecipes().stream().filter(recipe -> key.equals(getKey(recipe))).findAny();
    }

    @Override
    public MyRecipe encodeRecipe(IBrewingRecipe recipe) {
        if (recipe instanceof MyBrewingRecipe brew) {
            MyIngredient input = encodeIngredient(brew.getInput());
            MyIngredient additive = encodeIngredient(brew.getIngredient());
            if (input == null || additive == null) {
                return null;
            }

            MyRecipe result = new MyRecipe();
            result.id = brew.getKey();
            result.ingredients = List.of(input, additive);
            result.results = List.of(new MyResult.ItemResult(brew.getOutput()));
            result.type = RecipeType.BREWING;
            return result;
        }
        return null;
    }

    @Override
    protected void exportDeleted(StringBuilder sb, ResourceLocation id) {
        // TODO brewing.removeRecipe(<potion:minecraft:thick>, <item:minecraft:glowstone_dust>, <potion:minecraft:water>);
    }

    @Override
    protected void exportAdded(StringBuilder sb, MyRecipe recipe) {
        // TODO brewing.addRecipe(<item:minecraft:dirt>, <item:minecraft:apple>, <item:minecraft:arrow>);
    }

    /**
     * Get a list of brewing recipes that is equivalent to the vanilla potion recipe.
     */
    private static List<MyBrewingRecipe> getVanillaReplacementRecipes() {
        // Get list of all vanilla potion effects
        List<Potion> all_potions = ForgeRegistries.POTIONS.getValues().stream()
            .filter(potion -> potion == Potions.WATER || PotionBrewing.isBrewablePotion(potion))
            .toList();

        // All vanilla potion containers
        List<Item> containers = List.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);

        // Find all ingredients
        List<ItemStack> ingredients = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            ItemStack ingredient = item.getDefaultInstance();

            // check if the item is a vanilla ingredient
            if (PotionBrewing.isIngredient(ingredient)) {
                ingredients.add(ingredient);
            }
        }

        List<MyBrewingRecipe> result = new ArrayList<>();
        // Multiple recipes for the same potion will have a nummber at the end of the key
        Map<String, Integer> keyIndices = new HashMap<>();

        // for each potion
        for (Potion potion : all_potions) {

            // for each possible container
            for (Item container : containers) {

                // map potion to actual items
                ItemStack potion_item = PotionUtils.setPotion(new ItemStack(container), potion);

                for (ItemStack ingredient : ingredients) {

                    // check if potion and ingredient are compatible
                    if (!PotionBrewing.hasMix(potion_item, ingredient))
                        continue;

                    // create recipe
                    ItemStack result_potion = PotionBrewing.mix(ingredient, potion_item);

                    // create unique key
                    String keyPrefix;
                    if (result_potion.getItem() == Items.POTION) {
                        keyPrefix = "potion_";
                    } else if (result_potion.getItem() == Items.SPLASH_POTION) {
                        keyPrefix = "splash_";
                    } else if (result_potion.getItem() == Items.LINGERING_POTION) {
                        keyPrefix = "lingering_";
                    } else {
                        continue;
                    }
                    keyPrefix = keyPrefix + ResourceLocation.tryParse(result_potion.getTag().getString("Potion")).getPath();
                    String keyName = keyPrefix;

                    Integer index = keyIndices.get(keyPrefix);
                    if (index == null)
                        index = 1;
                    else {
                        index = index + 1;
                        keyName = keyPrefix + "_" + index;
                    }
                    keyIndices.put(keyPrefix, index);

                    ResourceLocation key = new ResourceLocation("minecraft", keyName);
                    result.add(new MyBrewingRecipe(key, Ingredient.of(potion_item), Ingredient.of(ingredient), result_potion));
                }
            }
        }
        return result;
    }
}
